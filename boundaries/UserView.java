package boundaries;

import controllers.UserCTRL;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * View class for user authentication and main menu interactions in the BTO system.
 * <p>
 * Handles login, logout, and displays common menu options for users.
 * </p>
 */
public class UserView {
    /** Pattern for validating NRIC: must start with S or T, then 7 digits, then an uppercase letter. */
    private static final Pattern NRIC_PATTERN = Pattern.compile("^[ST]\\d{7}[A-Z]$");

    /**
     * Handles the login flow for a user, including NRIC and password validation.
     *
     * @param sc       The Scanner object for user input.
     * @param userCTRL The UserCTRL instance for authentication.
     * @return true if login is successful, false otherwise.
     */
    public boolean loginFlow(Scanner sc, UserCTRL userCTRL) {
        displayLogin();

        // Uncomment for actual run, for now use this user for testing
        // Change accordingly
        // String nric = "T1234567E";
        // String password = "a";

        System.out.print("NRIC (uppercase only): ");
        String nric = sc.nextLine().trim();

        // enforce uppercase
        if (!nric.equals(nric.toUpperCase())) {
        System.out.println("Error: NRIC must be in uppercase.");
        return false;
        }

        // validate structure
        if (!NRIC_PATTERN.matcher(nric).matches()) {
        System.out.println("Invalid NRIC format. It must:");
        System.out.println("- Start with 'S' or 'T'");
        System.out.println("- Followed by exactly 7 digits");
        System.out.println("- End with an uppercase letter");
        return false;
        }

        System.out.print("Password: ");
        String password = sc.nextLine().trim();

        if (userCTRL.login(nric, password)) {
            System.out.println("Login successful! Welcome, " + userCTRL.getCurrentUser().getName() + " [" + userCTRL.getCurrentUser().getRole()  +"]");
            return true;
        } else {
            System.out.println("Invalid credentials. Returning to main menu.");
            return false;
        }
    }

    /**
     * Displays the login header.
     */
    public void displayLogin() {
        System.out.println("=== HDB Hub Login ===");
        System.out.println("Please enter your credentials below:");
    }

    /**
     * Displays the logout confirmation message.
     */
    public void displayLogout() {
        System.out.println("\n=== Logout ===");
        System.out.println("You have been successfully logged out. Goodbye!");
    }

    /**
     * Displays the common main menu options for users.
     */
    public void displayMenu() {
        System.out.println("\n=== Main Menu ===");
        System.out.println("1. Change Password");
        System.out.println("2. Filter/Sort Projects");
        System.out.println("3. Logout");
        System.out.print("Select an option: ");
    }
}
