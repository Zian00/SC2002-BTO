package views;
public class ManagerView extends UserView {

	@Override
    public void displayMenu() {
        System.out.println("\n=== HDB Manager Menu ===");
        System.out.println("1. Create New BTO Project");
        System.out.println("2. Edit Existing Project");
        System.out.println("3. Delete Project");
        System.out.println("4. Toggle Project Visibility");
        System.out.println("5. View All Projects");
        System.out.println("6. View My Projects Only");
        System.out.println("7. View Officer Registration Requests");
        System.out.println("8. Approve/Reject Officer Registrations");
        System.out.println("9. Approve/Reject Applicant Applications");
        System.out.println("10. Approve/Reject Withdrawals");
        System.out.println("11. Generate Booking Report");
        System.out.println("12. View All Enquiries");
        System.out.println("13. Respond to Enquiries for My Projects");
        System.out.println("14. Change Password");
        System.out.println("15. Filter/Sort Projects");
        System.out.println("16. Logout");
        System.out.print("Select an option: ");
    }

}