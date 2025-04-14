package views;
import java.util.List;
import models.BTOProject;
import models.User;
import models.enumerations.MaritalState;

public class BTOProjectView {

	public void displayApplicantMenu() {
		System.out.println("\n=== BTO Project Menu ===");
		System.out.println("1. Display All Available Projects");
		System.out.println("2. Apply for a Project");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
	}
	public void displayOfficerMenu() {
		System.out.println("\n=== BTO Project Menu ===");
		System.out.println("1. Display All Available Projects");
		System.out.println("2. Apply for Officer BTO Projects ");
		System.out.println("3. View my Enquiries");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
	}
	public void displayManagerMenu() {
		System.out.println("\n=== BTO Project Menu ===");
		System.out.println("1. Display All Available Projects");
		System.out.println("2. Change BTO Projects ");
		System.out.println("3. Change Enquiries");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
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
	
	  /**
     * Displays projects for an Applicant, showing only the room‑types
     * they’re eligible to view:
     *  - Singles ≥35 see only 2‑Room
     *  - Married ≥21 see both 2‑Room & 3‑Room
     *  - Everyone else sees a “not eligible” message
     */
    public void displayAvailableForApplicant(User user, List<BTOProject> projects) {
        // Determine eligibility
        boolean canSee2 = false, canSee3 = false;
        MaritalState ms = user.getMaritalStatus();
        int age = user.getAge();

        if (ms == MaritalState.SINGLE && age >= 35) {
            canSee2 = true;
        } else if (ms == MaritalState.MARRIED && age >= 21) {
            canSee2 = true;
            canSee3 = true;
        } else {
            System.out.println("You are not eligible to view any projects.");
            return;
        }

        // Header
        System.out.println("===================================");
        System.out.println("      Available Projects         ");
        System.out.println("===================================");

        if (projects == null || projects.isEmpty()) {
            System.out.println("No available projects.");
        } else {
            for (BTOProject p : projects) {
                System.out.println("Project ID:   " + p.getProjectID());
                System.out.println("Name:         " + p.getProjectName());
                System.out.println("Neighborhood: " + p.getNeighborhood());

                // Always show 2‑Room if allowed
                if (canSee2) {
                    System.out.printf("2-Room units: %d (Price: $%d)%n",
                                      p.getAvailable2Room(), p.getTwoRoomPrice());
                }
                // Show 3‑Room only if allowed
                if (canSee3) {
                    System.out.printf("3-Room units: %d (Price: $%d)%n",
                                      p.getAvailable3Room(), p.getThreeRoomPrice());
                }
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
		
		System.out.println("=== Projects You're Handling ===");
		for (BTOProject p : projects) {
			System.out.println(p);  // relies on your detailed toString()
			System.out.println("-----------------------------------");
		}
	}
}

