package views;
import java.util.List;
import java.util.stream.Collectors;

import models.BTOProject;

public class BTOProjectView {

	public void displayMenu() {
		// TODO - implement BTOProjectView.displayMenu
		throw new UnsupportedOperationException();
	}

	/**
     * Displays all projects with divider lines.
     * @param projects the list of BTOProject objects to display
     */
    public void displayAllProject(List<BTOProject> projects) {
        System.out.println("===================================");
        System.out.println("      Available Projects         ");
        System.out.println("===================================");
        if (projects == null || projects.isEmpty()) {
            System.out.println("No available projects.");
        } else {
            for (BTOProject project : projects) {
                System.out.println(project);
                System.out.println("-----------------------------------");
            }
        }
        System.out.println("===================================");
    }

	public BTOProject promptNewProject() {
		// TODO - implement BTOProjectView.promptNewProject
		throw new UnsupportedOperationException();
	}

	public int promptProjectID() {
		// TODO - implement BTOProjectView.promptProjectID
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param proj
	 */
	public void editProjectDetails(BTOProject proj) {
		// TODO - implement BTOProjectView.editProjectDetails
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param msg
	 */
	public void showMessage(String msg) {
		// TODO - implement BTOProjectView.showMessage
		throw new UnsupportedOperationException();
	}

	public void displayHandledProjects(List<BTOProject> projects) {
		if (projects.isEmpty()) {
			System.out.println("You have no approved officer assignments.");
			return;
		}
		
		System.out.println("=== Projects You’re Handling ===");
		for (BTOProject p : projects) {
			System.out.println(p);  // relies on your detailed toString()
			System.out.println("-----------------------------------");
		}
	}
}

