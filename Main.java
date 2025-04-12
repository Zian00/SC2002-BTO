import controllers.UserCTRL;
import models.User;

public class Main {
    public static void main(String[] args) {
        UserCTRL userCTRL = new UserCTRL();
        userCTRL.loadUserData();
        
        if (userCTRL.getUserList() == null || userCTRL.getUserList().isEmpty()) {
            System.out.println("No users loaded.");
        } else {
            for (User user : userCTRL.getUserList()) {
                String filter = (user.getFilterSettings() == null || user.getFilterSettings().isEmpty()) ? "null" : user.getFilterSettings();
                System.out.println("Name: " + user.getName() +
                                   ", NRIC: " + user.getNRIC() +
                                   ", Age: " + user.getAge() +
                                   ", Marital Status: " + user.getMaritalStatus() +
                                   ", Role: " + user.getRole() +
                                   ", Filter Settings: " + filter);
            }
        }
    }
}