import controllers.BTOApplicationCTRL;
import controllers.BTOProjectCTRL;
import controllers.EnquiryCTRL;
import controllers.OfficerApplicationCTRL;
import controllers.UserCTRL;
import java.util.Scanner;
import java.util.stream.Collectors;
import models.BTOApplication;
import models.BTOProject;
import models.Enquiry;
import models.FilterSettings;
import models.enumerations.ApplicationStatus;
import models.enumerations.ApplicationType;
import models.enumerations.FlatType;
import models.enumerations.MaritalState;
import models.enumerations.RegistrationStatus;
import models.enumerations.Role;
import views.ApplicantView;
import views.BTOApplicationView;
import views.BTOProjectView;
import views.EnquiryView;
import views.ManagerView;
import views.OfficerApplicationView;
import views.OfficerView;
import views.UserView;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            UserCTRL userCTRL = new UserCTRL();
            UserView userView = new UserView(); // Create a UserView instance
            userCTRL.loadUserData();

            OUTER: while (true) {
                // =====================================
                // Main Menu Options
                // =====================================
                System.out.println("\n=== HDB Hub ===");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Select an option: ");
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> {
                        if (userView.loginFlow(sc, userCTRL)) {
                            // 2) Logged‑in Main menu
                            runCentralMenu(sc, userCTRL);
                        }
                    }
                    case "2" -> {
                        System.out.println("Exiting... Goodbye!");
                        break OUTER;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // Central Menu: Navigation for Logged-in Users
    // --------------------------------------------------------------------------------------------------
    private static void runCentralMenu(Scanner sc, UserCTRL userCTRL) {
        Role role = userCTRL.getCurrentUser().getRole();

        // Instantiate Views
        UserView baseView = switch (role) {
            case APPLICANT -> new ApplicantView();
            case HDBOFFICER -> new OfficerView();
            case HDBMANAGER -> new ManagerView();
        };
        BTOProjectView projectView = new BTOProjectView();
        BTOApplicationView btoApplicationView = new BTOApplicationView();
        EnquiryView enquiryView = new EnquiryView();

        // Instantiate Controllers
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        BTOApplicationCTRL applicationCTRL = new BTOApplicationCTRL(userCTRL.getCurrentUser());
        EnquiryCTRL enquiryCTRL = new EnquiryCTRL(userCTRL.getCurrentUser());
        // added this used for offical application viewing - bryan
        OfficerApplicationCTRL OfficerAppCTRL = new OfficerApplicationCTRL(userCTRL.getCurrentUser());
        OfficerApplicationView officerAppView = new OfficerApplicationView();

        while (true) {
            baseView.displayMenu();

            String opt = sc.nextLine().trim();

            // --- Common options 1–4 ---
            switch (opt) {
                case "1" ->
                    runProjectMenu(sc, userCTRL, projectCTRL, projectView, applicationCTRL, enquiryView, enquiryCTRL);
                case "2" -> runApplicationMenu(sc, userCTRL, projectCTRL, applicationCTRL, btoApplicationView);
                case "3" -> runEnquiryMenu(sc, userCTRL, projectCTRL, enquiryView, enquiryCTRL);
                case "4" -> {
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null)
                        return; // back to login
                }
            }

            if (role != null) // --- Role‑specific extra options ---
                switch (role) {
                    case APPLICANT:
                        switch (opt) {
                            case "5" -> { // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                    case HDBOFFICER:
                        switch (opt) {
                            case "5" -> { // case "5" Enter Officer Application Menu
                                runOfficerApplicationMenu(sc, OfficerAppCTRL, officerAppView, projectCTRL);
                            }
                            case "6" -> { // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                        break;
                    case HDBMANAGER:
                        switch (opt) {
                            // case "5" -> officerApplicationCTRL.
                            case "5" -> {
                                runOfficerApplicationMenu(sc, OfficerAppCTRL, officerAppView, projectCTRL);
                            }
                            case "6" -> {
                                // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                        break;
                    default:
                        break;
                }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // Change Password Handler
    // --------------------------------------------------------------------------------------------------
    private static void handleChangePassword(Scanner sc, UserCTRL userCTRL) {
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();
        userCTRL.changePassword(newPass);
    }

    // OFFICER APPLICATION SUB MENU (option 5)
    // ===========================
    private static void runOfficerApplicationMenu(Scanner sc,
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
                            // 3) Register as officer
                            var elig = offAppCTRL.getEligibleOfficerProjects();
                            view.displayEligibleProjects(elig);

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

                                boolean ok = offAppCTRL.registerAsOfficer(pid);
                                System.out.println(ok
                                        ? "Registration submitted (status PENDING)."
                                        : "Registration failed.");
                            } catch (NumberFormatException e) {
                                System.out.println("Invalid input. Please enter a valid number. Returning to menu.");
                            }
                        }
                        case "4" -> {
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
                return;
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // Projects Menu for Users
    // --------------------------------------------------------------------------------------------------
    private static void runProjectMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
            BTOProjectView projectView, BTOApplicationCTRL applicationCTRL,
            EnquiryView enquiryView, EnquiryCTRL enquiryCTRL) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            switch (role) {
                case APPLICANT -> projectView.displayApplicantMenu();
                case HDBOFFICER -> projectView.displayOfficerMenu();
                case HDBMANAGER -> projectView.displayManagerMenu();
            }
            String c = sc.nextLine().trim();
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

                        // display all bto projects by filter
                        case "2" -> {
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
                        }
                        case "4" -> { // Submit Enquiry for a project
                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);

                            // Get project selection
                            System.out.print("Enter project ID to submit Enquiry: ");
                            int projectId = Integer.parseInt(sc.nextLine());

                            String enquiryText = enquiryView.promptEnquiryCreation(sc);
                            Enquiry newEnquiry = enquiryCTRL.createEnquiry(projectId, enquiryText);
                            enquiryView.displayEnquiry(newEnquiry);
                        }
                        case "5" -> { // back to central menu
                            return;
                        }
                    }
                }
                case HDBOFFICER -> {
                    var availableProjects = projectCTRL.getFilteredProjects();
                    switch (c) {

                        case "1" -> { // Display All BTO Projects (ignore officer assignment and visibility)
                            var allProjects = projectCTRL.getAllProjects();
                            projectView.displayAllProject(allProjects);
                        }
                        case "2" -> {
                            //exactly the same view as applicant
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

                                // Get eligible projects for officer application
                                var eligibleProjects = projectCTRL.getEligibleProjectsForOfficerApplication(officerNRIC,
                                        ms, age);

                                if (eligibleProjects.isEmpty()) {
                                    projectView.showMessage("No eligible BTO projects available for application.");
                                    break;
                                }

                                // Display eligible projects for officer
                                projectView.displayEligibleProjectsForOfficer(eligibleProjects, ms, age);

                                // Prompt for project ID
                                System.out.print("Enter project ID to apply: ");
                                int projectId;
                                try {
                                    projectId = Integer.parseInt(sc.nextLine().trim());
                                } catch (NumberFormatException e) {
                                    projectView.showMessage("Invalid project ID.");
                                    break;
                                }

                                // Validate project selection
                                var selected = eligibleProjects.stream()
                                        .filter(p -> p.getProjectID() == projectId)
                                        .findFirst();
                                if (selected.isEmpty()) {
                                    projectView.showMessage("Selected project is not eligible for application.");
                                    break;
                                }

                                // Prompt for flat type selection
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
                                    if (flatChoice == 1) {
                                        flatType = FlatType.TWOROOM;
                                    } else if (flatChoice == 2) {
                                        flatType = FlatType.THREEROOM;
                                    } else {
                                        projectView.showMessage("Invalid flat type choice.");
                                        break;
                                    }
                                } else {
                                    projectView.showMessage("You are not eligible to apply for any flat type.");
                                    break;
                                }

                                // Submit application
                                boolean ok = applicationCTRL.apply(projectId, flatType);
                                if (ok) {
                                    projectView.showMessage("Application submitted! Status: PENDING.");
                                }
                            } catch (Exception e) {
                                projectView.showMessage(
                                        "An error occurred while applying for a BTO project: " + e.getMessage());
                            }
                        }
                        case "4" -> { // Submit Enquiry for a BTO project

                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);

                            // Get project selection
                            System.out.print("Enter project ID to submit Enquiry: ");
                            int projectId = Integer.parseInt(sc.nextLine());

                            String enquiryText = enquiryView.promptEnquiryCreation(sc);
                            Enquiry newEnquiry = enquiryCTRL.createEnquiry(projectId, enquiryText);
                            enquiryView.displayEnquiry(newEnquiry);

                        }
                        case "5" -> { // Register as HDB Officer of a BTO Projects
                            runOfficerApplicationMenu(sc, new OfficerApplicationCTRL(userCTRL.getCurrentUser()),
                                    new OfficerApplicationView(), projectCTRL);
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
                        default -> System.out.println("Invalid choice, try again.");
                    }
                }
                case HDBMANAGER -> {
                    switch (c) {
                        case "1" -> { // Display All BTO Projects
                            var allProjects = projectCTRL.getAllProjects();
                            projectView.displayAllProject(allProjects);
                        }
                        case "2" -> { // Manager views his own projects

                            var allProjects = projectCTRL.getAllProjects();
                            var managerNRIC = userCTRL.getCurrentUser().getNRIC();
                            var myProjects = allProjects.stream()
                                    .filter(project -> project.getManager().equals(managerNRIC))
                                    .toList();
                            if (myProjects.isEmpty()) {
                                projectView.showMessage("No projects found for you.");
                            } else {
                                projectView.displayManagerProjects(myProjects);
                            }
                        }

                        case "3" -> { // Add BTO Project
                            BTOProject newProj = projectView.promptNewProject(sc);
                            // automatically set projectID
                            int id = projectCTRL.getNextProjectID();
                            newProj.setProjectID(id);
                            newProj.setManager(userCTRL.getCurrentUser().getNRIC());

                            // --- Overlap check: prevent overlapping application periods for same manager
                            var managerNRIC = userCTRL.getCurrentUser().getNRIC();
                            var myProjects = projectCTRL.getAllProjects().stream()
                                    .filter(project -> project.getManager().equals(managerNRIC))
                                    .toList();

                            // Parse dates for new project
                            String newOpen = newProj.getApplicationOpeningDate();
                            String newClose = newProj.getApplicationClosingDate();

                            boolean overlaps = myProjects.stream().anyMatch(existing -> {
                                String existOpen = existing.getApplicationOpeningDate();
                                String existClose = existing.getApplicationClosingDate();

                                // not (existClose < newOpen || newClose < existOpen)
                                return !(existClose.compareTo(newOpen) < 0 || newClose.compareTo(existOpen) < 0);
                            });

                            if (overlaps) {
                                projectView.showMessage(
                                        "Error: The new project's application period overlaps with an existing project you manage. Please choose a different period.");
                                break;
                            }

                            projectCTRL.createProject(newProj);
                            projectView.showMessage("Project created.");
                        }
                        case "4" -> { // Edit BTO Project
                            int id = projectView.promptProjectID(sc);
                            BTOProject existing = projectCTRL.getProjectById(id);
                            if (existing == null) {
                                projectView.showMessage("Project not found.");
                                break;
                            }
                            // prevent manager from editing other people projects - bryan
                            String mgrNRIC = userCTRL.getCurrentUser().getNRIC();
                            if (!existing.getManager().equals(mgrNRIC)) {
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
                                        .filter(project -> project.getManager().equals(managerNRIC)
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
                        case "5" -> { // Delete BTO Project
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
                        case "6" -> { // back to central menu
                            return;
                        }
                    
                
                default -> System.out.println("Invalid choice, try again.");
            }
        }

            }
        }
    }
    

    // --------------------------------------------------------------------------------------------------
    // Enquiry Menu for Users
    // --------------------------------------------------------------------------------------------------
    private static void runEnquiryMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
            EnquiryView enquiryView, EnquiryCTRL enquiryCTRL) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            switch (role) {
                case APPLICANT, HDBOFFICER -> enquiryView.displayApplicantMenu();
                case HDBMANAGER -> enquiryView.displayAdminMenu();
            }

            String c = sc.nextLine().trim();
            switch (role) {
                case APPLICANT -> {
                    var userEnquiries = enquiryCTRL.getFilteredEnquiriesByNRIC();
                    var projectList = projectCTRL.getAllProjects();
                    switch (c) {
                        case "1" -> { // Only display Enquiry by User
                            enquiryView.displayFilteredEnquiries(projectList, userEnquiries);
                        }
                        case "2" -> { // Edit Selected Enquiry
                            // Filter userEnquiries with no response and display
                            var editableEnquiries = enquiryCTRL.getEditableEnquiriesByNRIC();
                            if (editableEnquiries.isEmpty()) {
                                enquiryView.showMessage("No editable enquiries found.");
                                break;
                            }
                            enquiryView.displayFilteredEnquiries(projectList, editableEnquiries);
                            // Get user selection on which enquiry to edit
                            int selectedId = enquiryView.promptEnquirySelection(editableEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(editableEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Get new enquiry text to update
                            String newText = enquiryView.promptNewEnquiryText(selected.getEnquiryText(), sc);
                            if (newText == null || newText.isEmpty()) {
                                enquiryView.showMessage("Enquiry update cancelled!");
                            } else {
                                if (enquiryCTRL.editEnquiry(selected, newText)) {
                                    enquiryView.showMessage("Enquiry updated successfully!");
                                    enquiryView.displayEnquiry(selected);
                                } else {
                                    enquiryView.showMessage("Failed to edit enquiry, please try again.");
                                }
                            }
                        }
                        case "3" -> { // Delete Enquiry
                            // Filter userEnquiries with no response and display
                            var editableEnquiries = enquiryCTRL.getEditableEnquiriesByNRIC();
                            if (editableEnquiries.isEmpty()) {
                                enquiryView.showMessage("No editable enquiries found.");
                                break;
                            }
                            enquiryView.displayFilteredEnquiries(projectList, editableEnquiries);
                            // Get user selection on which enquiry to edit
                            int selectedId = enquiryView.promptEnquirySelection(editableEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(editableEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Confirm deletion
                            if (enquiryView.promptDeletionConfirmation(sc)) {
                                enquiryCTRL.deleteEnquiry(selected);
                                enquiryView.showMessage("Enquiry deleted successfully!");
                            } else {
                                enquiryView.showMessage("Deletion cancelled.");
                            }
                        }
                        case "4" -> {
                            return; // back to central menu
                        }
                    }
                }
                case HDBOFFICER -> {
                    switch (c) {
                        case "4" -> {
                            return; // back to central menu
                        }
                    }
                }
                case HDBMANAGER -> {
                    switch (c) {
                        case "2" -> {
                            return; // back to central menu
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // Application Menu for Users
    // --------------------------------------------------------------------------------------------------
    private static void runApplicationMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
            BTOApplicationCTRL applicationCTRL,
            BTOApplicationView btoApplicationView) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            switch (role) {
                case APPLICANT -> btoApplicationView.displayApplicantMenu();
                case HDBOFFICER -> btoApplicationView.displayOfficerMenu();
                case HDBMANAGER -> btoApplicationView.displayManagerMenu();
            }

            String c = sc.nextLine().trim();
            switch (role) {
                case APPLICANT -> {

                    switch (c) {
                        case "1" -> { // Display All My Applications
                            try {
                                var apps = applicationCTRL.viewUserApplications();
                                var projects = applicationCTRL.getProjects(); // retrieve projects list
                                btoApplicationView.displayUserApplication(apps, userCTRL.getCurrentUser(), projects);
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while retrieving your applications: " + e.getMessage());
                            }
                        }
                        case "2" -> { // Withdraw my application
                            try {
                                var userApps = applicationCTRL.viewUserApplications();
                                var projects = applicationCTRL.getProjects(); // retrieve projects list
                                // Use the view method to display available pending applications
                                if (!btoApplicationView.displayPendingApplications(userApps, projects)) {
                                    break;
                                }

                                // Now prompt user for the application ID to withdraw
                                System.out.print("Enter Application ID to withdraw: ");
                                int appId = Integer.parseInt(sc.nextLine().trim());
                                boolean success = applicationCTRL.withdraw(appId);
                                if (success) {
                                    System.out.println(
                                            "Application withdrawn successfully. Application type updated to WITHDRAWAL and Status updated to PENDING.");
                                } else {
                                    System.out.println(
                                            "Withdrawal failed. Please ensure the application exists and belongs to you.");
                                }
                            } catch (NumberFormatException nfe) {
                                System.out.println("Invalid application ID. Please enter a valid number.");
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while withdrawing your application: " + e.getMessage());
                            }
                        }
                        case "3" -> { // back to central menu
                            return;
                        }
                    }
                }
                case HDBOFFICER -> {
                    switch (c) {
                        case "1" -> { // Display All My Applications
                            try {
                                var apps = applicationCTRL.viewUserApplications();
                                var projects = applicationCTRL.getProjects(); // retrieve projects list
                                btoApplicationView.displayUserApplication(apps, userCTRL.getCurrentUser(), projects);
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while retrieving your applications: " + e.getMessage());
                            }

                        }
                        case "2" -> { // Withdraw my application
                            try {
                                var userApps = applicationCTRL.viewUserApplications();
                                var projects = applicationCTRL.getProjects(); // retrieve projects list
                                // Use the view method to display available pending applications
                                if (!btoApplicationView.displayPendingApplications(userApps, projects)) {
                                    break;
                                }

                                // Prompt the user for the application ID to withdraw
                                System.out.print("Enter Application ID to withdraw: ");
                                int appId = Integer.parseInt(sc.nextLine().trim());
                                boolean success = applicationCTRL.withdraw(appId);
                                if (success) {
                                    System.out.println(
                                            "Application withdrawn successfully. Application type updated to WITHDRAWAL and Status updated to PENDING.");
                                } else {
                                    System.out.println(
                                            "Withdrawal failed. Please ensure the application exists and belongs to you.");
                                }
                            } catch (NumberFormatException nfe) {
                                System.out.println("Invalid application ID. Please enter a valid number.");
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while withdrawing your application: " + e.getMessage());
                            }
                        }
                        case "3" -> { // Booking for successful applicant
                            try {
                                // Get successful applications handled by this officer.
                                var officerApps = applicationCTRL.getApplicationsHandledByOfficer();
                                // Retrieve all projects.
                                var projects = applicationCTRL.getProjects();
                                // Use view helper to display successful applications.
                                if (!btoApplicationView.displaySuccessfulApplications(officerApps, projects)) {
                                    break;
                                }

                                System.out.print("Enter Application ID to book: ");
                                int appId = Integer.parseInt(sc.nextLine().trim());

                                // Book and generate receipt.
                                boolean booked = applicationCTRL.bookAndGenerateReceipt(appId, projectCTRL, userCTRL);
                                if (booked) {
                                    System.out.println("Booking confirmed and receipt generated successfully.");

                                } else {
                                    System.out.println(
                                            "Booking failed: Please check the application details or flat availability.");
                                }
                            } catch (NumberFormatException nfe) {
                                System.out.println("Invalid application ID. Please enter a valid number.");
                            } catch (Exception e) {
                                System.out.println("An error occurred while processing booking: " + e.getMessage());
                            }
                        }
                        case "4" -> {
                            return; // back to central menu
                        }
                    }
                }
                case HDBMANAGER -> {
                    switch (c) {
                        case "1" -> { // Display All Applications Handled By Me
                            try {

                                var managerApplications = applicationCTRL.getApplicationsHandledByManager();
                                if (managerApplications == null || managerApplications.isEmpty()) {
                                    System.out.println("No applications were found under your management.");
                                } else {
                                    btoApplicationView.displayAllApplications(managerApplications);
                                }
                            } catch (Exception e) {
                                System.out
                                        .println("An error occurred while retrieving applications handled by manager: "
                                                + e.getMessage());
                            }
                        }
                        case "2" -> { // Approval / Rejection for Application
                            try {
                                var pendingApps = applicationCTRL.getApplicationsHandledByManager().stream()
                                        .filter(app -> app.getApplicationType() == ApplicationType.APPLICATION
                                                && app.getStatus() == ApplicationStatus.PENDING)
                                        .collect(Collectors.toList());

                                if (pendingApps.isEmpty()) {
                                    System.out.println("No pending applications available for approval or rejection.");
                                    break;
                                }

                                System.out.println("\n=== Pending Applications for Approval/Rejection ===");
                                for (BTOApplication app : pendingApps) {
                                    System.out.println("Application ID: " + app.getApplicationId()
                                            + " | Applicant: " + app.getApplicantNRIC()
                                            + " | Flat Type Applying for: " + app.getFlatType()
                                            + " | Status: " + app.getStatus());

                                    BTOProject project = projectCTRL.getProjectById(app.getProjectID());
                                    if (project != null) {
                                        System.out.println("   -> Project Details: ID: " + project.getProjectID()
                                                + ", Manager: " + project.getManager()
                                                + ", Available 2-Room: " + project.getAvailable2Room()
                                                + ", Available 3-Room: " + project.getAvailable3Room() + "\n");
                                    } else {
                                        System.out.println("   -> Project details not found for Project ID: "
                                                + app.getProjectID());
                                    }
                                }

                                System.out.print("Enter Application ID to process: ");
                                String input = sc.nextLine().trim();
                                int appId;
                                try {
                                    appId = Integer.parseInt(input);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid application ID entered. Please enter a valid number.");
                                    break;
                                }

                                System.out.print("Approve (A) or Reject (R) the application? ");
                                String decision = sc.nextLine().trim();

                                // Delegate the decision processing to the controller.
                                boolean success = applicationCTRL.processApplicationDecision(appId, decision,
                                        projectCTRL);
                                if (success) {
                                    System.out.println("Application decision processed successfully.");
                                } else {
                                    System.out.println("Application decision processing failed.");
                                }
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while processing the application: " + e.getMessage());
                            }
                        }

                        case "3" -> { // Approval for Withdrawal of BTO Application (no need for rejection)
                            try {
                                var pendingWithdrawals = applicationCTRL.getApplicationsHandledByManager().stream()
                                        .filter(app -> app.getApplicationType() == ApplicationType.WITHDRAWAL
                                                && app.getStatus() == ApplicationStatus.PENDING)
                                        .collect(Collectors.toList());

                                if (pendingWithdrawals.isEmpty()) {
                                    System.out.println("No pending withdrawal applications available for approval.");
                                    break;
                                }

                                System.out.println("\n=== Pending Withdrawal Applications ===");
                                for (BTOApplication app : pendingWithdrawals) {
                                    System.out.println("Application ID: " + app.getApplicationId()
                                            + " | Applicant: " + app.getApplicantNRIC()
                                            + " | Flat Type: " + app.getFlatType()
                                            + " | Status: " + app.getStatus());
                                    System.out.println("--------------------------------------");
                                }

                                System.out.print("Enter Application ID for withdrawal approve: ");
                                String input = sc.nextLine().trim();
                                int appId;
                                try {
                                    appId = Integer.parseInt(input);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid application ID entered. Please enter a valid number.");
                                    break;
                                }

                                boolean success = applicationCTRL.approveWithdrawalApplication(appId, projectCTRL);
                                if (!success) {
                                    System.out.println("Withdrawal approval failed.");
                                }
                            } catch (Exception e) {
                                System.out.println(
                                        "An error occurred while processing the withdrawal: " + e.getMessage());
                            }
                        }
                        case "4" -> {
                            return; // back to central menu
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
}
