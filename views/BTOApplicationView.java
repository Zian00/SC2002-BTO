package views;
import java.util.List;
import models.BTOApplication;
import models.User;

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
		//case 3 needs two checks, 1 for applicants they manage
		//another for status = Successful
		System.out.println("3. Booking for successful applicant");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
	}
	public void displayManagerMenu() {
		System.out.println("\n=== BTO Application Menu ===");
		System.out.println("1. Display All Applications Handled By You");
		System.out.println("2. Approval / Rejection for Application"); // filter by applicationType - only display Application  
		System.out.println("3. Approval for Withdrawal"); // filter by applicationType - only display Withdrawal
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
	public void displayUserApplication(List<BTOApplication> applications, User currentUser) {
		// TODO - implement BTOApplicationView.displayUserApplication
		throw new UnsupportedOperationException();
	}

	public void generateReport() {
		// TODO - implement BTOApplicationView.generateReport
		throw new UnsupportedOperationException();
	}

}