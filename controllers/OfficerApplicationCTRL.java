package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.BTOApplication;
import models.BTOProject;
import models.OfficerApplication;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import models.User;
import models.repositories.ApplicationCSVRepository;
import models.repositories.BTOProjectCSVRepository;
import models.repositories.OfficerApplicationCSVRepository;
import models.enumerations.*;

public class OfficerApplicationCTRL {

    private List<OfficerApplication> officerApplicationList;
    private List<BTOApplication> btoApplicationList;
    private List<BTOProject> projects;
    private User currentUser;
    private OfficerApplicationCSVRepository officerRepo;
    private ApplicationCSVRepository appRepo;
    private BTOProjectCSVRepository projRepo;

    /**
     * Load all applications and projects, keep track of the logged-in user
     */

    public Role getCurrentUserRole() {
        return currentUser.getRole();
    }

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
     * View all officer applications submitted by current user
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
     * Register as an officer for a project if eligible
     * 
     * @param projectId The project to register for
     * @return true if registration was successful, false otherwise
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
                        a.getProjectID() == projectId);

        if (alreadyRegistered) {
            System.out.println("You've already registered as an officer for this project.");
            return false;
        }

        // Check if already approved as officer for another project with overlapping
        // dates
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
     * Get all pending officer applications for projects managed by current user
     */
    public List<OfficerApplication> getPendingOfficerApplicationsForManager() {
        List<Integer> managedProjectIds = projects.stream()
                .filter(p -> p.getManager().equals(currentUser.getNRIC()))
                .map(BTOProject::getProjectID)
                .collect(Collectors.toList());

        return officerApplicationList.stream()
                .filter(app -> managedProjectIds.contains(app.getProjectID()) &&
                        app.getStatus() == RegistrationStatus.PENDING)
                .collect(Collectors.toList());
    }

    /**
     * Process an officer application decision (approve/reject)
     * 
     * @param applicationId ID of the application to process
     * @param decision      "A" for approve, "R" for reject
     * @return true if successful, false otherwise
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
        if (!project.getManager().equals(currentUser.getNRIC())) {
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
     * Check if current user is an approved officer for a project
     * 
     * @param projectId Project to check
     * @return true if user is an approved officer, false otherwise
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
     * Check if dates overlap between two projects
     */
    private boolean datesOverlap(BTOProject p1, BTOProject p2) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        LocalDate p1Start = LocalDate.parse(p1.getApplicationOpeningDate(), formatter);
        LocalDate p1End = LocalDate.parse(p1.getApplicationClosingDate(), formatter);
        LocalDate p2Start = LocalDate.parse(p2.getApplicationOpeningDate(), formatter);
        LocalDate p2End = LocalDate.parse(p2.getApplicationClosingDate(), formatter);

        // No overlap if one project ends before the other starts
        return !(p1End.isBefore(p2Start) || p2End.isBefore(p1Start));
    }

    /**
     * Get the next available officer application ID
     */
    private int getNextOfficerApplicationID() {
        return officerApplicationList.stream()
                .mapToInt(OfficerApplication::getOfficerApplicationId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Get all projects user can apply to be an officer for
     */
    /**
     * Get all projects user can apply to be an officer for
     */
    public List<BTOProject> getEligibleOfficerProjects() {
        return projects.stream()
                .filter(p -> p.isVisibility() && p.getAvailableOfficerSlots() > 0)
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

                    // Officer can apply again only if all previous applications were rejected
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
                                    a.getProjectID() != p.getProjectID()) // Exclude current project
                            .noneMatch(a -> {
                                // Find the project for this pending application
                                BTOProject pendingProject = projects.stream()
                                        .filter(proj -> proj.getProjectID() == a.getProjectID())
                                        .findFirst()
                                        .orElse(null);

                                return pendingProject != null && datesOverlap(pendingProject, p);
                            });

                    return notAppliedAsApplicant && canApplyToProject && noApprovedOverlaps && noPendingOverlaps;
                })
                .collect(Collectors.toList());
    }

    public List<OfficerApplication> getPendingAndSuccessfullOfficerApplicationsForManager() {
        List<Integer> managedProjectIds = projects.stream()
                .filter(p -> p.getManager().equals(currentUser.getNRIC()))
                .map(BTOProject::getProjectID)
                .collect(Collectors.toList());

        return officerApplicationList.stream()
                .filter(app -> managedProjectIds.contains(app.getProjectID()) &&
                        (app.getStatus() == RegistrationStatus.PENDING ||
                                app.getStatus() == RegistrationStatus.APPROVED))
                .collect(Collectors.toList());
    }

}
