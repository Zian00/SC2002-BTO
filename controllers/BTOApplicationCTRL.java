package controllers;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.BTOApplication;
import models.BTOProject;
import models.User;
import models.enumerations.ApplicationStatus;
import models.enumerations.ApplicationType;
import models.enumerations.FlatType;
import models.repositories.ApplicationCSVRepository;
import models.repositories.BTOProjectCSVRepository;

public class BTOApplicationCTRL {

    private List<BTOApplication> applicationList;
    private List<BTOProject> projects;
    private User currentUser;
    private ApplicationCSVRepository appRepo = new ApplicationCSVRepository();
    private BTOProjectCSVRepository projRepo = new BTOProjectCSVRepository();

    /** Load all applications and projects, keep track of the logged‑in user */
    public BTOApplicationCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.appRepo = new ApplicationCSVRepository();
        this.projRepo = new BTOProjectCSVRepository();
        this.applicationList = appRepo.readApplicationFromCSV();
        this.projects = projRepo.readBTOProjectFromCSV();
    }

    /** 1) Show all applications by this user */
    public List<BTOApplication> viewUserApplications() {
        return applicationList.stream()
                .filter(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()))
                .collect(Collectors.toList());
    }

    /**
     * 2) Apply for a project
     * – only one application total
     * – enforces flat‑type eligibility
     */

    public boolean apply(int projectId, FlatType flatType) {
        // already applied?
        boolean hasApplied = applicationList.stream()
                .anyMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()));
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
            System.out.println("Project not found or not visible.");
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
     * 3) Withdraw an application
     * – only if it belongs to this user
     * 
     * An application is considered withdrawn if:
     * - its status is UNSUCCESSFUL and its application type is WITHDRAWAL.
     * 
     * If the application type is WITHDRAWAL but the status is PENDING,
     * then withdrawal is still processing.
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

    /** Utility to pick the next unique application ID */
    private int getNextProjectID() {
        return applicationList.stream()
                .mapToInt(BTOApplication::getApplicationId)
                .max()
                .orElse(0) + 1;
    }

    /*
     * Manager menu -> BTO Application Menu -> 1) Display All Applications Handled
     * By You
     */
    public List<BTOApplication> getApplicationsHandledByManager() {
        return applicationList.stream()
                .filter(app -> projects.stream()
                        .anyMatch(proj -> proj.getProjectID() == app.getProjectID() &&
                                proj.getManager().equals(currentUser.getNRIC())))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all applications for which the associated project's approved
     * officer list
     * contains the current officer's NRIC.
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
     * Manager menu -> BTO Application Menu -> 2) Approval / Rejection for
     * Application
     * 
     * @param applicationId The ID of the application to update.
     * @param newStatus     The new status (e.g., "SUCCESSFUL" or "UNSUCCESSFUL").
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

        // ... other stubs (generateFilteredList, updateFlatAvailability,
        // approve/reject, generateReceipt) ...
    }

    public boolean processApplicationDecision(int appId, String decision, BTOProjectCTRL projectCTRL) {
        // Retrieve the application from applicationList
        Optional<BTOApplication> optApp = applicationList.stream()
                .filter(app -> app.getApplicationId() == appId && app.getStatus() == ApplicationStatus.PENDING)
                .findFirst();
        if (optApp.isEmpty()) {
            System.out.println("Application not found or not pending.");
            return false;
        }
        BTOApplication selectedApp = optApp.get();
        if (decision.equalsIgnoreCase("A")) {
            // Approval: check project supply
            BTOProject project = projectCTRL.getProjectById(selectedApp.getProjectID());
            if (project == null) {
                System.out.println("Associated project not found.");
                return false;
            }
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

    public boolean bookApplication(int applicationId, BTOProjectCTRL projectCTRL) {
        // Retrieve the application
        var appOpt = applicationList.stream()
                .filter(app -> app.getApplicationId() == applicationId)
                .findFirst();
        if (appOpt.isEmpty()) {
            System.out.println("Application not found.");
            return false;
        }
        BTOApplication app = appOpt.get();
        // Check that the application is marked as SUCCESSFUL
        if (app.getStatus() != ApplicationStatus.SUCCESSFUL) {
            System.out.println("Application is not marked as SUCCESSFUL. Cannot book.");
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
                System.out.println("No 2-Room flats remaining.");
                return false;
            }
            proj.setAvailable2Room(proj.getAvailable2Room() - 1);
        } else if (app.getFlatType().equalsIgnoreCase("THREEROOM")) {
            if (proj.getAvailable3Room() <= 0) {
                System.out.println("No 3-Room flats remaining.");
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
        return true;
    }

    public List<BTOProject> getProjects() {
        return projects;
    }

}
