package controllers;

import java.util.List;

import entity.User;
import entity.enumerations.Role;
import entity.repositories.UserCSVRepository;

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

  
    /**
     * Login as before.
     */
    public boolean login(String NRIC, String password, Role role) {
        if (userList == null)
            loadUserData();
        for (User u : userList) {
            if (u.getNRIC().equalsIgnoreCase(NRIC) && u.getPassword().equals(password) && u.getRole() == role) {
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
