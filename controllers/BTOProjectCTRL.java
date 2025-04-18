package controllers;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import models.BTOApplication;
import models.BTOProject;
import models.FilterSettings;
import models.User;
import models.HDBManager;
import models.enumerations.FlatType;
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
        BTOProject existing = null;
        // only manager can edit project          
        if (currentUser instanceof HDBManager manager) {
            existing = manager.getProjectById(projects, projectId);
        }
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

    //FILTER SETTINGS
     /** apply both “visibility + eligibility” _and_ the user’s custom price/room filters */
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
            System.out.println("Sorry as you are only able to view 2 rooms, and are asking to see 3 rooms, the view is empty");
            return Collections.emptyList();
        }
        if ("2-Room".equals(fs.getRoomType()) && !canSee2) {
            return Collections.emptyList();
        }
        // 3) apply them
        return base.stream()
            .filter(p -> {
                // room‐type filter?
                if (fs.getRoomType() != null) {
                    if (fs.getRoomType().equals("2‑Room") && p.getAvailable2Room() == 0) return false;
                    if (fs.getRoomType().equals("3‑Room") && p.getAvailable3Room() == 0) return false;
                }
                return true;
            })
            .filter(p -> {
                // price filter (choose correct price)
                int price = fs.getRoomType()!=null && fs.getRoomType().equals("3‑Room")
                                ? p.getThreeRoomPrice()
                                : p.getTwoRoomPrice();
                if (fs.getMinPrice() != null && price < fs.getMinPrice()) return false;
                if (fs.getMaxPrice() != null && price > fs.getMaxPrice()) return false;
                return true;
            })
            .collect(Collectors.toList());
    }

    /** save new filter settings back to the user record + CSV */
    public void updateUserFilterSettings(User user, FilterSettings fs) {
        user.setFilterSettings(fs.toCsv());
        // assume UserCTRL has saveUserData()
        new UserCTRL().saveUserData();
    }
    // stubs for edit/delete…
    // public void editProject() {
    //     throw new UnsupportedOperationException();
    // }

    // public void deleteProject() {
    //     throw new UnsupportedOperationException();
    // }

    public void saveProjects() {
        repo.writeBTOProjectToCSV(projects);
    }
}
