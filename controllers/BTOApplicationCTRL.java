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
    public void viewUserApplications() {
        List<BTOApplication> mine = applicationList.stream()
                .filter(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()))
                .collect(Collectors.toList());

        if (mine.isEmpty()) {
            System.out.println("You have no applications.");
        } else {
            mine.forEach(System.out::println);
        }
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
     */
    public boolean withdraw(int applicationId) {
        for (BTOApplication a : applicationList) {
            if (a.getApplicationId() == applicationId
                    && a.getApplicantNRIC().equals(currentUser.getNRIC())) {
                a.setApplicationType(ApplicationType.WITHDRAWAL);
                a.setStatus(ApplicationStatus.UNSUCCESSFUL);
                appRepo.writeApplicationToCSV(applicationList);
                return true;
            }
        }
        System.out.println("Application not found or not yours.");
        return false;
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
}
