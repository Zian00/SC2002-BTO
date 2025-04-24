package controllers;

import boundaries.BTOApplicationView;
import boundaries.BTOProjectView;
import boundaries.EnquiryView;
import boundaries.OfficerApplicationView;
import entity.BTOProject;
import entity.Enquiry;
import entity.FilterSettings;
import entity.HDBManager;
import entity.User;
import entity.enumerations.FlatType;
import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import entity.repositories.BTOProjectCSVRepository;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller class for managing BTO projects in the BTO system.
 * <p>
 * Handles project creation, editing, deletion, filtering, and visibility
 * updates. Provides role-based project menus and supports applicant, officer,
 * and manager flows.
 * </p>
 */
public class BTOProjectCTRL {

    /**
     * List of all BTO projects loaded from the repository.
     */
    private List<BTOProject> projects;
    /**
     * The currently logged-in user.
     */
    private User currentUser;
    /**
     * Repository for reading/writing BTO project data.
     */
    private BTOProjectCSVRepository repo = new BTOProjectCSVRepository();

    /**
     * Constructs a BTOProjectCTRL for the given user and loads project data.
     *
     * @param currentUser The currently logged-in user.
     */
    public BTOProjectCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.projects = repo.readBTOProjectFromCSV();
    }

    // --------------------------------------------------------------------------------------------------
    // Projects Menu for Users
    // --------------------------------------------------------------------------------------------------
    /**
     * Runs the project menu for the current user, displaying options and
     * routing to the appropriate actions based on the user's role (applicant,
     * officer, manager).
     *
     * @param sc Scanner for user input.
     * @param userCTRL The UserCTRL instance.
     * @param projectCTRL This BTOProjectCTRL instance.
     * @param projectView The BTOProjectView for displaying menus and results.
     * @param applicationCTRL The BTOApplicationCTRL for handling applications.
     * @param officerAppCTRL The OfficerApplicationCTRL for officer
     * applications.
     * @param officerAppView The OfficerApplicationView for officer application
     * UI.
     * @param enquiryView The EnquiryView for enquiry UI.
     * @param enquiryCTRL The EnquiryCTRL for handling enquiries.
     */
    public void runProjectMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
            BTOProjectView projectView, BTOApplicationCTRL applicationCTRL,
            OfficerApplicationCTRL officerAppCTRL, OfficerApplicationView officerAppView,
            EnquiryView enquiryView, EnquiryCTRL enquiryCTRL, BTOApplicationView btoApplicationView) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            switch (role) {
                case APPLICANT ->
                    projectView.displayApplicantMenu();
                case HDBOFFICER ->
                    projectView.displayOfficerMenu();
                case HDBMANAGER ->
                    projectView.displayManagerMenu();
            }
            String c = sc.nextLine().trim();
            // Update project visibility before filtering/displaying projects
            projectCTRL.updateProjectVisibility();
            switch (role) {
                case APPLICANT -> {
                    var availableProjects = projectCTRL.getFilteredProjects();
                    switch (c) {
                        case "1" -> { // Only display projects User can apply
                            var eligible = projectCTRL.getFilteredProjects();
                            // 2) use the NO‑FILTER view so it doesnt re-apply filter
                            projectView.displayAvailableForApplicantNoFilter(
                                    userCTRL.getCurrentUser(), eligible);
                        }
                        case "2" -> { // display all bto projects by filter
                            // 1) prompt & save
                            FilterSettings fs = projectView.promptFilterSettings(userCTRL.getCurrentUser(), sc);
                            projectCTRL.updateUserFilterSettings(userCTRL.getCurrentUser(), fs);
                            userCTRL.saveUserData();
                            // convert to the single CSV string
                            String filterCsv = fs.toCsv();

                            // store it on the user
                            userCTRL.updateFilterSettings(userCTRL.getCurrentUser(), filterCsv);

                            // feedback
                            System.out.println("Your filters have been saved: " + filterCsv);

                            // 2) re‑fetch & display
                            var filtered = projectCTRL.getFilteredProjectsForUser(userCTRL.getCurrentUser());
                            projectView.displayAvailableForApplicant(userCTRL.getCurrentUser(), filtered);
                        }
                        case "3" -> { // Apply for BTO
                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);

                            // Get project selection
                            try {
                                System.out.print("Enter project ID to apply: ");
                                int projectId = Integer.parseInt(sc.nextLine());

                                // Get flat type selection
                                System.out.println("Select flat type:");
                                System.out.println("1. 2-Room");
                                System.out.println("2. 3-Room");
                                System.out.print("I want: ");
                                int flatChoice = Integer.parseInt(sc.nextLine());
                                FlatType flatType = (flatChoice == 1) ? FlatType.TWOROOM : FlatType.THREEROOM;

                                // Submit application
                                boolean ok = applicationCTRL.apply(projectId, flatType);
                                if (ok) {
                                    System.out.println("Application submitted! Status: PENDING.");
                                }
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please try again.");
                            } catch (Exception e) {
                                System.out.println("An error occurred: " + e.getMessage());
                            }
                        }
                        case "4" -> { // Submit Enquiry for a project
                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);

                            // Get project selection
                            System.out.print("Enter project ID to submit Enquiry: ");
                            int projectId;
                            try {
                                projectId = Integer.parseInt(sc.nextLine().trim());
                            } catch (NumberFormatException e) {
                                projectView.showMessage("Invalid project ID.");
                                break;
                            }

                            // Ensure projectId is in availableProjects
                            boolean valid = availableProjects.stream()
                                    .anyMatch(p -> p.getProjectID() == projectId);
                            if (!valid) {
                                projectView.showMessage("You can only submit enquiries for projects shown to you.");
                                break;
                            }

                            String enquiryText = enquiryView.promptEnquiryCreation(sc);
                            Enquiry newEnquiry = enquiryCTRL.createEnquiry(projectId, enquiryText);
                            enquiryView.displayEnquiry(newEnquiry);
                        }

                        case "5" -> { // Display Past BTO Applications
                            try {
                                // 1. Get all applications for the current user
                                var pastApplications = applicationCTRL.viewUserApplications();
                                // 2. Get all projects for lookup
                                var allProjects = projectCTRL.getAllProjects();
                                // 3. Call the new view method to display them
                                btoApplicationView.displayPastUserApplications(pastApplications, allProjects);
                            } catch (Exception e) {
                                System.out.println("An error occurred while retrieving past applications: " + e.getMessage());
                                // Optionally log the stack trace for debugging
                                // e.printStackTrace();
                            }

                        }
                        case "6" -> { // back to central menu
                            return;
                        }
                    }
                }
                case HDBOFFICER -> {
                    var availableProjects = projectCTRL.getFilteredProjects();
                    OUTER:
                    switch (c) {
                        case "1" -> { // Display All BTO Projects (ignore officer assignment and visibility)
                            var allProjects = projectCTRL.getAllProjects();
                            projectView.displayAllProject(allProjects);
                        }
                        case "2" -> { // Display all bto projects by filter
                            // 1) prompt & save
                            FilterSettings fs = projectView.promptFilterSettings(userCTRL.getCurrentUser(), sc);
                            projectCTRL.updateUserFilterSettings(userCTRL.getCurrentUser(), fs);
                            userCTRL.saveUserData();
                            // convert to the single CSV string
                            String filterCsv = fs.toCsv();

                            // store it on the user
                            userCTRL.updateFilterSettings(userCTRL.getCurrentUser(), filterCsv);

                            // feedback
                            System.out.println("Your filters have been saved: " + filterCsv);

                            // 2) re‑fetch & display
                            var filtered = projectCTRL.getFilteredProjectsForUser(userCTRL.getCurrentUser());
                            projectView.displayAvailableForApplicant(userCTRL.getCurrentUser(), filtered);
                        }
                        case "3" -> { // Apply for a BTO Project
                            try {
                                String officerNRIC = userCTRL.getCurrentUser().getNRIC();
                                var ms = userCTRL.getCurrentUser().getMaritalStatus();
                                int age = userCTRL.getCurrentUser().getAge();
                                // Only show visible projects, eligible by age/marital status, and not handled
                                // by this officer
                                var eligibleProjects = projectCTRL.getAllProjects().stream()
                                        .filter(BTOProject::isVisibility)
                                        .filter(p -> {
                                            var pending = p.getPendingOfficer();
                                            var approved = p.getApprovedOfficer();
                                            boolean notPending = pending == null || pending.stream()
                                                    .noneMatch(nric -> nric.equalsIgnoreCase(officerNRIC));
                                            boolean notApproved = approved == null || approved.stream()
                                                    .noneMatch(nric -> nric.equalsIgnoreCase(officerNRIC));
                                            boolean flatEligible = false;
                                            if (ms == MaritalState.SINGLE && age >= 35) {
                                                flatEligible = p.getAvailable2Room() > 0;
                                            } else if (ms == MaritalState.MARRIED && age >= 21) {
                                                flatEligible = p.getAvailable2Room() > 0 || p.getAvailable3Room() > 0;
                                            }
                                            return notPending && notApproved && flatEligible;
                                        })
                                        .collect(Collectors.toList());
                                if (eligibleProjects.isEmpty()) {
                                    projectView.showMessage("No eligible BTO projects available for application.");
                                    break;
                                }
                                projectView.displayEligibleProjectsForOfficer(eligibleProjects, ms, age);
                                System.out.print("Enter project ID to apply: ");
                                int projectId;
                                try {
                                    projectId = Integer.parseInt(sc.nextLine().trim());
                                } catch (NumberFormatException e) {
                                    projectView.showMessage("Invalid project ID.");
                                    break;
                                }
                                var selected = eligibleProjects.stream()
                                        .filter(p -> p.getProjectID() == projectId)
                                        .findFirst();
                                if (selected.isEmpty()) {
                                    projectView.showMessage("Selected project is not eligible for application.");
                                    break;
                                }
                                FlatType flatType = null;
                                if (ms == MaritalState.SINGLE && age >= 35) {
                                    System.out.println("Select flat type:");
                                    System.out.println("1. 2-Room");
                                    System.out.print("I want: ");
                                    int flatChoice;
                                    try {
                                        flatChoice = Integer.parseInt(sc.nextLine().trim());
                                    } catch (NumberFormatException e) {
                                        projectView.showMessage("Invalid flat type choice.");
                                        break;
                                    }
                                    if (flatChoice == 1) {
                                        flatType = FlatType.TWOROOM;
                                    } else {
                                        projectView.showMessage("Invalid flat type choice for your marital status.");
                                        break;
                                    }
                                } else if (ms == MaritalState.MARRIED && age >= 21) {
                                    System.out.println("Select flat type:");
                                    System.out.println("1. 2-Room");
                                    System.out.println("2. 3-Room");
                                    System.out.print("I want: ");
                                    int flatChoice;
                                    try {
                                        flatChoice = Integer.parseInt(sc.nextLine().trim());
                                    } catch (NumberFormatException e) {
                                        projectView.showMessage("Invalid flat type choice.");
                                        break;
                                    }
                                    switch (flatChoice) {
                                        case 1 ->
                                            flatType = FlatType.TWOROOM;
                                        case 2 ->
                                            flatType = FlatType.THREEROOM;
                                        default -> {
                                            projectView.showMessage("Invalid flat type choice.");
                                            break OUTER;
                                        }
                                    }
                                } else {
                                    projectView.showMessage("You are not eligible to apply for any flat type.");
                                    break;
                                }
                                boolean ok = applicationCTRL.apply(projectId, flatType);
                                if (ok) {
                                    projectView.showMessage("Application submitted! Status: PENDING.");
                                }
                            } catch (Exception e) {
                                projectView.showMessage(
                                        "An error occurred while applying for a BTO project: " + e.getMessage());
                            }
                        }
                        case "4" -> { // Submit Enquiry for a BTO project (officer acts as applicant)
                            // Show available projects (use officer's filtered projects)
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);

                            // Get project selection
                            System.out.print("Enter project ID to submit Enquiry: ");
                            int projectId;
                            try {
                                projectId = Integer.parseInt(sc.nextLine().trim());
                            } catch (NumberFormatException e) {
                                projectView.showMessage("Invalid project ID.");
                                break;
                            }

                            // Ensure projectId is in availableProjects
                            boolean valid = availableProjects.stream()
                                    .anyMatch(p -> p.getProjectID() == projectId);
                            if (!valid) {
                                projectView.showMessage("You can only submit enquiries for projects shown to you.");
                                break;
                            }

                            String enquiryText = enquiryView.promptEnquiryCreation(sc);
                            Enquiry newEnquiry = enquiryCTRL.createEnquiry(projectId, enquiryText);
                            enquiryView.displayEnquiry(newEnquiry);
                        }
                        case "5" -> { // Register as officer

                            var elig = officerAppCTRL.getEligibleOfficerProjects();
                            officerAppView.displayEligibleProjects(elig);

                            if (elig.isEmpty()) {
                                System.out.println("No eligible projects to register for.");
                                break;
                            }

                            System.out.print("Enter Project ID to register: ");
                            try {
                                int pid = Integer.parseInt(sc.nextLine().trim());

                                // Check if input PID is in the list of eligible project IDs
                                boolean isValidProject = elig.stream().anyMatch(p -> p.getProjectID() == pid);

                                if (!isValidProject) {
                                    System.out.println("Invalid Project ID. Returning to menu.");
                                    break;
                                }

                                boolean ok = officerAppCTRL.registerAsOfficer(pid);
                                System.out.println(ok
                                        ? "Registration submitted (status PENDING)."
                                        : "Registration failed.");
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter a valid number. Returning to menu.");
                            }
                        }
                        case "6" -> { // Display BTO Projects I'm handling
                            try {
                                var handledProjects = projectCTRL.getHandledProjects();
                                if (handledProjects.isEmpty()) {
                                    System.out.println("You are not handling any BTO projects.");
                                } else {
                                    projectView.displayAllProject(handledProjects);
                                }
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while displaying handled projects: " + e.getMessage());
                            }
                        }
                        case "7" -> {
                            return; // back to central menu
                        }
                        default ->
                            System.out.println("Invalid choice, try again.");
                    }
                }
                case HDBMANAGER -> {
                    switch (c) {
                        case "1" -> { // Display All BTO Projects
                            var allProjects = projectCTRL.getAllProjects();
                            projectView.displayAllProject(allProjects);
                        }
                        case "2" -> { // Display Filtered All BTO Projects
                            // 1) prompt & save
                            FilterSettings fs = projectView.promptFilterSettings(userCTRL.getCurrentUser(), sc);
                            projectCTRL.updateUserFilterSettings(userCTRL.getCurrentUser(), fs);
                            userCTRL.saveUserData();
                            // convert to the single CSV string
                            String filterCsv = fs.toCsv();

                            // store it on the user
                            userCTRL.updateFilterSettings(userCTRL.getCurrentUser(), filterCsv);

                            // feedback
                            System.out.println("Your filters have been saved: " + filterCsv);

                            // 2) re‑fetch & display
                            var filtered = projectCTRL.getFilteredProjectsForManager(userCTRL.getCurrentUser());

                            projectView.displayFilteredProject(userCTRL.getCurrentUser(), filtered);
                        }
                        case "3" -> { // display all Manager bto projects by filter
                            // 1) prompt & save
                            FilterSettings fs = projectView.promptFilterSettings(userCTRL.getCurrentUser(), sc);
                            projectCTRL.updateUserFilterSettings(userCTRL.getCurrentUser(), fs);
                            userCTRL.saveUserData();
                            // convert to the single CSV string
                            String filterCsv = fs.toCsv();

                            // store it on the user
                            userCTRL.updateFilterSettings(userCTRL.getCurrentUser(), filterCsv);

                            // feedback
                            System.out.println("Your filters have been saved: " + filterCsv);

                            // 2) re‑fetch & display
                            var filtered = projectCTRL.getFilteredProjectsCreatedbyCurrentManager(userCTRL.getCurrentUser());

                            projectView.displayFilteredProject(userCTRL.getCurrentUser(), filtered);
                        }
                        case "4" -> { // Manager views his own projects

                            var allProjects = projectCTRL.getAllProjects();
                            var managerNRIC = userCTRL.getCurrentUser().getNRIC();
                            var myProjects = allProjects.stream()
                                    .filter(project -> project.getManagerID().equals(managerNRIC))
                                    .toList();
                            if (myProjects.isEmpty()) {
                                projectView.showMessage("No projects found for you.");
                            } else {
                                projectView.displayManagerProjects(myProjects);
                            }
                        }

                        case "5" -> { // Add BTO Project
                            BTOProject newProj = projectView.promptNewProject(sc);
                            // automatically set projectID
                            int id = projectCTRL.getNextProjectID();
                            newProj.setProjectID(id);
                            newProj.setManagerID(userCTRL.getCurrentUser().getNRIC());

                            // --- Overlap check: prevent overlapping application periods for same manager
                            var managerNRIC = userCTRL.getCurrentUser().getNRIC();
                            var myProjects = projectCTRL.getAllProjects().stream()
                                    .filter(project -> project.getManagerID().equals(managerNRIC))
                                    .toList();

                            // Parse dates for new project
                            LocalDate newOpen = LocalDate.parse(newProj.getApplicationOpeningDate());
                            LocalDate newClose = LocalDate.parse(newProj.getApplicationClosingDate());

                            boolean overlaps = myProjects.stream().anyMatch(existing -> {
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                                LocalDate existOpen = LocalDate.parse(existing.getApplicationOpeningDate(), formatter);
                                LocalDate existClose = LocalDate.parse(existing.getApplicationClosingDate(), formatter);

                                // not (existClose < newOpen || newClose < existOpen)
                                return !(existClose.isBefore(newOpen) || newClose.isBefore(existOpen));
                            });

                            if (overlaps) {
                                projectView.showMessage(
                                        "Error: The new project's application period overlaps with an existing project you manage. Please choose a different period.");
                                break;
                            }

                            projectCTRL.createProject(newProj);
                            projectView.showMessage("Project created.");
                        }
                        case "6" -> { // Edit BTO Project
                            int id = projectView.promptProjectID(sc);
                            BTOProject existing = projectCTRL.getProjectById(id);
                            if (existing == null) {
                                projectView.showMessage("Project not found.");
                                break;
                            }
                            // prevent manager from editing other people projects - bryan
                            String mgrNRIC = userCTRL.getCurrentUser().getNRIC();
                            if (!existing.getManagerID().equals(mgrNRIC)) {
                                projectView.showMessage("Error: You do not manage that project.");
                                break;
                            }
                            // Store old dates for comparison
                            String oldOpen = existing.getApplicationOpeningDate();
                            String oldClose = existing.getApplicationClosingDate();

                            // Let manager edit details (including dates)
                            projectView.editProjectDetails(sc, existing);

                            // If application period changed, check for overlap
                            String newOpen = existing.getApplicationOpeningDate();
                            String newClose = existing.getApplicationClosingDate();

                            if (!oldOpen.equals(newOpen) || !oldClose.equals(newClose)) {
                                var managerNRIC = userCTRL.getCurrentUser().getNRIC();
                                var myProjects = projectCTRL.getAllProjects().stream()
                                        .filter(project -> project.getManagerID().equals(managerNRIC)
                                        && project.getProjectID() != id)
                                        .toList();

                                boolean overlaps = myProjects.stream().anyMatch(other -> {
                                    String existOpen = other.getApplicationOpeningDate();
                                    String existClose = other.getApplicationClosingDate();
                                    // not (existClose < newOpen || newClose < existOpen)
                                    return !(existClose.compareTo(newOpen) < 0 || newClose.compareTo(existOpen) < 0);
                                });

                                if (overlaps) {
                                    projectView.showMessage(
                                            "Error: The new application period overlaps with another project you manage. Edit cancelled.");
                                    // Revert to old dates
                                    existing.setApplicationOpeningDate(oldOpen);
                                    existing.setApplicationClosingDate(oldClose);
                                    break;
                                }
                            }

                            projectCTRL.editProject(id, existing);
                            projectView.showMessage("Project updated.");
                        }
                        case "7" -> { // Delete BTO Project
                            // display a list of projects and ID for reference
                            var allProjects = projectCTRL.getAllProjects();
                            projectView.displayProjectIdNameList(allProjects);
                            // which ID to delete
                            int id = projectView.promptProjectID(sc);
                            if (projectCTRL.deleteProject(id)) {
                                projectView.showMessage("Project deleted.");
                            } else {
                                projectView.showMessage("Project not found.");
                            }
                        }
                        case "8" -> { // back to central menu
                            return;
                        }

                        default ->
                            System.out.println("Invalid choice, try again.");
                    }
                }

            }
        }
    }

    /**
     * For manager: get any project by ID.
     *
     * @param id The project ID.
     * @return The {@link BTOProject} with the specified ID, or null if not
     * found.
     */
    public BTOProject getProjectById(int id) {
        // If current user is a manager, use their method (for possible future logic)
        if (currentUser instanceof HDBManager manager) {
            return manager.getProjectById(projects, id);
        }
        // Otherwise, just search the list
        return projects.stream()
                .filter(p -> p.getProjectID() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns the next available project ID (max existing ID + 1).
     *
     * @return The next project ID.
     */
    public int getNextProjectID() {
        return projects.stream()
                .mapToInt(BTOProject::getProjectID)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Creates a new BTO project and persists it.
     *
     * @param p The new {@link BTOProject} to add.
     */
    public void createProject(BTOProject p) {
        projects.add(p);
        repo.writeBTOProjectToCSV(projects);
    }

    /**
     * Edits an existing project by replacing its fields with those from the
     * updated object. Only managers can edit projects.
     *
     * @param projectId The ID of the project to edit.
     * @param updated The updated {@link BTOProject} object.
     * @return true if the project was updated, false otherwise.
     */
    public boolean editProject(int projectId, BTOProject updated) {
        BTOProject existing = null;
        // only manager can edit project
        // only manager can edit project
        if (currentUser instanceof HDBManager manager) {
            existing = manager.getProjectById(projects, projectId);
        }
        if (existing == null) {
            return false;
        }

        existing.setProjectName(updated.getProjectName());
        existing.setNeighborhood(updated.getNeighborhood());
        existing.setAvailable2Room(updated.getAvailable2Room());
        existing.setTwoRoomPrice(updated.getTwoRoomPrice());
        existing.setAvailable3Room(updated.getAvailable3Room());
        existing.setThreeRoomPrice(updated.getThreeRoomPrice());
        existing.setApplicationOpeningDate(updated.getApplicationOpeningDate());
        existing.setApplicationClosingDate(updated.getApplicationClosingDate());
        existing.setAvailableOfficerSlots(updated.getAvailableOfficerSlots());
        existing.setVisibility(updated.isVisibility());

        // manager, pending/approved lists typically left alone
        repo.writeBTOProjectToCSV(projects);
        return true;
    }

    /**
     * Deletes a project by its ID.
     *
     * @param projectId The ID of the project to delete.
     * @return true if the project was deleted, false otherwise.
     */
    public boolean deleteProject(int projectId) {
        boolean removed = projects.removeIf(p -> p.getProjectID() == projectId);
        if (removed) {
            repo.writeBTOProjectToCSV(projects);
        }
        return removed;
    }

    /**
     * Filters projects based on the current user's role and eligibility.
     * Applicants and HDB Officers see only visible and eligible projects.
     * Managers see only projects they manage.
     *
     * @return List of filtered {@link BTOProject} objects.
     */
    public List<BTOProject> getFilteredProjects() {
        Role role = currentUser.getRole();
        List<BTOProject> filtered = new ArrayList<>(); // initialise filtered to an empty list due to error
        switch (role) {
            case APPLICANT -> {
                MaritalState ms = currentUser.getMaritalStatus();
                int age = currentUser.getAge();

                if (ms == MaritalState.SINGLE && age >= 35) {
                    // Singles, 35 years old and above, can ONLY apply for 2-Room
                    filtered = projects.stream()
                            .filter(BTOProject::isVisibility)
                            .filter(p -> p.getAvailable2Room() > 0)
                            .collect(Collectors.toList());

                } else if (ms == MaritalState.MARRIED && age >= 21) {
                    // Married, 21 years old and above, can apply for any flat types
                    // (2-Room or 3-Room)
                    filtered = projects.stream()
                            .filter(BTOProject::isVisibility)
                            .filter(p -> p.getAvailable2Room() > 0
                            || p.getAvailable3Room() > 0)
                            .collect(Collectors.toList());

                } else {
                    // Under age or does not match marital state's criteria – no available units to
                    // view.
                    filtered = new ArrayList<>();
                }
            }
            case HDBOFFICER -> {
                MaritalState ms = currentUser.getMaritalStatus();
                int age = currentUser.getAge();
                String officerNRIC = currentUser.getNRIC();

                // Only show visible projects that officer is NOT already an approved officer of
                filtered = projects.stream()
                        .filter(BTOProject::isVisibility)
                        .filter(p -> {
                            var approved = p.getApprovedOfficer();
                            var pending = p.getPendingOfficer();
                            boolean notApproved = approved == null
                                    || approved.stream().noneMatch(nric -> nric.equalsIgnoreCase(officerNRIC));
                            boolean notPending = pending == null
                                    || pending.stream().noneMatch(nric -> nric.equalsIgnoreCase(officerNRIC));
                            boolean flatEligible = false;
                            if (ms == MaritalState.SINGLE && age >= 35) {
                                flatEligible = p.getAvailable2Room() > 0;
                            } else if (ms == MaritalState.MARRIED && age >= 21) {
                                flatEligible = p.getAvailable2Room() > 0 || p.getAvailable3Room() > 0;
                            }
                            return notPending && notApproved && flatEligible;
                        })
                        .collect(Collectors.toList());
            }
            case HDBMANAGER ->
                filtered = projects.stream()
                        .filter(p -> p.getManagerID().equalsIgnoreCase(currentUser.getNRIC()))
                        .collect(Collectors.toList());
            default ->
                filtered = new ArrayList<>();
        }
        return filtered;
    }

    /**
     * Returns a copy of all BTO projects.
     *
     * @return List of all {@link BTOProject} objects.
     */
    public List<BTOProject> getAllProjects() {
        // return a copy so callers can’t edit the internal list
        return new ArrayList<>(projects);
    }

    /**
     * Returns a list of BTO projects handled by the current officer (approved
     * officer).
     *
     * @return List of handled {@link BTOProject} objects.
     */
    public List<BTOProject> getHandledProjects() {
        String me = currentUser.getNRIC();
        return projects.stream()
                .filter(p -> p.getApprovedOfficer() != null
                && p.getApprovedOfficer().stream()
                        .anyMatch(nric -> nric.equalsIgnoreCase(me)))
                .collect(Collectors.toList());
    }

    /**
     * Returns a list of projects filtered by both eligibility and the user's
     * custom filter settings.
     *
     * @param user The user whose filter settings to apply.
     * @return List of filtered {@link BTOProject} objects.
     */
    public List<BTOProject> getFilteredProjectsForUser(User user) {
        // 1) existing filter (visibility, marital/age)
        List<BTOProject> base = getFilteredProjects();

        // 2) load user’s saved filter settings
        FilterSettings fs = FilterSettings.fromCsv(user.getFilterSettings());

        // check marital/age eligibility
        boolean canSee2 = user.getMaritalStatus() == MaritalState.SINGLE
                ? user.getAge() >= 35
                : user.getAge() >= 21;
        boolean canSee3 = user.getMaritalStatus() == MaritalState.MARRIED
                && user.getAge() >= 21;

        // if they asked for 3‑Room but can’t see 3‑Room, return empty immediately:
        if ("3-Room".equals(fs.getRoomType()) && !canSee3) {
            System.out.println(
                    "Sorry as you are only able to view 2 rooms, and are asking to see 3 rooms, the view is empty");
            return Collections.emptyList();
        }
        if ("2-Room".equals(fs.getRoomType()) && !canSee2) {
            return Collections.emptyList();
        }
        // 3) apply them
        return base.stream()
                .filter(p -> {
                    // room type filter
                    if (fs.getRoomType() != null) {
                        if (fs.getRoomType().equals("2-Room") && p.getAvailable2Room() == 0) {
                            return false;
                        }
                        if (fs.getRoomType().equals("3-Room") && p.getAvailable3Room() == 0) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter((BTOProject p) -> {
                    // price filter (choose correct price)
                    int price = fs.getRoomType() != null && fs.getRoomType().equals("3-Room")
                            ? p.getThreeRoomPrice()
                            : p.getTwoRoomPrice();
                    if (fs.getMinPrice() != null && price < fs.getMinPrice()) {
                        return false;
                    }
                    if (fs.getMaxPrice() != null && price > fs.getMaxPrice()) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // Returns a list of projects managed by current user with filter applied
    public List<BTOProject> getFilteredProjectsCreatedbyCurrentManager(User user) {
        // 1) Get the full list and restrict to projects managed by the current manager.
        List<BTOProject> base = getAllProjects().stream()
                .filter(p -> p.getManagerID().equalsIgnoreCase(user.getNRIC()))
                .collect(Collectors.toList());

        // 2) load user’s saved filter settings
        FilterSettings fs = FilterSettings.fromCsv(user.getFilterSettings());

        // 3) apply them
        return base.stream()
                .filter(p -> {
                    // room type filter
                    if (fs.getRoomType() != null) {
                        if (fs.getRoomType().equals("2-Room") && p.getAvailable2Room() == 0) {
                            return false;
                        }
                        if (fs.getRoomType().equals("3-Room") && p.getAvailable3Room() == 0) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter((BTOProject p) -> {
                    // price filter (choose correct price)
                    int price = fs.getRoomType() != null && fs.getRoomType().equals("3-Room")
                            ? p.getThreeRoomPrice()
                            : p.getTwoRoomPrice();
                    if (fs.getMinPrice() != null && price < fs.getMinPrice()) {
                        return false;
                    }
                    if (fs.getMaxPrice() != null && price > fs.getMaxPrice()) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    // Return projects filtered by current user (Manager)
    public List<BTOProject> getFilteredProjectsForManager(User user) {
        // 1) existing filter (visibility, marital/age)
        List<BTOProject> base = getAllProjects();

        // 2) load user’s saved filter settings
        FilterSettings fs = FilterSettings.fromCsv(user.getFilterSettings());

        // 3) apply them
        return base.stream()
                .filter(p -> {
                    // room type filter
                    if (fs.getRoomType() != null) {
                        if (fs.getRoomType().equals("2-Room") && p.getAvailable2Room() == 0) {
                            return false;
                        }
                        if (fs.getRoomType().equals("3-Room") && p.getAvailable3Room() == 0) {
                            return false;
                        }
                    }
                    return true;
                })
                .filter((BTOProject p) -> {
                    // price filter (choose correct price)
                    int price = fs.getRoomType() != null && fs.getRoomType().equals("3-Room")
                            ? p.getThreeRoomPrice()
                            : p.getTwoRoomPrice();
                    if (fs.getMinPrice() != null && price < fs.getMinPrice()) {
                        return false;
                    }
                    if (fs.getMaxPrice() != null && price > fs.getMaxPrice()) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * Saves new filter settings back to the user record and CSV.
     *
     * @param user The user whose filter settings are to be updated.
     * @param fs The new {@link FilterSettings}.
     */
    public void updateUserFilterSettings(User user, FilterSettings fs) {
        user.setFilterSettings(fs.toCsv());
        // assume UserCTRL has saveUserData()
        new UserCTRL().saveUserData();
    }

    /**
     * Updates project visibility based on the current date and each project's
     * application period. Projects outside their application period are set to
     * invisible. Changes are persisted to the CSV file.
     */
    public void updateProjectVisibility() {
        // use the correct date format as in our file ("yyyy-MM-dd")
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (BTOProject project : getAllProjects()) {
            try {
                LocalDate openingDate = LocalDate.parse(project.getApplicationOpeningDate(), formatter);
                LocalDate closingDate = LocalDate.parse(project.getApplicationClosingDate(), formatter);
                if (LocalDate.now().isAfter(closingDate) || LocalDate.now().isBefore(openingDate)) {
                    project.setVisibility(false);
                }
            } catch (Exception e) {
                System.out.println("Error parsing date for project "
                        + project.getProjectID() + ": " + e.getMessage());
            }
        }
        // Then persist the changes to the CSV file
        saveProjects();
    }

    /**
     * Persists the current list of projects to the CSV file.
     */
    public void saveProjects() {
        repo.writeBTOProjectToCSV(projects);
    }

    /**
     * Returns a list of projects the officer is eligible to apply for as an
     * officer, based on NRIC, marital status, and age.
     *
     * @param officerNRIC The NRIC of the officer.
     * @param ms The marital status of the officer.
     * @param age The age of the officer.
     * @return List of eligible {@link BTOProject} objects.
     */
    public List<BTOProject> getEligibleProjectsForOfficerApplication(String officerNRIC, MaritalState ms, int age) {
        return projects.stream()
                .filter(p -> {
                    var pending = p.getPendingOfficer();
                    var approved = p.getApprovedOfficer();
                    boolean notPending = pending == null
                            || pending.stream().noneMatch(nric -> nric.equalsIgnoreCase(officerNRIC));
                    boolean notApproved = approved == null
                            || approved.stream().noneMatch(nric -> nric.equalsIgnoreCase(officerNRIC));
                    boolean flatEligible = false;
                    if (ms == MaritalState.SINGLE && age >= 35) {
                        flatEligible = p.getAvailable2Room() > 0;
                    } else if (ms == MaritalState.MARRIED && age >= 21) {
                        flatEligible = p.getAvailable2Room() > 0 || p.getAvailable3Room() > 0;
                    }
                    return notPending && notApproved && flatEligible;
                })
                .collect(Collectors.toList());
    }
}
