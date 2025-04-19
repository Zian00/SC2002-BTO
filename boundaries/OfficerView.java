package boundaries;

/**
 * View class for HDB Officer main menu interactions in the BTO system.
 * <p>
 * Extends {@link ApplicantView} and customizes the menu for HDB Officers,
 * providing options relevant to officer responsibilities.
 * </p>
 */
public class OfficerView extends ApplicantView {

    /**
     * Displays the main menu options for HDB Officers.
     * Overrides the applicant menu with officer-specific options.
     */
    @Override
    public void displayMenu() {
        System.out.println("\n=== HDB Officer Menu ===");
        System.out.println("1. Enter Project Menu");
        System.out.println("2. Enter Application Menu");
        System.out.println("3. Enter Enquiry Menu");
        System.out.println("4. Change Password");
        System.out.println("5. Enter Officer Application Menu");
        System.out.println("6. Logout");
        System.out.print("Select an option: ");
    }
}
