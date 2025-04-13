package views;

public class ApplicantView extends UserView {

    @Override
	public void displayMenu() {
		System.out.println("\n=== Main Menu ===");
		System.out.println("1. View Available Projects");
		System.out.println("2. View My Applications");
		System.out.println("3. View my Enquiries");
		System.out.println("4. Change Password");
		System.out.println("5. Logout");
		System.out.print("Select an option: ");
		
	}

}