package controllers;

import java.util.ArrayList;
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
    
    /**
     * Filters projects based on the current user's role.
     * For Applicants and HDB Officers, the logic is the same (only visible projects).
     * For HDB Managers, only projects managed by the manager are returned.
     */
    public List<BTOProject> getFilteredProjects() {
        Role role = currentUser.getRole();
        List<BTOProject> filtered;
        switch (role) {
            case APPLICANT:
            case HDBOFFICER:
                filtered = projects.stream()
                            .filter(BTOProject::isVisibility)
                            .collect(Collectors.toList());
                break;
            case HDBMANAGER:
                filtered = projects.stream()
                            .filter(p -> p.getManager().equalsIgnoreCase(currentUser.getNRIC()))
                            .collect(Collectors.toList());
                break;
            default:
                filtered = new ArrayList<>();
        }
        return filtered;
    }

    // stubs for edit/delete…
    public void editProject()   { throw new UnsupportedOperationException(); }
    public void deleteProject() { throw new UnsupportedOperationException(); }
}
