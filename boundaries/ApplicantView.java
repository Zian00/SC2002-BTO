package boundaries;

public class ApplicantView extends UserView {

    @Override
	public void displayMenu() {
		System.out.println("\n=== Main Menu ===");
		System.out.println("1. Enter Project Menu");
		System.out.println("2. Enter Application Menu");
		System.out.println("3. Enter Enquiry Menu");
		System.out.println("4. Change Password");
		System.out.println("5. Logout");
		System.out.print("Select an option: ");
	}

}