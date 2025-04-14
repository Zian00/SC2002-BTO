package views;
import java.util.List;
import models.BTOProject;
import models.User;
import models.enumerations.MaritalState;

public class BTOProjectView {

	public void displayApplicantMenu() {
		System.out.println("\n=== BTO Project Menu ===");
		System.out.println("1. Display All Available BTO Projects");
		System.out.println("2. Apply for a BTO Project");
		System.out.println("3. Submit Enquiry for a BTO project");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
	}
	public void displayOfficerMenu() {
		System.out.println("\n=== BTO Project Menu ===");
		System.out.println("1. Display All Available BTO Projects");
		System.out.println("2. Apply for a BTO Project");
		System.out.println("3. Submit Enquiry for a BTO project");
		System.out.println("4. Register as HDB Officer of a BTO Projects ");
		System.out.println("5. Display BTO Projects I'm handling");
		System.out.println("6. Back");
		System.out.println("Select an option: ");
	}
	public void displayManagerMenu() {
		System.out.println("\n=== BTO Project Menu ===");
		System.out.println("1. Display All BTO Projects");
		System.out.println("2. Add BTO Project");
		System.out.println("3. Edit BTO Project");
		System.out.println("4. Delete BTO Project");
		System.out.println("5. Back");
		System.out.print("Select an option: ");
	}
	public void displayManageBTOProjectMenu() { // Menu only available to manager to create/edit/delete BTO Project Listings
		System.out.println("\n=== BTO Project Editing Menu ===");
		System.out.println("1. Edit Project Name");
		System.out.println("2. Edit Neighborhood");
		System.out.println("3. Edit Number of 2 Room Flats");
		System.out.println("4. Edit 2 Room Flat Price");
		System.out.println("5. Edit Number of 3 Room Flats");
		System.out.println("6. Edit 3 Room Flat Price");
		System.out.println("7. Edit Application Opening Date");
		System.out.println("8. Edit Application Closing Date");
		System.out.println("9. Edit HDB Manager in charge");
		System.out.println("10. Edit Available HDB Officer SLots (Max10)");
		System.out.println("11. Back");
		System.out.print("Select an option to edit: ");
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

