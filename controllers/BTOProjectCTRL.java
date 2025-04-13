package controllers;

import java.util.List;
import java.util.stream.Collectors;

import models.BTOProject;
import models.User;
import models.enumerations.Role;
import models.repositories.BTOProjectCSVRepository;

public class BTOProjectCTRL {

    private List<BTOProject> projects;
    private User currentUser;

    public BTOProjectCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.projects    = new BTOProjectCSVRepository().readBTOProjectFromCSV();
    }

    /** 1) Applicants: only visible projects */
    public void viewAvailableProjects() {
        projects.stream()
            .filter(BTOProject::isVisibility)
            .forEach(System.out::println);
    }

    /** 2) Officers: all projects they’re assigned to (approved), ignore visibility */
    public void viewOfficerProjects() {
        if (currentUser.getRole() != Role.HDBOFFICER) {
            System.out.println("Not an HDB Officer.");
            return;
        }
        List<BTOProject> mine = projects.stream()
            .filter(p -> p.getApprovedOfficer() != null
                      && p.getApprovedOfficer().contains(currentUser.getName()))
            .collect(Collectors.toList());

        if (mine.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
        } else {
            mine.forEach(System.out::println);
        }
    }

    /** 3) Managers: view every project, regardless of visibility */
    public void viewAllProjects() {
        if (currentUser.getRole() != Role.HDBMANAGER) {
            System.out.println("Not an HDB Manager.");
            return;
        }
        projects.forEach(System.out::println);
    }

    /** 4) Managers: view only projects they created */
    public void viewMyCreatedProjects() {
        if (currentUser.getRole() != Role.HDBMANAGER) {
            System.out.println("Not an HDB Manager.");
            return;
        }
        List<BTOProject> mine = projects.stream()
            .filter(p -> p.getManager().equalsIgnoreCase(currentUser.getName()))
            .collect(Collectors.toList());

        if (mine.isEmpty()) {
            System.out.println("You have not created any projects.");
        } else {
            mine.forEach(System.out::println);
        }
    }

    // stubs for edit/delete…
    public void editProject()   { throw new UnsupportedOperationException(); }
    public void deleteProject() { throw new UnsupportedOperationException(); }
}
