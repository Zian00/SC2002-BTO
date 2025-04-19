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

public class UserCTRL {

    private List<User> userList;
    private User currentUser;
    private final UserCSVRepository userRepo = new UserCSVRepository();

    public void loadUserData() {
        userList = userRepo.readUserFromCSV();
    }

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
                            case "5" -> { // case "5" Enter Officer Application Menu
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
                            // case "5" -> officerApplicationCTRL.
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
    public void handleChangePassword(Scanner sc, UserCTRL userCTRL) {
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();
        userCTRL.changePassword(newPass);
    }

    /**
     * Login as before.
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
     /** update just the filterSettings for one user and persist */
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
     * Change password with verification of the old password.
     * 
     * @param newPassword the desired new password
     * @return true if the change succeeded, false otherwise
     */
    /**
     * Change the current user’s password to newPassword, then persist to CSV.
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

    // getters/setters…

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }

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
