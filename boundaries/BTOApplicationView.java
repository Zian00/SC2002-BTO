package boundaries;

import entity.BTOApplication;
import entity.BTOProject;
import entity.User;
import entity.enumerations.ApplicationStatus;
import entity.enumerations.ApplicationType;
import entity.enumerations.MaritalState;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class BTOApplicationView {

	public void displayApplicantMenu() {
		System.out.println("\n=== BTO Application Menu ===");
		System.out.println("1. Display All My Applications");
		System.out.println("2. Withdraw my application");
		System.out.println("3. Back");
		System.out.print("Select an option: ");
	}

	public void displayOfficerMenu() {
		System.out.println("\n=== BTO Application Menu ===");
		System.out.println("1. Display All My Applications");
		System.out.println("2. Withdraw my application");
		System.out.println("3. Booking for successful applicant");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
	}

	public void displayManagerMenu() {
		System.out.println("\n=== BTO Application Menu ===");
		System.out.println("1. Display All Applications Handled By You");
		System.out.println("2. Approval / Rejection for Application");
		System.out.println("3. Approval for Withdrawal");
		System.out.println("4. Generate / Filter report of all APPLICANTS under projects handled by you");
		System.out.println("5. Back");
		System.out.print("Select an option: ");
	}

	/**
	 * 
	 * @param applications
	 */
	public void displayAllApplications(List<BTOApplication> applications) {
		if (applications.isEmpty()) {
			System.out.println("No applications handled by you.");
			return;
		}

		System.out.println("\n=== Applications Handled By You ===");
		for (BTOApplication app : applications) {
			System.out.println("Application ID: " + app.getApplicationId());
			System.out.println("Applicant: " + app.getApplicantNRIC());
			System.out.println("Flat Type: " + app.getFlatType());
			System.out.println("Application Type: " + app.getApplicationType());
			System.out.println("Status: " + app.getStatus());
			System.out.println("-------------------------------");
		}
	}

	/**
	 * 
	 * @param applications
	 * @param currentUser
	 */
	public void displayUserApplication(List<BTOApplication> applications, User currentUser, List<BTOProject> projects) {
		if (applications.isEmpty()) {
			System.out.println("You have no applications.");
			return;
		}
		System.out.println("\n=== My Applications ===");
		for (BTOApplication app : applications) {
			System.out.println("Application ID: " + app.getApplicationId());
			System.out.println("Project ID: " + app.getProjectID());
			// Lookup project details for this application
			var matchedProject = projects.stream()
					.filter(p -> p.getProjectID() == app.getProjectID())
					.findFirst();
			if (matchedProject.isPresent()) {
				var project = matchedProject.get();
				System.out.println("Project Name: " + project.getProjectName());

				System.out.println("Project Neighbourhood: " + project.getNeighborhood());

			} else {
				System.out.println("Project details not found.");
			}
			System.out.println("Flat Type: " + app.getFlatType());
			System.out.println("Application Type: " + app.getApplicationType());
			System.out.println("Status: " + app.getStatus());
			System.out.println("-------------------------------");
		}
	}

	/**
	 * Displays only the pending (non‑withdrawn) applications.
	 * Returns true if pending applications were displayed,
	 * or false if there are none.
	 */
	public boolean displayPendingApplications(List<BTOApplication> applications, List<BTOProject> projects) {
		var pendingApps = applications.stream()
				.filter(app -> app.getStatus() != ApplicationStatus.UNSUCCESSFUL
						&& app.getApplicationType() == ApplicationType.APPLICATION)
				.collect(Collectors.toList());
		if (pendingApps.isEmpty()) {
			System.out.println("No active applications available for withdrawal.");
			return false;
		}
		System.out.println("\n=== Active Applications ===");
		for (BTOApplication app : pendingApps) {
			System.out.println("Application ID: " + app.getApplicationId()
					+ " | Project ID: " + app.getProjectID()
					+ " | Application Type: " + app.getApplicationType()
					+ " | Flat Type: " + app.getFlatType()
					+ " | Status: " + app.getStatus());
			// Lookup project details for this application
			var matchedProject = projects.stream()
					.filter(p -> p.getProjectID() == app.getProjectID())
					.findFirst();

			if (matchedProject.isPresent()) {
				var project = matchedProject.get();
				System.out.println("   =>Project Name: " + project.getProjectName());
				System.out.println("   =>Project Neighbourhood: " + project.getNeighborhood());
				System.out.println(
						"-----------------------------------------------------------------------------------------------------------------------");
			} else {
				System.out.println("    Project details not found.");
			}
		}
		return true;
	}

	/**
	 * Displays successful applications (with status SUCCESSFUL) along with project
	 * details.
	 * Returns true if there is at least one application to display.
	 */
	public boolean displaySuccessfulApplications(List<BTOApplication> applications, List<BTOProject> projects) {
		var successfulApps = applications.stream()
				.filter(app -> app.getStatus() == ApplicationStatus.SUCCESSFUL)
				.collect(Collectors.toList());
		if (successfulApps.isEmpty()) {
			System.out.println("No successful applications available for booking.");
			return false;
		}
		System.out.println("\n=== Successful Applications ===");
		for (BTOApplication app : successfulApps) {
			System.out.println("Application ID: " + app.getApplicationId()
					+ " | Applicant NRIC: " + app.getApplicantNRIC()
					+ " | Flat Type: " + app.getFlatType());
			// Lookup project details for this application
			var matchedProj = projects.stream()
					.filter(p -> p.getProjectID() == app.getProjectID())
					.findFirst();
			if (matchedProj.isPresent()) {
				var proj = matchedProj.get();
				System.out.println("   => Project Name: " + proj.getProjectName()
						+ ", Neighbourhood: " + proj.getNeighborhood());
			} else {
				System.out.println("   => Project details not found.");
			}
		}
		return true;
	}

	/**
     * Prompts the user to filter by marital status.
     * The user may enter "MARRIED" or "SINGLE" (case insensitive) or press Enter for no filter.
     *
     * @param sc the Scanner used for reading user input.
     * @return the MaritalState if valid input is provided; otherwise, null.
     */
	public MaritalState promptMaritalStatusFilter(Scanner sc) {
        System.out.print("Filter by Marital Status (MARRIED or SINGLE) or press Enter for all: ");
        String input = sc.nextLine().trim().toUpperCase();
        if (input.isEmpty() || input.equals("ALL")) {
            return null;
        }
        try {
            return MaritalState.valueOf(input);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid marital status. No filter applied.");
            return null;
        }
    }

	/**
     * Prompts the user to filter by flat type.
     * The user may enter "TWOROOM" or "THREEROOM" (case insensitive) or press Enter for no filter.
     *
     * @param sc the Scanner used for reading user input.
     * @return the flat type as a String if valid input is provided; otherwise, null.
     */
    public String promptFlatTypeFilter(Scanner sc) {
        System.out.print("Filter by Flat Type (TWOROOM or THREEROOM) or press Enter for all: ");
        String input = sc.nextLine().trim().toUpperCase();
        if (input.isEmpty() || input.equals("ALL")) {
            return null;
        }
        if (input.equals("TWOROOM") || input.equals("THREEROOM")) {
            return input;
        }
        System.out.println("Invalid flat type. No filter applied.");
        return null;
    }

    /**
     * Prompts the user to enter a minimum age to filter applications.
     * The user should provide an integer value, or press Enter for no filter.
     *
     * @param sc the Scanner used for reading user input.
     * @return the minimum age as an Integer if valid input is provided; otherwise, null.
     */
    public Integer promptMinAge(Scanner sc) {
        System.out.print("Filter by Minimum Age or press Enter for all: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()){
            return null;
        }
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid minimum age. No filter applied.");
            return null;
        }
    }

    /**
     * Prompts the user to enter a maximum age to filter applications.
     * The user should provide an integer value, or press Enter for no filter.
     *
     * @param sc the Scanner used for reading user input.
     * @return the maximum age as an Integer if valid input is provided; otherwise, null.
     */
    public Integer promptMaxAge(Scanner sc) {
        System.out.print("Filter by Maximum Age or press Enter for all: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()){
            return null;
        }
        try {
            return Integer.valueOf(input);
        } catch (NumberFormatException e) {
            System.out.println("Invalid maximum age. No filter applied.");
            return null;
        }
    }

    /**
     * Prompts the user to filter by neighbourhood.
     * The user may enter a neighbourhood name or press Enter for no filter.
     *
     * @param sc the Scanner used for reading user input.
     * @return the neighbourhood filter as a String if provided; otherwise, null.
     */
    public String promptNeighbourhoodFilter(Scanner sc) {
        System.out.print("Filter by Neighbourhood or press Enter for all: ");
        String input = sc.nextLine().trim();
        if (input.isEmpty()){
            return null;
        }
        return input;
    }
}