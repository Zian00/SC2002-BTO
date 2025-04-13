package controllers;

import java.io.IOException;
import java.util.List;
import models.User;
import models.enumerations.Role;
import models.repositories.UserCSVRepository;

public class UserCTRL {

    private List<User> userList;
    private User currentUser;
    private UserCSVRepository userRepo = new UserCSVRepository();

    public void loadUserData() {
        userList = userRepo.readUserFromCSV();
    }

    public void saveUserData() {
        userRepo.writeUserToCSV(userList);
    }

    /**
     * Login as before.
     */
    public boolean login(String NRIC, String password, Role role) {
        if (userList == null) loadUserData();
        for (User u : userList) {
            if (u.getNRIC().equalsIgnoreCase(NRIC)
             && u.getPassword().equals(password)
             && u.getRole() == role) {
                currentUser = u;
                return true;
            }
        }
        return false;
    }

    /**
     * Change password with verification of the old password.
     * @param newPassword the desired new password
     * @return true if the change succeeded, false otherwise
     */
    /** 
 * Change the current user’s password to newPassword, then persist to CSV.
 */
    public boolean changePassword(String newPassword) {
    if (newPassword == null || newPassword.trim().isEmpty()) {
        System.out.println("Error: password cannot be blank.");
        return false;
    }
    if (currentUser == null) {
        System.out.println("Error: no user is currently logged in.");
        return false;
    }

    currentUser.setPassword(newPassword);
    saveUserData();  // writes out assets/userList.csv
    System.out.println("Password changed successfully. You will be logged out now.");
    currentUser = null;  // <— force logout
    return true;
}
    


    // getters/setters…

    public User getCurrentUser() {
        return currentUser;
    }
}
