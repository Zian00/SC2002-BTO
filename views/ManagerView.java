package views;
public class ManagerView extends UserView {

	@Override
    public void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. View All Projects");
        System.out.println("2. View Applications");
        System.out.println("3. View Enquiries");
        System.out.println("4. Change Password");
        System.out.println("5. View Officer Applications");
        System.out.println("6. Logout");
        System.out.print("Select an option: ");
    }

}