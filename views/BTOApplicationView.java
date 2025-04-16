package views;

import java.util.List;
import java.util.stream.Collectors;

import models.BTOApplication;
import models.BTOProject;
import models.User;
import models.enumerations.ApplicationStatus;
import models.enumerations.ApplicationType;

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
		System.out.println("4. Back");
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

	public void generateReport() {
		// TODO - implement BTOApplicationView.generateReport

	}

}