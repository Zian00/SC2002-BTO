package controllers;

import java.util.List;
import models.User;
import models.enumerations.Role;
import models.repositories.UserCSVRepository;

public class UserCTRL {

    private List<User> userList;
    private User currentUser;
    private UserCSVRepository userRepo = new UserCSVRepository();

    // Loads the user data from the CSV file into the userList
    public void loadUserData() {
        userList = userRepo.readUserFromCSV();
    }

    // Persists the current user list back to the CSV file
    public void saveUserData() {
        userRepo.writeUserToCSV(userList);
    }

    /**
     * (Currently not implemented)
     * @param NRIC
     * @param password
     * @param role
     */
    public boolean login(String NRIC, String password, Role role) {
        // TODO - Implement login logic if needed
        throw new UnsupportedOperationException();
    }

    /**
     * Changes the password for the current user and immediately saves the update.
     * @param newPassword
     */
    public void changePassword(String newPassword) {
        if (currentUser != null) {
            currentUser.setPassword(newPassword);
            saveUserData();  // Save changes immediately
        } else {
            System.out.println("Login to change password.");
        }
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        currentUser = user;
    }
}