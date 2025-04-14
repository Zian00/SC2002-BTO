package views;
public class ManagerView extends UserView {

	@Override
    public void displayMenu() {
        System.out.println("\n=== HDB Manager Menu ===");
        System.out.println("1. View All Projects");           // added new option
        System.out.println("2. View My Created Projects");
        System.out.println("3. Create Project");
        System.out.println("4. Edit Project");
        System.out.println("5. Delete Project");
        System.out.println("6. Change Password");
        System.out.println("7. Logout");
        System.out.print("Select an option: ");
    }

}