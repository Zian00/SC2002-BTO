package boundaries;

/**
 * View class for applicant main menu interactions in the BTO system.
 * <p>
 * Extends {@link UserView} and customizes the menu for applicants,
 * providing options relevant to applicant responsibilities.
 * </p>
 */
public class ApplicantView extends UserView {

    /**
     * Displays the main menu options for applicants.
     * Overrides the user menu with applicant-specific options.
     */
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