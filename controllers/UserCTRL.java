package controllers;

import boundaries.ApplicantView;
import boundaries.BTOApplicationView;
import boundaries.BTOProjectView;
import boundaries.EnquiryView;
import boundaries.ManagerView;
import boundaries.OfficerApplicationView;
import boundaries.OfficerView;
import boundaries.UserView;
import entity.User;
import entity.enumerations.Role;
import entity.repositories.UserCSVRepository;
import java.util.List;
import java.util.Scanner;

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

        while (true) {
            baseView.displayMenu();

            String opt = sc.nextLine().trim();
            // --- Common options 1–4 ---
            switch (opt) {
                case "1" ->
                {
                    // Update project visibility before filtering/displaying projects
                    projectCTRL.updateProjectVisibility();
                    projectCTRL.runProjectMenu(sc, userCTRL, projectCTRL, projectView, applicationCTRL, officerAppCTRL, officerAppView, enquiryView, enquiryCTRL);
                }
                case "2" -> applicationCTRL.runApplicationMenu(sc, userCTRL, projectCTRL, applicationCTRL, btoApplicationView);
                case "3" -> enquiryCTRL.runEnquiryMenu(sc, userCTRL, projectCTRL, enquiryView, enquiryCTRL);
                case "4" -> {
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null)
                        return; // back to login
                }
            }

            if (role != null) // --- Role‑specific extra options ---
                switch (role) {
                    case APPLICANT:
                        switch (opt) {
                            case "5" -> { // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                    case HDBOFFICER:
                        switch (opt) {
                            case "5" -> { // Enter Officer Application Menu
                                officerAppCTRL.runOfficerApplicationMenu(sc, officerAppCTRL, officerAppView, projectCTRL);
                            }
                            case "6" -> { // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                        break;
                    case HDBMANAGER:
                        switch (opt) {
                            case "5" -> {
                                officerAppCTRL.runOfficerApplicationMenu(sc, officerAppCTRL, officerAppView, projectCTRL);
                            }
                            case "6" -> {
                                // Logout
                                userCTRL.setCurrentUser(null);
                                baseView.displayLogout();
                                return;
                            }
                        }
                        break;
                    default:
                        break;
                }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // Change Password Handler
    // --------------------------------------------------------------------------------------------------
    /**
     * Handles the password change process for the current user.
     * Prompts for a new password and updates it.
     *
     * @param sc       The Scanner object for user input.
     * @param userCTRL The UserCTRL instance.
     */
    public void handleChangePassword(Scanner sc, UserCTRL userCTRL) {
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();
        userCTRL.changePassword(newPass);
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
     * @param me        The user whose filter settings are to be updated.
     * @param filterCsv The new filter settings in CSV format.
     */
    public void updateFilterSettings(User me, String filterCsv) {
        if (userList == null)
        {
            loadUserData();
        }
        currentUser.setFilterSettings(filterCsv);
        // Also synchronize the change into userList
        for (User u : userList) {
            if (u.getNRIC().equalsIgnoreCase(currentUser.getNRIC())) {
                u.setFilterSettings(filterCsv);
                break;
            }
        }
        saveUserData();
    }

    /**
     * Changes the current user's password to the specified new password and persists the change.
     * Logs out the user after a successful change.
     *
     * @param newPassword The desired new password.
     * @return true if the change succeeded, false otherwise.
     */
    public boolean changePassword(String newPassword) {
        if (newPassword == null || newPassword.trim().isEmpty()) {
            System.out.println("Password cannot be blank");
            return false;
        }

        if (currentUser == null) {
            System.out.println("Error: no user is currently logged in.");
            return false;
        }

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
        if (userList != null) {
            for (User u : userList) {
                if (u.getNRIC().equalsIgnoreCase(nric))
                    return u;
            }
        }
        return null;
    }

}
