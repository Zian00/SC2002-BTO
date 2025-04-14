package views;

public class OfficerView extends ApplicantView {

    @Override
    public void displayMenu() {
        System.out.println("\n=== HDB Officer Menu ===");
        // Applicant capabilities
        System.out.println("1. View Available Projects");
        System.out.println("2. View My Applications");
        System.out.println("3. View My Enquiries");
        System.out.println("4. Change Password");

        // Officer registration
        System.out.println("5. View My Officer Registrations");
        System.out.println("6. Register as Officer for a Project");

        // Officer own projects
        System.out.println("7. View Projects I'm Handling");
        System.out.println("8. Respond to Enquiries for My Projects");

        // Helping applicants / receipt
        System.out.println("9. Book Flat for Successful Applicant (and generate receipt)");
        System.out.println("10. Logout");
        System.out.print("Select an option: ");
    }
}
