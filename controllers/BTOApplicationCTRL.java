package controllers;

import java.util.List;
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
     *    – only one application total
     *    – enforces flat‑type eligibility
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
        app.setApplicationId(nextId());
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
     *    – only if it belongs to this user
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
    private int nextId() {
        return applicationList.stream()
            .mapToInt(BTOApplication::getApplicationId)
            .max()
            .orElse(0) + 1;
    }

    // ... other stubs (generateFilteredList, updateFlatAvailability, approve/reject, generateReceipt) ...
}
