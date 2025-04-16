import controllers.BTOApplicationCTRL;
import controllers.BTOProjectCTRL;
import controllers.EnquiryCTRL;
import controllers.UserCTRL;
import java.util.Scanner;
import java.util.stream.Collectors;

import models.repositories.ReceiptCSVRepository;
import models.BTOApplication;
import models.Receipt;
import models.User;
import models.BTOProject;
import models.Enquiry;
import models.enumerations.ApplicationStatus;
import models.enumerations.ApplicationType;
import models.enumerations.FlatType;
import models.enumerations.Role;
import views.BTOApplicationView;
import views.ApplicantView;
import views.BTOProjectView;
import views.EnquiryView;
import views.ManagerView;
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
        // OfficerApplicationCTRL officerApplicationCTRL = new
        // OfficerApplicationCTRL(userCTRL.getCurrentUser());

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
                            case "5" -> {
                                // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                    case HDBOFFICER:
                        switch (opt) {
                            // case "5" -> officerApplicationCTRL.
                            case "6" -> {
                                // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                        break;
                    case HDBMANAGER:
                        switch (opt) {
                            // case "5" -> officerApplicationCTRL.
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
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);
                        }
                        case "2" -> { // Apply for BTO
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
                            int flatChoice = Integer.parseInt(sc.nextLine());
                            FlatType flatType = (flatChoice == 1) ? FlatType.TWOROOM : FlatType.THREEROOM;

                            // Submit application
                            boolean ok = applicationCTRL.apply(projectId, flatType);
                            if (ok) {
                                System.out.println("Application submitted! Status: PENDING.");
                            }
                        }
                        case "3" -> { // Submit Enquiry for a project
                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                    userCTRL.getCurrentUser(), availableProjects);

                            // Get project selection
                            System.out.print("Enter project ID to submit Enquiry: ");
                            int projectId = Integer.parseInt(sc.nextLine());

                            String enquiryText = enquiryView.promptEnquiryCreation(sc);
                            Enquiry newEnquiry = enquiryCTRL.createEnquiry(projectId, enquiryText);
                            enquiryView.displayEnquiryCreated(newEnquiry);
                        }
                        case "4" -> {
                            return; // back to central menu
                        }
                    }
                }
                case HDBOFFICER -> {
                    switch (c) {

                        case "6" -> {
                            return; // back to central menu
                        }
                    }
                }
                case HDBMANAGER -> {
                    switch (c) {
                        case "1" -> {
                            var allProjects = projectCTRL.getAllProjects();
                            projectView.displayAllProject(allProjects);
                        }
                        case "2" -> {
                            // Manager views his own projects
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

                        case "3" -> {
                            BTOProject newProj = projectView.promptNewProject(sc);
                            // automatically set projectID
                            int id = projectCTRL.getNextProjectID();
                            newProj.setProjectID(id);
                            newProj.setManager(userCTRL.getCurrentUser().getNRIC());
                            projectCTRL.createProject(newProj);
                            projectView.showMessage("Project created.");
                        }
                        case "4" -> { // Edit
                            int id = projectView.promptProjectID(sc);
                            BTOProject existing = projectCTRL.getProjectById(id);
                            if (existing == null) {
                                projectView.showMessage("Project not found.");
                                break;
                            }
                            projectView.editProjectDetails(sc, existing);
                            projectCTRL.editProject(id, existing);
                            projectView.showMessage("Project updated.");
                        }
                        case "5" -> { // Delete
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
                        case "6" -> {
                            return; // back to central menu
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
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
                    switch (c) {
                        case "1" -> { // Only display Enquiry by User
                            var projectList = projectCTRL.getAllProjects();
                            enquiryView.displayFilteredEnquiries(projectList, userEnquiries);
                        }
                        case "2" -> { // Apply for BTO
                            // Show enquiries by user
                            // Select enquiry to edit
                            // Show edit options
                        }
                        case "3" -> { // Delete Enquiry
                            // Show enquiries by user
                            // Select enquiry to delete
                            // Confirm deletion
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
                                            "Application withdrawn successfully. Status updated to PENDING.");
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
                        case "3" -> {
                            return;// back to central menu
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
                                            "Application withdrawn successfully. Status updated to PENDING.");
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
                                            "Booking failed. Please check the application details or flat availability.");
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
                            try { // withdrawal application has to be in pending status in order to approve
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

                                System.out.print("Enter Withdrawal Application ID to approve: ");
                                String input = sc.nextLine().trim();
                                int appId;
                                try {
                                    appId = Integer.parseInt(input);
                                } catch (NumberFormatException e) {
                                    System.out.println("Invalid application ID entered. Please enter a valid number.");
                                    break;
                                }

                                var selectedWithdrawal = pendingWithdrawals.stream()
                                        .filter(app -> app.getApplicationId() == appId)
                                        .findFirst();
                                if (selectedWithdrawal.isEmpty()) {
                                    System.out.println("Withdrawal application not found or not pending.");
                                    break;
                                }

                                // Approving the withdrawal simply means updating its status to UNSUCCESSFUL
                                boolean success = applicationCTRL.updateApplicationStatus(appId, "UNSUCCESSFUL");
                                if (success) {
                                    System.out.println("Withdrawal approved. Application marked as UNSUCCESSFUL.");
                                } else {
                                    System.out.println("Failed to update the withdrawal application status.");
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
