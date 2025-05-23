package controllers;

import boundaries.ApplicantView;
import boundaries.BTOApplicationView;
import boundaries.BTOProjectView;
import boundaries.EnquiryView;
import boundaries.ManagerView;
import boundaries.OfficerApplicationView;
import boundaries.OfficerView;
import boundaries.UserView;
import entity.Applicant;
import entity.User;
import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import entity.interfaces.IApplicantRepository;
import entity.repositories.ApplicantCSVRepository;
import entity.repositories.UserCSVRepository;
import java.util.List;
import java.util.Scanner;
// No need for regex import if using character checks

/**
 * Controller class for managing user-related operations in the BTO system.
 * <p>
 * Handles user authentication, user data loading/saving, password changes,
 * and navigation to role-specific menus and views.
 * </p>
 */
public class UserCTRL {
    /** List of all users loaded from the repository. */
    private List<User> userList;
    /** The currently logged-in user. */
    private User currentUser;
    /** Repository for reading/writing user data from/to CSV. */
    private final UserCSVRepository userRepo = new UserCSVRepository();

    //repo for applicant
    private final IApplicantRepository applicantRepository;
    
    // Default Constructor
    public UserCTRL() {
        this(new ApplicantCSVRepository());
    }

    //used for creation of user
    public UserCTRL(IApplicantRepository applicantRepository) {
        this.applicantRepository = applicantRepository;
    }
    
    /**
     * Loads user data from the CSV repository into the user list.
     */
    public void loadUserData() {
        userList = userRepo.readUserFromCSV();
    }

    /**
     * Saves the current user list to the CSV repository.
     * If the user list is not loaded, it attempts to load it first.
     */
    public void saveUserData() {
        if (this.userList == null) {
            // Instead of printing an error, load the users here.
            loadUserData();
            if (this.userList == null) {
                System.err.println("ERROR: no users loaded, skipping save");
                return;
            }
        }
        userRepo.writeUserToCSV(this.userList);
    }

  /**
   * The `createNewAccount` function in Java prompts the user to input details for a new account,
   * validates the input, creates an `Applicant` object, and saves it using an
   * `ApplicantCSVRepository`.
   * 
   * @param sc The `sc` parameter in the `createNewAccount` method is of type `Scanner`. It is used to
   * read input from the user during the account creation process. The `Scanner` class in Java is used
   * for obtaining input of primitive types like int, double, etc., and strings from the
   */
    public void createNewAccount(Scanner sc) {
        System.out.println("\n=== Create New Account ===");

        // Get NRIC with validation
        String nric;
        while (true) {
            System.out.print("Enter NRIC (must start with T or S, followed by 7 digits, and end with a capital letter): ");
            nric = sc.nextLine().trim();
            if (nric.isEmpty()) {
                System.out.println("NRIC cannot be blank.");
            } else if (!nric.matches("^[TS]\\d{7}[A-Z]$")) {
                System.out.println("Invalid NRIC format. Please ensure it starts with T or S, followed by 7 digits, and ends with a capital letter.");
            } else if (getUserByNRIC(nric) != null) { // Check for duplicate NRIC
                System.out.println("This NRIC is already registered. Please enter a different NRIC.");
            } else {
                break;
            }
        }

        // Get Name with validation
        String name;
        while (true) {
            System.out.print("Enter Name: ");
            name = sc.nextLine().trim();
            if (name.isEmpty()) {
                System.out.println("Name cannot be blank.");
            } else if (!name.matches("^[A-Z][a-z]*( [A-Z][a-z]*)*$")) {
                System.out.println("Name must contain only alphabets with each word starting with a capital letter.");
            } else {
                break;
            }
        }

        // Get Age with error checking
        int age = 0;
        while (true) {
            System.out.print("Enter Age: ");
            String ageInput = sc.nextLine().trim();
            try {
                age = Integer.parseInt(ageInput);
                if (age <= 0) {
                    System.out.println("Age must be a positive number.");
                } else {
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number for age.");
            }
        }

        // Get Marital Status
        MaritalState maritalStatus = null;
        while (true) {
            System.out.print("Enter Marital Status (SINGLE/MARRIED): ");
            String maritalInput = sc.nextLine().trim().toUpperCase();
            try {
                maritalStatus = MaritalState.valueOf(maritalInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid marital status. Please enter SINGLE or MARRIED.");
            }
        }
        // Get Role
        Role role = null;
        while (true) {
            System.out.print("Enter Role (APPLICANT/HDBOFFICER/HDBMANAGER): ");
            String roleInput = sc.nextLine().trim().toUpperCase();
            try {
                role = Role.valueOf(roleInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid role. Please enter APPLICANT, HDBOFFICER, or HDBMANAGER.");
            }
        }

        // password
        String password = "Passw0rd";

        /// Create Applicant object
        Applicant applicant = new Applicant(nric, name, password, age, maritalStatus, "", role, applicantRepository);

        // Save the applicant using the ApplicantCSVRepository
        try {
            applicant.save();
            System.out.println("Account created successfully!");
        } catch (Exception e) {
            System.out.println("An error occurred while saving the account: " + e.getMessage());
        }

        loadUserData();
    }

    // --------------------------------------------------------------------------------------------------
    // Central Menu: Navigation for Logged-in Users
    // --------------------------------------------------------------------------------------------------
    /**
     * Runs the central menu for the currently logged-in user, displaying options
     * and routing to the appropriate controllers and views based on the user's role.
     *
     * @param sc       The Scanner object for user input.
     * @param userCTRL The UserCTRL instance (usually this).
     */
    public void runCentralMenu(Scanner sc, UserCTRL userCTRL) {
        Role role = userCTRL.getCurrentUser().getRole();

        // Instantiate Views
        UserView baseView = switch (role) {
            case APPLICANT -> new ApplicantView();
            case HDBOFFICER -> new OfficerView();
            case HDBMANAGER -> new ManagerView();
            default -> new UserView(); // Fallback, though should ideally not happen if role is valid
        };
        BTOProjectView projectView = new BTOProjectView();
        BTOApplicationView btoApplicationView = new BTOApplicationView();
        EnquiryView enquiryView = new EnquiryView();
        OfficerApplicationView officerAppView = new OfficerApplicationView();

        // Instantiate Controllers
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        BTOApplicationCTRL applicationCTRL = new BTOApplicationCTRL(userCTRL.getCurrentUser());
        EnquiryCTRL enquiryCTRL = new EnquiryCTRL(userCTRL.getCurrentUser());
        OfficerApplicationCTRL officerAppCTRL = new OfficerApplicationCTRL(userCTRL.getCurrentUser());

        boolean keepRunning = true; // Flag to control the loop
        while (keepRunning) {
            baseView.displayMenu();

            String opt = sc.nextLine().trim();
            boolean optionHandled = false; // Flag to track if an option was processed

            // --- Common options ---
            switch (opt) {
                case "1" -> {
                    projectCTRL.updateProjectVisibility();
                    projectCTRL.runProjectMenu(sc, userCTRL, projectCTRL, projectView, applicationCTRL, officerAppCTRL, officerAppView, enquiryView, enquiryCTRL, btoApplicationView);
                    optionHandled = true;
                }
                case "2" -> {
                    applicationCTRL.runApplicationMenu(sc, userCTRL, projectCTRL, applicationCTRL, btoApplicationView);
                    optionHandled = true;
                }
                case "3" -> {
                    enquiryCTRL.runEnquiryMenu(sc, userCTRL, projectCTRL, enquiryView, enquiryCTRL);
                    optionHandled = true;
                }
                case "4" -> {
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null) {
                        keepRunning = false; // Exit loop if logged out after password change
                    }
                    optionHandled = true;
                }
            }

            // --- Role-specific extra options ---
            if (!optionHandled && role != null) {
                switch (role) {
                    case APPLICANT -> {
                        if ("5".equals(opt)) { // Logout
                            userCTRL.setCurrentUser(null);
                            baseView.displayLogout();
                            keepRunning = false; // Exit loop
                            optionHandled = true;
                        }
                    }
                    case HDBOFFICER -> {
                        if ("5".equals(opt)) { // Enter Officer Application Menu
                            officerAppCTRL.runOfficerApplicationMenu(sc, officerAppCTRL, officerAppView, projectCTRL);
                            optionHandled = true;
                        } else if ("6".equals(opt)) { // Logout
                            userCTRL.setCurrentUser(null);
                            baseView.displayLogout();
                            keepRunning = false; // Exit loop
                            optionHandled = true;
                        }
                    }
                    case HDBMANAGER -> {
                        if ("5".equals(opt)) { // Enter Officer Application Menu
                            officerAppCTRL.runOfficerApplicationMenu(sc, officerAppCTRL, officerAppView, projectCTRL);
                            optionHandled = true;
                        } else if ("6".equals(opt)) { // Logout
                            userCTRL.setCurrentUser(null);
                            baseView.displayLogout();
                            keepRunning = false; // Exit loop
                            optionHandled = true;
                        }
                    }
                    default -> {
                    }
                }
                // Should not happen with defined roles, but good practice
                            }

            // If no valid option was handled (common or role-specific)
            if (!optionHandled && keepRunning) { // Check keepRunning to avoid message after logout
                 System.out.println("Invalid choice, please try again.");
            }
        }
    }


    // --------------------------------------------------------------------------------------------------
    // Change Password Handler
    // --------------------------------------------------------------------------------------------------
    /**
     * Handles the password change process for the current user.
     * Prompts for current password, new password, confirmation, and updates it after validation.
     *
     * @param sc       The Scanner object for user input.
     * @param userCTRL The UserCTRL instance.
     */
    public void handleChangePassword(Scanner sc, UserCTRL userCTRL) {
        // Ensure user is logged in before attempting password change
        if (userCTRL.getCurrentUser() == null) {
            System.out.println("Error: You must be logged in to change your password.");
            return;
        }

        System.out.print("Enter current password: ");
        String currentPass = sc.nextLine().trim();

        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();

        System.out.print("Confirm new password: ");
        String confirmPass = sc.nextLine().trim();

        // Check if new password and confirmation match
        if (!newPass.equals(confirmPass)) {
            System.out.println("New passwords do not match. Password change cancelled.");
            return;
        }

        // Call the updated changePassword method with both passwords
        userCTRL.changePassword(currentPass, newPass);
        // The changePassword method now handles logout on success
    }

    /**
     * Attempts to log in a user with the given NRIC and password.
     * Sets the current user if successful.
     *
     * @param NRIC     The NRIC of the user.
     * @param password The password of the user.
     * @return true if login is successful, false otherwise.
     */
    public boolean login(String NRIC, String password) {
        if (userList == null)
            loadUserData();
        for (User u : userList) {
            if (u.getNRIC().equalsIgnoreCase(NRIC) && u.getPassword().equals(password)) {
                currentUser = u;
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the filter settings for the current user and persists the change.
     *
     * @param me        The user whose filter settings are to be updated (can be derived from currentUser).
     * @param filterCsv The new filter settings in CSV format.
     */
    public void updateFilterSettings(User me, String filterCsv) {
        if (currentUser == null) {
            System.err.println("Error: Cannot update filter settings. No user logged in.");
            return;
        }
        // Ensure the user passed is the current user, or just use currentUser directly
        if (me == null || !me.getNRIC().equalsIgnoreCase(currentUser.getNRIC())) {
             System.err.println("Warning: Attempting to update filter settings for a user different from the logged-in user. Using logged-in user.");
        }

        if (userList == null) {
            loadUserData();
            if (userList == null) {
                 System.err.println("Error: User data could not be loaded. Filter settings not saved.");
                 return;
            }
        }

        currentUser.setFilterSettings(filterCsv);
        boolean found = false;
        // Also synchronize the change into userList
        for (User u : userList) {
            if (u.getNRIC().equalsIgnoreCase(currentUser.getNRIC())) {
                u.setFilterSettings(filterCsv);
                found = true;
                break;
            }
        }
        if (found) {
            saveUserData();
        } else {
             System.err.println("Error: Logged-in user not found in the main user list. Filter settings not saved to file.");
        }
    }

    /**
     * Changes the current user's password after verifying the current password
     * and validating the new password's complexity.
     * Logs out the user after a successful change.
     *
     * @param currentPassword The user's claimed current password.
     * @param newPassword     The desired new password.
     * @return true if the change succeeded, false otherwise.
     */
    public boolean changePassword(String currentPassword, String newPassword) {
        if (currentUser == null) {
            System.out.println("Error: no user is currently logged in.");
            return false;
        }

        // 1. Verify current password
        if (!currentUser.getPassword().equals(currentPassword)) {
            System.out.println("Incorrect old password. Password change failed.");
            return false;
        }

        // 2. Validate new password complexity
        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasDigit = false;
        boolean hasForbiddenChar = false;
        final int MIN_LENGTH = 8;

        for (char c : newPassword.toCharArray()) {
            // Check for forbidden characters: comma, double quotes, or newline characters
            if (c == ',' || c == '\"' || c == '\n' || c == '\r') {
                hasForbiddenChar = true;
            }
            // Check for uppercase, lowercase, and digit
            if (Character.isUpperCase(c)) {
                hasUpper = true;
            } else if (Character.isLowerCase(c)) {
                hasLower = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            // Optimization: break early if all conditions met
            if (hasUpper && hasLower && hasDigit) {
                break;
            }
        }

        if (hasForbiddenChar || !(hasUpper && hasLower && hasDigit && newPassword.length() >= MIN_LENGTH)) {
            System.out.println("Password change failed. New password must contain:");
            System.out.println("- At least " + MIN_LENGTH +" Characters Long");
            System.out.println("- At least one uppercase letter (A-Z)");
            System.out.println("- At least one lowercase letter (a-z)");
            System.out.println("- At least one number (0-9)");
            System.out.println("- And DOES NOT contain any special characters like (Comma, Double Quotes or Newline Characters)");
            return false;
        }

        // 3. Check if new password is the same as the old one
         if (newPassword.equals(currentPassword)) {
             System.out.println("Password change failed. New password cannot be the same as the old password.");
             return false;
         }


        // All checks passed, update password
        currentUser.setPassword(newPassword);
        saveUserData(); // writes out assets/userList.csv
        System.out.println("Password changed successfully. You will be logged out now.");
        setCurrentUser(null); // <— force logout
        return true;
    }


    /**
     * Gets the currently logged-in user.
     *
     * @return The current {@link User}, or null if no user is logged in.
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the current user.
     *
     * @param user The {@link User} to set as the current user.
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Retrieves a user by their NRIC.
     *
     * @param nric The NRIC of the user to retrieve.
     * @return The {@link User} with the specified NRIC, or null if not found.
     */
    public User getUserByNRIC(String nric) {
        // Ensure userList is loaded before searching
        if (userList == null) {
            loadUserData();
            // If still null after loading, return null
            if (userList == null) {
                System.err.println("Error: User data could not be loaded.");
                return null;
            }
        }
        // Proceed with search
        for (User u : userList) {
            if (u.getNRIC().equalsIgnoreCase(nric))
                return u;
        }
        return null; // Return null if not found after searching
    }

}
