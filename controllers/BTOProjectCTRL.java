package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import models.BTOApplication;
import models.BTOProject;
import models.User;
import models.enumerations.MaritalState;
import models.enumerations.Role;
import models.repositories.BTOProjectCSVRepository;

public class BTOProjectCTRL {

    private List<BTOProject> projects;
    private User currentUser;
    private BTOProjectCSVRepository repo = new BTOProjectCSVRepository();

    public BTOProjectCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.projects = repo.readBTOProjectFromCSV();
    }

    /** For manager: get any project by ID */
    public BTOProject getProjectById(int id) {
        return projects.stream()
                .filter(p -> p.getProjectID() == id)
                .findFirst()
                .orElse(null);
    }

    /** Returns max existing ID + 1 (dont need to manually key in) */
    public int getNextProjectID() {
        return projects.stream()
                .mapToInt(BTOProject::getProjectID)
                .max()
                .orElse(0) + 1;
    }

    /** Create a new project */
    public void createProject(BTOProject p) {
        projects.add(p);
        repo.writeBTOProjectToCSV(projects);
    }

    /** Edit an existing project: replace fields on the found object */
    public boolean editProject(int projectId, BTOProject updated) {
        BTOProject existing = getProjectById(projectId);
        if (existing == null)
            return false;

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

    /** Delete a project by ID */
    public boolean deleteProject(int projectId) {
        boolean removed = projects.removeIf(p -> p.getProjectID() == projectId);
        if (removed) {
            repo.writeBTOProjectToCSV(projects);
        }
        return removed;
    }

    /**
     * Filters projects based on the current user's role.
     * For Applicants and HDB Officers, the logic is the same (only visible
     * projects).
     * For HDB Managers, only projects managed by the manager are returned.
     */
    public List<BTOProject> getFilteredProjects() {
        Role role = currentUser.getRole();
        List<BTOProject> filtered = new ArrayList<>(); // initialise filtered to an empty list due to error
        switch (role) {
            case APPLICANT -> {
                MaritalState ms = currentUser.getMaritalStatus();
                int age = currentUser.getAge();
                System.out.println("Debug: MaritalState: " + ms + ", Age: " + age);
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
            case HDBOFFICER -> filtered = projects.stream()
                    .filter(BTOProject::isVisibility)
                    .collect(Collectors.toList());
            case HDBMANAGER -> filtered = projects.stream()
                    .filter(p -> p.getManager().equalsIgnoreCase(currentUser.getNRIC()))
                    .collect(Collectors.toList());
            default -> filtered = new ArrayList<>();
        }
        return filtered;
    }

    public List<BTOProject> getAllProjects() {
        // return a copy so callers can’t edit the internal list
        return new ArrayList<>(projects);
    }

    // HDBOfficer gethandled projects filter
    // gets approvedofficer NRIC match and ignores visibility
    public List<BTOProject> getHandledProjects() {
        String me = currentUser.getNRIC();
        return projects.stream()
                .filter(p -> p.getApprovedOfficer() != null
                        && p.getApprovedOfficer().stream()
                                .anyMatch(nric -> nric.equalsIgnoreCase(me)))
                .collect(Collectors.toList());
    }

    // stubs for edit/delete…
    public void editProject() {
        throw new UnsupportedOperationException();
    }

    public void deleteProject() {
        throw new UnsupportedOperationException();
    }
}
