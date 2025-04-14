package views;
public class ManagerView extends UserView {

	@Override
    public void displayMenu() {
        System.out.println("\n=== HDB Manager Menu ===");
		System.out.println("1. Enter Project Menu");
		System.out.println("2. Enter Application Menu");
		System.out.println("3. Enter Enquiry Menu");
		System.out.println("4. Change Password");
		System.out.println("5. Enter Officer Application Menu");
		System.out.println("6. Logout");
        System.out.print("Select an option: ");
    }

}