package views;

public class OfficerView extends ApplicantView {

    @Override
    public void displayMenu() {
        System.out.println("\n=== HDB Officer Menu ===");
        super.displayMenu();  // prints options 1–9 from ApplicantView
        System.out.println("10. View Projects I'm Handling");
        System.out.println("11. Register as Officer for Project");
        System.out.println("12. View Officer Registration Status");
        System.out.println("13. Respond to Enquiries for My Project");
        System.out.println("14. Book Flat for Successful Applicant");
        System.out.println("15. Generate Receipt");
        System.out.println("16. Logout");
        System.out.print("Select an option: ");
    }
}
