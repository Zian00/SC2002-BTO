package controllers;

import boundaries.BTOApplicationView;
import entity.BTOApplication;
import entity.BTOProject;
import entity.Receipt;
import entity.User;
import entity.enumerations.ApplicationStatus;
import entity.enumerations.ApplicationType;
import entity.enumerations.FlatType;
import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import entity.repositories.ApplicationCSVRepository;
import entity.repositories.BTOProjectCSVRepository;
import entity.repositories.ReceiptCSVRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Controller class for managing BTO applications in the BTO system.
 * <p>
 * Handles application submission, withdrawal, approval/rejection, booking, and report generation.
 * Provides role-based application menus for applicants, officers, and managers.
 * </p>
 */
public class BTOApplicationCTRL {

    /** List of all BTO applications loaded from the repository. */
    private List<BTOApplication> applicationList;
    /** List of all BTO projects loaded from the repository. */
    private List<BTOProject> projects;
    /** The currently logged-in user. */
    private User currentUser;
    /** Repository for reading/writing application data. */
    private ApplicationCSVRepository appRepo = new ApplicationCSVRepository();
    /** Repository for reading/writing project data. */
    private BTOProjectCSVRepository projRepo = new BTOProjectCSVRepository();

    /**
     * Loads all applications and projects, and keeps track of the logged-in user.
     * @param currentUser The currently logged-in user.
     */
    public BTOApplicationCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.appRepo = new ApplicationCSVRepository();
        this.projRepo = new BTOProjectCSVRepository();
        this.applicationList = appRepo.readApplicationFromCSV();
        this.projects = projRepo.readBTOProjectFromCSV();
    }

    // --------------------------------------------------------------------------------------------------
    // Application Menu for Users
    // --------------------------------------------------------------------------------------------------

    /**
     * Runs the application menu for the current user, displaying options and routing
     * to the appropriate actions based on the user's role (applicant, officer, manager).
     *
     * @param sc Scanner for user input.
     * @param userCTRL The UserCTRL instance.
     * @param projectCTRL The BTOProjectCTRL instance.
     * @param applicationCTRL This BTOApplicationCTRL instance.
     * @param btoApplicationView The BTOApplicationView for displaying menus and results.
     */
    public void runApplicationMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
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
                                                + ", Manager: " + project.getManagerID()
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
                        case "4" -> { // Generate / Filter report of all APPLICANTS under projects handled by you
                            try {
                                var allManagerApps = applicationCTRL.getApplicationsHandledByManager();
                                if (allManagerApps == null || allManagerApps.isEmpty()) {
                                    System.out.println("No applications found under your management.");
                                    break;
                                }
                                var allProjects = applicationCTRL.getProjects();
                        
                                MaritalState maritalFilter = btoApplicationView.promptMaritalStatusFilter(sc);
                                String flatTypeFilter = btoApplicationView.promptFlatTypeFilter(sc);
                                Integer minAge = btoApplicationView.promptMinAge(sc);
                                Integer maxAge = btoApplicationView.promptMaxAge(sc);
                                String neighbourhoodFilter = btoApplicationView.promptNeighbourhoodFilter(sc);
                        
                                List<BTOApplication> filteredApps = applicationCTRL.generateReport(
                                        maritalFilter, flatTypeFilter, minAge, maxAge, neighbourhoodFilter, allProjects, userCTRL);
                        
                                if (filteredApps.isEmpty()) {
                                    System.out.println("No applicants found matching the selected filters.");
                                } else {
                                    System.out.println("\n=== Filtered Applicant Report ===");
                                    for (BTOApplication app : filteredApps) {
                                        User applicant = userCTRL.getUserByNRIC(app.getApplicantNRIC());
                                        Optional<BTOProject> projectOpt = allProjects.stream()
                                                .filter(p -> p.getProjectID() == app.getProjectID())
                                                .findFirst();
                                        String projectName = projectOpt.map(BTOProject::getProjectName)
                                                .orElse("Unknown");
                                        String neighbourhood = projectOpt.map(BTOProject::getNeighborhood)
                                                .orElse("Unknown");
                                        System.out.println("Applicant: " + applicant.getName());
                                        System.out.println("NRIC: " + applicant.getNRIC());
                                        System.out.println("Age: " + applicant.getAge());
                                        System.out.println("Marital: " + applicant.getMaritalStatus());
                                        System.out.println("Flat: " + app.getFlatType());
                                        System.out.println("Project: " + projectName);
                                        System.out.println("Neighbourhood: " + neighbourhood);
                                        System.out.println("Status: " + app.getStatus());
                                        System.out.println("-------------------------------");
                                    }
                                    System.out.println("=== End of Report ===");
                                }
                            } catch (Exception e) {
                                System.out.println("An error occurred while generating the report: " + e.getMessage());
                            }
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    /**
     * Shows all applications by the currently logged-in user.
     * @return List of {@link BTOApplication} objects for the user.
     */
    public List<BTOApplication> viewUserApplications() {
        return applicationList.stream()
                .filter(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()))
                .collect(Collectors.toList());
    }

    /**
     * Submits a new application for a BTO project if eligible.
     * Only one application is allowed per user.
     * @param projectId The project ID to apply for.
     * @param flatType The flat type to apply for.
     * @return true if application was successful, false otherwise.
     */
    public boolean apply(int projectId, FlatType flatType) {
        // already applied?
        boolean hasApplied = applicationList.stream()
                .anyMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC())
                        && a.getApplicationType() == ApplicationType.APPLICATION);
        System.out.println("[DEBUG]" + applicationList.stream()
        .anyMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC())
                && a.getApplicationType() == ApplicationType.APPLICATION));
        if (hasApplied) {
            System.out.println("Cannot apply for more than one project.");
            return false;
        }

        // find project
        BTOProject proj = projects.stream()
                .filter(p -> p.getProjectID() == projectId && p.isVisibility())
                .findFirst()
                .orElse(null);
        if (proj == null) {
            System.out.println("Project not found or not avaialable.");
            return false;
        }

        // check eligibility
        boolean eligible;
        if (currentUser.getMaritalStatus().name().equals("SINGLE")) {
            eligible = flatType == FlatType.TWOROOM && currentUser.getAge() >= 35;
        } else {
            eligible = currentUser.getAge() >= 21;
        }
        if (!eligible) {
            System.out.println("You are not eligible for " + flatType);
            return false;
        }

        // create & persist
        BTOApplication app = new BTOApplication();
        app.setApplicationId(getNextProjectID());
        app.setApplicantNRIC(currentUser.getNRIC());
        app.setProjectID(projectId);
        app.setApplicationType(ApplicationType.APPLICATION);
        app.setStatus(ApplicationStatus.PENDING);
        app.setFlatType(flatType.name());

        applicationList.add(app);
        appRepo.writeApplicationToCSV(applicationList);
        return true;
    }

    /**
     * Withdraws an application by its ID if it belongs to the current user.
     * @param applicationId The application ID to withdraw.
     * @return true if withdrawal was successful, false otherwise.
     */
    public boolean withdraw(int applicationId) {
        try {
            var appOpt = applicationList.stream()
                    .filter(a -> a.getApplicationId() == applicationId
                            && a.getApplicantNRIC().equals(currentUser.getNRIC()))
                    .findFirst();
            if (appOpt.isEmpty()) {
                System.out.println("Application not found or not owned by you.");
                return false;
            }
            BTOApplication app = appOpt.get();
            // Check if application is already withdrawn
            if (app.getApplicationType() == ApplicationType.WITHDRAWAL) {
                if (app.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
                    System.out.println("Application is already withdrawn.");
                    return false;
                } else if (app.getStatus() == ApplicationStatus.PENDING) {
                    System.out.println("Withdrawal is already processing.");
                    return false;
                }
            }
            // Set type to WITHDRAWAL and update status
            app.setApplicationType(ApplicationType.WITHDRAWAL);
            app.setStatus(ApplicationStatus.PENDING);
            appRepo.writeApplicationToCSV(applicationList);
            return true;
        } catch (Exception e) {
            System.out.println("An error occurred while processing withdrawal: " + e.getMessage());
            return false;
        }
    }

    /**
     * Utility to pick the next unique application ID.
     * @return The next available application ID.
     */
    private int getNextProjectID() {
        return applicationList.stream()
                .mapToInt(BTOApplication::getApplicationId)
                .max()
                .orElse(0) + 1;
    }

    /**
     * Gets all applications handled by the current manager.
     * @return List of {@link BTOApplication} objects.
     */
    public List<BTOApplication> getApplicationsHandledByManager() {
        return applicationList.stream()
                .filter(app -> projects.stream()
                        .anyMatch(proj -> proj.getProjectID() == app.getProjectID() &&
                                proj.getManagerID().equals(currentUser.getNRIC())))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications for which the associated project's approved officer list
     * contains the current officer's NRIC.
     * @return List of {@link BTOApplication} objects.
     */
    public List<BTOApplication> getApplicationsHandledByOfficer() {
        String officerNRIC = currentUser.getNRIC();
        return applicationList.stream()
                .filter(app -> projects.stream()
                        .filter(proj -> proj.getProjectID() == app.getProjectID()
                                && proj.getApprovedOfficer() != null)
                        .anyMatch(proj -> proj.getApprovedOfficer().stream()
                                .anyMatch(nric -> nric.equalsIgnoreCase(officerNRIC))))
                .collect(Collectors.toList());
    }

    /**
     * Updates the status of an application.
     * @param applicationId The ID of the application to update.
     * @param newStatus The new status ("SUCCESSFUL" or "UNSUCCESSFUL").
     * @return true if updated successfully, false otherwise.
     */
    public boolean updateApplicationStatus(int applicationId, String newStatus) {

        // Parse the input string to its corresponding enum, for instance:
        ApplicationStatus statusToSet;
        if (newStatus.equalsIgnoreCase("SUCCESSFUL")) {
            statusToSet = ApplicationStatus.SUCCESSFUL;
        } else if (newStatus.equalsIgnoreCase("UNSUCCESSFUL")) {
            statusToSet = ApplicationStatus.UNSUCCESSFUL;
        } else {
            System.out.println("Invalid status provided.");
            return false;
        }

        for (BTOApplication app : applicationList) {
            if (app.getApplicationId() == applicationId && app.getStatus() == ApplicationStatus.PENDING) {
                app.setStatus(statusToSet);

                appRepo.writeApplicationToCSV(applicationList);
                return true;
            }
        }
        return false;

    }

    /**
     * Processes an application decision (approve or reject) for a manager.
     * @param appId The application ID to process.
     * @param decision "A" for approve, "R" for reject.
     * @param projectCTRL The BTOProjectCTRL instance.
     * @return true if processed successfully, false otherwise.
     */
    public boolean processApplicationDecision(int appId, String decision, BTOProjectCTRL projectCTRL) {

        // Retrieve the application from applicationList
        Optional<BTOApplication> optApp = applicationList.stream()
                .filter(app -> app.getApplicationId() == appId && app.getStatus() == ApplicationStatus.PENDING)
                .findFirst();
        if (optApp.isEmpty()) {
            System.out.println("Application not found or not in pending.");
            return false;
        }
        BTOApplication selectedApp = optApp.get();

        // --- Only allow approval/rejection if manager handles the project ---
        BTOProject project = projectCTRL.getProjectById(selectedApp.getProjectID());
        if (project == null) {
            System.out.println("Associated project not found.");
            return false;
        }
        if (!project.getManagerID().equals(currentUser.getNRIC())) {
            System.out.println("You can only approve/reject applications for projects you manage.");
            return false;
        }

        if (decision.equalsIgnoreCase("A")) {
            String flatType = selectedApp.getFlatType();
            boolean supplyAvailable = false;
            if (flatType.equalsIgnoreCase("TWOROOM")) {
                if (project.getAvailable2Room() > 0) {
                    supplyAvailable = true;
                    project.setAvailable2Room(project.getAvailable2Room() - 1);
                } else {
                    System.out.println("No 2-Room flats remaining for approval.");
                }
            } else if (flatType.equalsIgnoreCase("THREEROOM")) {
                if (project.getAvailable3Room() > 0) {
                    supplyAvailable = true;
                    project.setAvailable3Room(project.getAvailable3Room() - 1);
                } else {
                    System.out.println("No 3-Room flats remaining for approval.");
                }
            } else {
                System.out.println("Invalid flat type in application: " + flatType);
            }

            if (supplyAvailable) {
                // Update project details first.
                if (projectCTRL.editProject(project.getProjectID(), project)) {
                    // Now update application status.
                    return updateApplicationStatus(appId, "SUCCESSFUL");
                } else {
                    System.out.println("Failed to update project data with the reduced flat supply.");
                    return false;
                }
            }
        } else if (decision.equalsIgnoreCase("R")) {
            return updateApplicationStatus(appId, "UNSUCCESSFUL");
        } else {
            System.out.println("Invalid decision input. Please enter 'A' for approve or 'R' for reject.");
        }
        return false;
    }

    /**
     * Books a flat for a successful application and updates the application status to BOOKED.
     * @param applicationId The application ID to book.
     * @param projectCTRL The BTOProjectCTRL instance.
     * @return true if booking was successful, false otherwise.
     */
    public boolean bookApplication(int applicationId, BTOProjectCTRL projectCTRL) {

        // Retrieve the application
        var appOption = applicationList.stream()
                .filter(app -> app.getApplicationId() == applicationId)
                .findFirst();
        if (appOption.isEmpty()) {
            System.out.println("Application not found.");
            return false;
        }
        BTOApplication app = appOption.get();

        // NEW: Check if the applicant already has a booked flat
        boolean alreadyBooked = applicationList.stream()
                .filter(a -> a.getApplicantNRIC().equalsIgnoreCase(app.getApplicantNRIC()))
                .anyMatch(a -> a.getStatus() == ApplicationStatus.BOOKED);

        if (alreadyBooked) {
            System.out.println(
                    "Applicant " + app.getApplicantNRIC() + " has already booked a flat. Cannot book another.");
            return false;
        }

        // === GUARD CLAUSE: Check if application is not in SUCCESSFUL state
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            if (app.getStatus() == ApplicationStatus.BOOKED) {
                System.out.println("Application ID " + applicationId + " has already been booked.");
            } else {
                System.out.println("Application ID " + applicationId + " is not marked as SUCCESSFUL. Cannot book.");
            }
            return false;
        }

        // Retrieve the associated project
        BTOProject proj = projectCTRL.getProjectById(app.getProjectID());

        if (proj == null) {
            System.out.println("Associated project not found.");
            return false;
        }
        // Update available flats as per chosen flat type
        if (app.getFlatType().equalsIgnoreCase("TWOROOM")) {
            if (proj.getAvailable2Room() <= 0) {
                System.out.println("No 2-Room flats remaining for booking.");
                return false;
            }
            proj.setAvailable2Room(proj.getAvailable2Room() - 1);
        } else if (app.getFlatType().equalsIgnoreCase("THREEROOM")) {
            if (proj.getAvailable3Room() <= 0) {
                System.out.println("No 3-Room flats remaining for booking.");
                return false;
            }
            proj.setAvailable3Room(proj.getAvailable3Room() - 1);
        } else {
            System.out.println("Invalid flat type in application.");
            return false;
        }
        // Update the application status to BOOKED
        app.setStatus(ApplicationStatus.BOOKED);
        // Persist application changes
        appRepo.writeApplicationToCSV(applicationList);
        // Persist updated project data
        projectCTRL.saveProjects();
        // Confirmation message
        System.out.println("Application ID " + applicationId + " successfully booked. Status updated to BOOKED.");
        return true;
    }

    /**
     * Gets an application by its ID.
     * @param applicationId The application ID.
     * @return The {@link BTOApplication} object, or null if not found.
     */
    public BTOApplication getApplicationById(int applicationId) {
        return applicationList.stream()
                .filter(app -> app.getApplicationId() == applicationId)
                .findFirst()
                .orElse(null);
    }

    /**
     * Gets the list of all BTO projects.
     * @return List of {@link BTOProject} objects.
     */
    public List<BTOProject> getProjects() {
        return projects;
    }

    /**
     * Books a successful application and generates a receipt for the applicant.
     * @param appId The application ID to book.
     * @param projectCTRL The BTOProjectCTRL instance.
     * @param userCTRL The UserCTRL instance.
     * @return true if booking and receipt generation were successful, false otherwise.
     */
    public boolean bookAndGenerateReceipt(int appId, BTOProjectCTRL projectCTRL, UserCTRL userCTRL) {
        try {
            // Only allow booking for applications handled by this officer
            var handledApps = getApplicationsHandledByOfficer();
            var appOpt = handledApps.stream()
                    .filter(a -> a.getApplicationId() == appId && a.getStatus() == ApplicationStatus.SUCCESSFUL)
                    .findFirst();
            if (appOpt.isEmpty()) {
                System.out.println("Application not found, not successful, or not handled by you.");
                return false;
            }
            // Book the application as before.
            boolean booked = bookApplication(appId, projectCTRL);
            if (!booked) {
                System.out.println("Booking failed. Please check application status and flat availability.");
                return false;
            }
            // Retrieve application and project details.
            BTOApplication bookedApp = getApplicationById(appId);
            if (bookedApp == null) {
                System.out.println("Booked application details not found.");
                return false;
            }
            BTOProject bookedProject = projectCTRL.getProjectById(bookedApp.getProjectID());
            if (bookedProject == null) {
                System.out.println("Associated project details not found.");
                return false;
            }
            // Create and populate the receipt.
            ReceiptCSVRepository receiptRepo = new ReceiptCSVRepository();
            Receipt receipt = new Receipt();
            User applicant = userCTRL.getUserByNRIC(bookedApp.getApplicantNRIC());
            if (applicant == null) {
                System.out.println("Applicant details not found.");
                return false;
            }
            receipt.setReceiptID(receiptRepo.getNextReceiptID());
            receipt.setNRIC(bookedApp.getApplicantNRIC());
            receipt.setApplicantName(applicant.getName());
            receipt.setAge(applicant.getAge());
            receipt.setMaritalStatus(applicant.getMaritalStatus());
            receipt.setFlatType(bookedApp.getFlatType());
            receipt.setProjectID(bookedProject.getProjectID());
            receipt.setProjectName(bookedProject.getProjectName());
            receipt.setNeighborhood(bookedProject.getNeighborhood());
            receipt.setApplicationOpeningDate(bookedProject.getApplicationOpeningDate());
            receipt.setApplicationClosingDate(bookedProject.getApplicationClosingDate());

            // Get manager's name
            User manager = userCTRL.getUserByNRIC(bookedProject.getManagerID());
            String managerName = (manager != null) ? manager.getName() : bookedProject.getManagerID();
            receipt.setManager(managerName);

            // Write the receipt to CSV.
            receiptRepo.writeReceiptCSV(receipt);

            // Print the generated receipt to the terminal.
            System.out.println("\n=== Generated Receipt ===");
            System.out.println("Receipt ID            : " + receipt.getReceiptID());
            System.out.println("Applicant NRIC        : " + receipt.getNRIC());
            System.out.println("Applicant Name        : " + receipt.getApplicantName());
            System.out.println("Applicant Age         : " + receipt.getAge());
            System.out.println("Marital Status        : " + receipt.getMaritalStatus());
            System.out.println("Flat Type             : " + receipt.getFlatType());
            System.out.println("Project ID            : " + receipt.getProjectID());
            System.out.println("Project Name          : " + receipt.getProjectName());
            System.out.println("Neighborhood          : " + receipt.getNeighborhood());
            System.out.println("Application Open Date : " + receipt.getApplicationOpeningDate());
            System.out.println("Application Close Date: " + receipt.getApplicationClosingDate());
            System.out.println("Manager               : " + receipt.getManager());
            System.out.println("==========================\n");

            return true;
        } catch (Exception ex) {
            System.out.println("Error in booking and receipt generation: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Approves a withdrawal application and updates flat availability.
     * @param appId The withdrawal application ID.
     * @param projectCTRL The BTOProjectCTRL instance.
     * @return true if withdrawal was approved, false otherwise.
     */
    public boolean approveWithdrawalApplication(int appId, BTOProjectCTRL projectCTRL) {
        // Find the pending withdrawal application
        Optional<BTOApplication> appOpt = applicationList.stream()
                .filter(app -> app.getApplicationId() == appId
                        && app.getApplicationType() == ApplicationType.WITHDRAWAL
                        && app.getStatus() == ApplicationStatus.PENDING)
                .findFirst();

        if (appOpt.isEmpty()) {
            System.out.println("Withdrawal application not found or not pending.");
            return false;
        }
        BTOApplication withdrawalApp = appOpt.get();

        // Check project and manager
        BTOProject project = projectCTRL.getProjectById(withdrawalApp.getProjectID());
        if (project == null) {
            System.out.println("Associated project not found.");
            return false;
        }
        if (!project.getManagerID().equals(currentUser.getNRIC())) {
            System.out.println("You can only approve withdrawal applications for projects you manage.");
            return false;
        }

        // Increment flat availability
        switch (withdrawalApp.getFlatType().toUpperCase()) {
            case "TWOROOM" -> project.setAvailable2Room(project.getAvailable2Room() + 1);
            case "THREEROOM" -> project.setAvailable3Room(project.getAvailable3Room() + 1);
            default -> {
                System.out.println("Unknown flat type: " + withdrawalApp.getFlatType());
                return false;
            }
        }

        // Persist project and application changes
        projectCTRL.editProject(project.getProjectID(), project);
        projectCTRL.saveProjects();
        withdrawalApp.setStatus(ApplicationStatus.SUCCESSFUL);
        appRepo.writeApplicationToCSV(applicationList);

        System.out.println("Withdrawal approved. Application marked as SUCCESSFUL. Available flat type incremented.");
        return true;
    }

    /**
     * Generates a filtered report of all applicants under projects handled by the current manager.
     * @param maritalFilter Marital status filter.
     * @param flatTypeFilter Flat type filter.
     * @param minAge Minimum age filter.
     * @param maxAge Maximum age filter.
     * @param neighbourhoodFilter Neighbourhood filter.
     * @param allProjects List of all BTO projects.
     * @param userCTRL The UserCTRL instance.
     * @return List of filtered {@link BTOApplication} objects.
     */
    public List<BTOApplication> generateReport(
            MaritalState maritalFilter,
            String flatTypeFilter,
            Integer minAge,
            Integer maxAge,
            String neighbourhoodFilter,
            List<BTOProject> allProjects,
            UserCTRL userCTRL) {
    
        List<BTOApplication> managerApps = getApplicationsHandledByManager(); // existing method
        if (managerApps == null || managerApps.isEmpty()) {
            return new ArrayList<>();
        }
        
        List<BTOApplication> filteredApps = new ArrayList<>();
        for (BTOApplication app : managerApps) {
            // Only consider main applications (skip withdrawals)
            if (app.getApplicationType() != ApplicationType.APPLICATION) {
                continue;
            }
            
            // Get applicant details
            User applicant = userCTRL.getUserByNRIC(app.getApplicantNRIC());
            if (applicant == null) {
                continue;
            }
            
            // Get project details
            Optional<BTOProject> projectOpt = allProjects.stream()
                    .filter(p -> p.getProjectID() == app.getProjectID())
                    .findFirst();
            if (projectOpt.isEmpty()) {
                continue;
            }
            BTOProject project = projectOpt.get();
            
            // Apply filters:
            if (maritalFilter != null && applicant.getMaritalStatus() != maritalFilter)
                continue;
            if (flatTypeFilter != null && !app.getFlatType().equalsIgnoreCase(flatTypeFilter))
                continue;
            if (minAge != null && applicant.getAge() < minAge)
                continue;
            if (maxAge != null && applicant.getAge() > maxAge)
                continue;
            if (neighbourhoodFilter != null &&
                    !project.getNeighborhood().toLowerCase().contains(neighbourhoodFilter.toLowerCase()))
                continue;
                
            filteredApps.add(app);
        }
        
        return filteredApps;
    }
}
