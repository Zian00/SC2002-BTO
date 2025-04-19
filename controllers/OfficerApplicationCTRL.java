package controllers;

import boundaries.OfficerApplicationView;
import entity.BTOApplication;
import entity.BTOProject;
import entity.OfficerApplication;
import entity.User;
import entity.enumerations.*;
import entity.repositories.ApplicationCSVRepository;
import entity.repositories.BTOProjectCSVRepository;
import entity.repositories.OfficerApplicationCSVRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller class for managing officer applications in the BTO system.
 * <p>
 * Handles officer registration for BTO projects, application approval/rejection,
 * eligibility checks, and provides officer application-related menus for both
 * officers and managers.
 * </p>
 */
public class OfficerApplicationCTRL {

    /** List of all officer applications loaded from the repository. */
    private List<OfficerApplication> officerApplicationList;
    /** List of all BTO applications loaded from the repository. */
    private List<BTOApplication> btoApplicationList;
    /** List of all BTO projects loaded from the repository. */
    private List<BTOProject> projects;
    /** The currently logged-in user. */
    private User currentUser;
    /** Repository for officer applications. */
    private OfficerApplicationCSVRepository officerRepo;
    /** Repository for BTO applications. */
    private ApplicationCSVRepository appRepo;
    /** Repository for BTO projects. */
    private BTOProjectCSVRepository projRepo;

    /**
     * Gets the role of the current user.
     * @return The {@link Role} of the current user.
     */
    public Role getCurrentUserRole() {
        return currentUser.getRole();
    }

    /**
     * Constructs a new OfficerApplicationCTRL and loads all relevant data.
     * @param currentUser The currently logged-in user.
     */
    public OfficerApplicationCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.officerRepo = new OfficerApplicationCSVRepository();
        this.appRepo = new ApplicationCSVRepository();
        this.projRepo = new BTOProjectCSVRepository();

        // Load all data
        this.officerApplicationList = officerRepo.readOfficerApplicationsFromCSV();
        this.btoApplicationList = appRepo.readApplicationFromCSV();
        this.projects = projRepo.readBTOProjectFromCSV();
    }

    /**
     * Runs the officer application menu for the current user, displaying options
     * and routing to the appropriate actions based on the user's role (officer or manager).
     *
     * @param sc Scanner for user input.
     * @param offAppCTRL This controller instance.
     * @param view The OfficerApplicationView for displaying menus and results.
     * @param projectCTRL The BTOProjectCTRL for accessing project data.
     */
    public void runOfficerApplicationMenu(Scanner sc,
            OfficerApplicationCTRL offAppCTRL,
            OfficerApplicationView view,
            BTOProjectCTRL projectCTRL) {
        // Determine user role
        Role userRole = offAppCTRL.getCurrentUserRole(); // Assuming User class has a getRole() method

        switch (userRole) {
            case HDBOFFICER -> {
                // Officer role menu
                while (true) {
                    view.displayOfficerMenu(); // 1-4 + Back
                    String choice = sc.nextLine().trim();
                    switch (choice) {
                        case "1" -> {
                            // 1) View all *your* officer applications
                            var mine = offAppCTRL.viewUserOfficerApplications();
                            view.displayOfficerApplications(mine);
                        }
                        case "2" -> {
                            // 2) Check registration status (i.e. show only PENDING if you like)
                            var mine = offAppCTRL.viewUserOfficerApplications()
                                    .stream()
                                    .filter(a -> a.getStatus() == RegistrationStatus.PENDING
                                            || a.getStatus() == RegistrationStatus.APPROVED)
                                    .collect(Collectors.toList());
                            view.displayOfficerApplications(mine);
                        }
                        case "3" -> {
                            return; // Back to the officer's main menu
                        }
                        default -> System.out.println("Invalid choice, try again.");
                    }
                }
            }
            case HDBMANAGER -> {
                // Manager role menu
                while (true) {
                    view.displayManagerMenu(); // 1-3 + Back
                    String choice = sc.nextLine().trim();
                    switch (choice) {
                        case "1" -> {
                            // 1) Display All Applications Handled By You
                            var pendingApps = offAppCTRL.getPendingAndSuccessfullOfficerApplicationsForManager();
                            view.displayManagerPendingApplications(pendingApps, projectCTRL.getAllProjects());

                        }
                        case "2" -> {
                            // 2) Approval / Rejection for Application
                            var pendingApps = offAppCTRL.getPendingOfficerApplicationsForManager();
                            if (pendingApps.isEmpty()) {
                                System.out.println("No pending applications to process.");
                                continue;
                            }
                            view.displayManagerPendingApplications(pendingApps, projectCTRL.getAllProjects());

                            System.out.print("Enter Application ID to process: ");
                            int appId = Integer.parseInt(sc.nextLine().trim());
                            System.out.print("Enter decision (A for Approve, R for Reject): ");
                            String decision = sc.nextLine().trim();

                            boolean success = offAppCTRL.processOfficerApplicationDecision(appId, decision);
                            System.out.println(success
                                    ? "Application processed successfully."
                                    : "Failed to process application.");
                        }

                        case "3" -> {
                            return; // Back to the manager's main menu
                        }
                        default -> System.out.println("Invalid choice, try again.");
                    }
                }
            }
            default -> {
                System.out.println("You don't have permission to access this menu.");
            }

        }
    }

    /**
     * Returns all officer applications submitted by the current user,
     * with project information attached.
     * @return List of {@link OfficerApplication} objects for the current user.
     */
    public List<OfficerApplication> viewUserOfficerApplications() {
        List<OfficerApplication> userApplications = officerApplicationList.stream()
                .filter(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()))
                .collect(Collectors.toList());

        // Enhance each application with project information
        for (OfficerApplication app : userApplications) {
            // Find the corresponding project
            BTOProject project = projects.stream()
                    .filter(p -> p.getProjectID() == app.getProjectID())
                    .findFirst()
                    .orElse(null);

            if (project != null) {
                // Set additional project information in the application
                app.setProjectName(project.getProjectName());
                app.setProjectLocation(project.getNeighborhood());

            }
        }

        return userApplications;
    }

    /**
     * Registers the current user as an officer for a given project if eligible.
     *
     * @param projectId The project to register for.
     * @return true if registration was successful, false otherwise.
     */
    public boolean registerAsOfficer(int projectId) {
        // Find the project
        BTOProject project = projects.stream()
                .filter(p -> p.getProjectID() == projectId)
                .findFirst()
                .orElse(null);

        if (project == null) {
            System.out.println("Project not found.");
            return false;
        }

        // Check if already applied for this project as applicant
        boolean hasAppliedAsApplicant = btoApplicationList.stream()
                .anyMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()) &&
                        a.getProjectID() == projectId);
        
        if (hasAppliedAsApplicant) {
            System.out.println("Cannot register as officer for a project you've applied to as an applicant.");
            return false;
        }
        
        // Check if already registered for this project as officer
        boolean alreadyRegistered = officerApplicationList.stream()
                .anyMatch(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) &&
                        a.getProjectID() == projectId &&
                        a.getStatus() != RegistrationStatus.REJECTED); // Allow reapplication if rejected
        
        if (alreadyRegistered) {
            System.out.println("You've already registered as an officer for this project.");
            return false;
        }
        
        // Check if already approved as officer for another project with overlapping dates
        boolean isOfficerElsewhere = false;
        for (BTOProject p : projects) {
            if (p.getApprovedOfficer() != null &&
                    p.getApprovedOfficer().contains(currentUser.getNRIC()) &&
                    datesOverlap(p, project)) {
                isOfficerElsewhere = true;
                break;
            }
        }

        if (isOfficerElsewhere) {
            System.out.println("Already an officer for another project with overlapping application period.");
            return false;
        }

        // Check if there are available slots
        if (project.getAvailableOfficerSlots() <= 0) {
            System.out.println("No available officer slots for this project.");
            return false;
        }

        // Create new officer application
        OfficerApplication app = new OfficerApplication();
        app.setOfficerApplicationId(getNextOfficerApplicationID());
        app.setOfficerNRIC(currentUser.getNRIC());
        app.setProjectID(projectId);
        app.setStatus(RegistrationStatus.PENDING);

        // Add to pending list in project
        List<String> pendingOfficers = project.getPendingOfficer();
        if (pendingOfficers == null) {
            pendingOfficers = new ArrayList<>();
        }
        pendingOfficers.add(currentUser.getNRIC());
        project.setPendingOfficer(pendingOfficers);

        // Save changes
        officerApplicationList.add(app);
        officerRepo.writeOfficerApplicationsToCSV(officerApplicationList);
        projRepo.writeBTOProjectToCSV(projects);

        return true;
    }

    /**
     * Gets all pending officer applications for projects managed by the current user.
     * @return List of pending {@link OfficerApplication} objects.
     */
    public List<OfficerApplication> getPendingOfficerApplicationsForManager() {
        List<Integer> managedProjectIds = projects.stream()
                .filter(p -> p.getManagerID().equals(currentUser.getNRIC()))
                .map(BTOProject::getProjectID)
                .collect(Collectors.toList());

        return officerApplicationList.stream()
                .filter(app -> managedProjectIds.contains(app.getProjectID()) &&
                        app.getStatus() == RegistrationStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Processes an officer application decision (approve or reject) for a manager.
     *
     * @param applicationId ID of the application to process.
     * @param decision "A" for approve, "R" for reject.
     * @return true if successful, false otherwise.
     */
    public boolean processOfficerApplicationDecision(int applicationId, String decision) {
        // Find the application
        Optional<OfficerApplication> appOpt = officerApplicationList.stream()
                .filter(a -> a.getOfficerApplicationId() == applicationId &&
                        a.getStatus() == RegistrationStatus.PENDING)
                .findFirst();

        if (appOpt.isEmpty()) {
            System.out.println("Application not found or not pending.");
            return false;
        }

        OfficerApplication app = appOpt.get();

        // Find the project
        BTOProject project = projects.stream()
                .filter(p -> p.getProjectID() == app.getProjectID())
                .findFirst()
                .orElse(null);

        if (project == null) {
            System.out.println("Project not found.");
            return false;
        }

        // Verify this manager manages this project
        if (!project.getManagerID().equals(currentUser.getNRIC())) {
            System.out.println("You do not manage this project.");
            return false;
        }

        // Check if officer has date conflicts with other approved projects
        boolean hasDateConflicts = false;
        List<BTOProject> conflictingProjects = new ArrayList<>();

        for (BTOProject p : projects) {
            if (p.getProjectID() != project.getProjectID() && // Different project
                    p.getApprovedOfficer() != null &&
                    p.getApprovedOfficer().contains(app.getOfficerNRIC()) && // Officer approved in this project
                    datesOverlap(p, project)) { // Dates overlap
                hasDateConflicts = true;
                conflictingProjects.add(p);
            }
        }

        if (decision.equalsIgnoreCase("A")) {
            // If there are conflicts and trying to approve, only allow rejection
            if (hasDateConflicts) {
                System.out.println("Cannot approve this application. Officer already has commitments for:");
                for (BTOProject p : conflictingProjects) {
                    System.out.println("- Project: " + p.getProjectName() +
                            " (" + p.getApplicationOpeningDate() + " to " +
                            p.getApplicationClosingDate() + ")");
                }
                System.out.println("Please reject this application instead.");
                return false;
            }

            // Update application status
            app.setStatus(RegistrationStatus.APPROVED);

            // Update project officer lists
            List<String> pendingOfficers = project.getPendingOfficer();
            if (pendingOfficers != null) {
                pendingOfficers.remove(app.getOfficerNRIC());
            }

            List<String> approvedOfficers = project.getApprovedOfficer();
            if (approvedOfficers == null) {
                approvedOfficers = new ArrayList<>();
            }
            approvedOfficers.add(app.getOfficerNRIC());

            project.setPendingOfficer(pendingOfficers);
            project.setApprovedOfficer(approvedOfficers);
            // Decrement available officer slots
            project.setAvailableOfficerSlots(project.getAvailableOfficerSlots() - 1);

        } else if (decision.equalsIgnoreCase("R")) {
            // Reject application - always allowed, even with no conflicts

            // Update application status
            app.setStatus(RegistrationStatus.REJECTED);

            // Remove from pending list
            List<String> pendingOfficers = project.getPendingOfficer();
            if (pendingOfficers != null) {
                pendingOfficers.remove(app.getOfficerNRIC());
                project.setPendingOfficer(pendingOfficers);
            }

        } else {
            System.out.println("Invalid decision. Use 'A' to approve or 'R' to reject.");
            return false;
        }

        // Save changes
        officerRepo.writeOfficerApplicationsToCSV(officerApplicationList);
        projRepo.writeBTOProjectToCSV(projects);

        return true;
    }

    /**
     * Checks if the current user is an approved officer for a given project.
     *
     * @param projectId Project to check.
     * @return true if user is an approved officer, false otherwise.
     */
    public boolean isApprovedOfficerForProject(int projectId) {
        BTOProject project = projects.stream()
                .filter(p -> p.getProjectID() == projectId)
                .findFirst()
                .orElse(null);

        if (project == null || project.getApprovedOfficer() == null) {
            return false;
        }

        return project.getApprovedOfficer().contains(currentUser.getNRIC());
    }

    /**
     * Checks if the application periods of two projects overlap.
     *
     * @param p1 The first project.
     * @param p2 The second project.
     * @return true if the periods overlap, false otherwise.
     */
    private boolean datesOverlap(BTOProject p1, BTOProject p2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        LocalDate p1Start = LocalDate.parse(p1.getApplicationOpeningDate(), formatter);
        LocalDate p1End = LocalDate.parse(p1.getApplicationClosingDate(), formatter);
        LocalDate p2Start = LocalDate.parse(p2.getApplicationOpeningDate(), formatter);
        LocalDate p2End = LocalDate.parse(p2.getApplicationClosingDate(), formatter);

        // No overlap if one project ends before the other starts
        return !(p1End.isBefore(p2Start) || p2End.isBefore(p1Start));
    }

    /**
     * Gets the next available officer application ID.
     * @return The next officer application ID as an integer.
     */
    private int getNextOfficerApplicationID() {
        return officerApplicationList.stream()
                .mapToInt(OfficerApplication::getOfficerApplicationId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Gets all projects the user can apply to be an officer for, based on eligibility.
     * @return List of eligible {@link BTOProject} objects.
     */
    public List<BTOProject> getEligibleOfficerProjects() {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    LocalDate currentDate = LocalDate.now();

    return projects.stream()
            .filter(p -> {
                LocalDate closingDate = LocalDate.parse(p.getApplicationClosingDate(), formatter);
                boolean stillOpen = !closingDate.isBefore(currentDate);
                boolean hasSlots = p.getAvailableOfficerSlots() > 0;
                return stillOpen && hasSlots;
            })
            .filter(p -> {
                // Not already applied for this project as applicant
                boolean notAppliedAsApplicant = btoApplicationList.stream()
                        .noneMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()) &&
                                a.getProjectID() == p.getProjectID());

                // Check if officer has any pending or approved applications for this project
                List<OfficerApplication> userApplicationsForThisProject = officerApplicationList.stream()
                        .filter(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) &&
                                a.getProjectID() == p.getProjectID())
                        .collect(Collectors.toList());

                boolean canApplyToProject = userApplicationsForThisProject.isEmpty() ||
                        userApplicationsForThisProject.stream()
                                .allMatch(a -> a.getStatus() == RegistrationStatus.REJECTED);

                // Check for overlapping commitments with approved applications
                boolean noApprovedOverlaps = projects.stream()
                        .filter(other -> other.getApprovedOfficer() != null &&
                                other.getApprovedOfficer().contains(currentUser.getNRIC()))
                        .noneMatch(other -> datesOverlap(other, p));

                // Check for overlapping commitments with pending applications
                boolean noPendingOverlaps = officerApplicationList.stream()
                        .filter(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) &&
                                a.getStatus() == RegistrationStatus.PENDING &&
                                a.getProjectID() != p.getProjectID())
                        .noneMatch(a -> {
                            BTOProject pendingProject = projects.stream()
                                    .filter(proj -> proj.getProjectID() == a.getProjectID())
                                    .findFirst()
                                    .orElse(null);
                            return pendingProject != null && datesOverlap(pendingProject, p);
                        });

                return notAppliedAsApplicant && canApplyToProject && noApprovedOverlaps && noPendingOverlaps;
            })
            .collect(Collectors.toList());


    // public List<BTOProject> getEligibleOfficerProjects() {
    //     DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    //     LocalDate currentDate = LocalDate.now();
    //     return projects.stream()
    //             .filter(p -> {
    //                 // Parse the closing date of the project
    //                 LocalDate closingDate = LocalDate.parse(p.getApplicationClosingDate(), formatter);
    //                 // Check if the closing date is not before the current date
    //                 return !closingDate.isBefore(currentDate);
    //             } && p.getAvailableOfficerSlots() > 0)
    //             .filter(p -> {
    //                 // Not already applied for this project as applicant
    //                 boolean notAppliedAsApplicant = btoApplicationList.stream()
    //                         .noneMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()) &&
    //                                 a.getProjectID() == p.getProjectID());

    //                 // Check if officer has any pending or approved applications for this project
    //                 List<OfficerApplication> userApplicationsForThisProject = officerApplicationList.stream()
    //                         .filter(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) &&
    //                                 a.getProjectID() == p.getProjectID())
    //                         .collect(Collectors.toList());

    //                 // Officer can apply again only if all previous applications were rejected
    //                 boolean canApplyToProject = userApplicationsForThisProject.isEmpty() ||
    //                         userApplicationsForThisProject.stream()
    //                                 .allMatch(a -> a.getStatus() == RegistrationStatus.REJECTED);

    //                 // Check for overlapping commitments with approved applications
    //                 boolean noApprovedOverlaps = projects.stream()
    //                         .filter(other -> other.getApprovedOfficer() != null &&
    //                                 other.getApprovedOfficer().contains(currentUser.getNRIC()))
    //                         .noneMatch(other -> datesOverlap(other, p));

    //                 // Check for overlapping commitments with pending applications
    //                 boolean noPendingOverlaps = officerApplicationList.stream()
    //                         .filter(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) &&
    //                                 a.getStatus() == RegistrationStatus.PENDING &&
    //                                 a.getProjectID() != p.getProjectID()) // Exclude current project
    //                         .noneMatch(a -> {
    //                             // Find the project for this pending application
    //                             BTOProject pendingProject = projects.stream()
    //                                     .filter(proj -> proj.getProjectID() == a.getProjectID())
    //                                     .findFirst()
    //                                     .orElse(null);

    //                             return pendingProject != null && datesOverlap(pendingProject, p);
    //                         });
    //                 // if (!notAppliedAsApplicant)
    //                 // System.out.println("Filtered out: already applicant for " +
    //                 // p.getProjectID());
    //                 // if (!canApplyToProject)
    //                 // System.out.println("Filtered out: already officer for " + p.getProjectID());
    //                 // if (!noApprovedOverlaps)
    //                 // System.out.println("Filtered out: overlapping approved for " +
    //                 // p.getProjectID());
    //                 // if (!noPendingOverlaps)
    //                 // System.out.println("Filtered out: overlapping pending for " +
    //                 // p.getProjectID());
    //                 return notAppliedAsApplicant && canApplyToProject && noApprovedOverlaps && noPendingOverlaps;
    //             })
    //             .collect(Collectors.toList());
    }

    /**
     * Gets all pending and approved officer applications for projects managed by the current user.
     * @return List of {@link OfficerApplication} objects.
     */
    public List<OfficerApplication> getPendingAndSuccessfullOfficerApplicationsForManager() {
        List<Integer> managedProjectIds = projects.stream()
                .filter(p -> p.getManagerID().equals(currentUser.getNRIC()))
                .map(BTOProject::getProjectID)
                .collect(Collectors.toList());

        return officerApplicationList.stream()
                .filter(app -> managedProjectIds.contains(app.getProjectID()) &&
                        (app.getStatus() == RegistrationStatus.PENDING ||
                                app.getStatus() == RegistrationStatus.APPROVED))
                .collect(Collectors.toList());
    }

}
