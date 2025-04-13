import controllers.BTOProjectCTRL;
import controllers.UserCTRL;
import java.util.Scanner;
import models.enumerations.Role;
import views.ApplicantView;
import views.ManagerView;
import views.OfficerView;
import views.UserView;

public class Main {
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            UserCTRL userCTRL = new UserCTRL();
            UserView userView = new UserView();  // Create a UserView instance
            userCTRL.loadUserData();
            
            OUTER:
            while (true) {
                System.out.println("\n=== HDB Hub ===");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Select an option: ");
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> {
                        if (userView.loginFlow(sc, userCTRL)) {
                            displayRoleMenu(sc, userCTRL);
                        }
                    }
                    case "2" -> {
                        System.out.println("Exiting... Goodbye!");
                        break OUTER;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }



    private static void displayRoleMenu(Scanner sc, UserCTRL userCTRL) {
        Role role = userCTRL.getCurrentUser().getRole();
        switch (role) {
            case APPLICANT    -> runApplicantMenu(sc, userCTRL);
            case HDBOFFICER   -> runOfficerMenu(sc, userCTRL);
            case HDBMANAGER   -> runManagerMenu(sc, userCTRL);
        }
    }

    /**
     * Prompt the user once for a new password and apply it.
     */
    private static void handleChangePassword(Scanner sc, UserCTRL userCTRL) {
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();
        userCTRL.changePassword(newPass);
    }

    private static void runApplicantMenu(Scanner sc, UserCTRL userCTRL) {
        ApplicantView view = new ApplicantView();
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        // BTOApplicationCTRL applicationCTRL = new BTOApplicationCTRL(userCTRL.getCurrentUser());
        // EnquiryCTRL enquiryCTRL = new EnquiryCTRL(userCTRL.getCurrentUser());
        // TODO: instantiate controllers (BTOProjectCTRL, BTOApplicationCTRL, EnquiryCTRL)

        while (true) {
            view.displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> projectCTRL.viewAvailableProjects();
                // case "2" -> applicationCTRL.viewUserApplications();
                // case "3" -> enquiryCTRL.displayEnquiries(choice);
                case "4" -> {
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null) return;  // back to login
                }
                case "5" -> {
                    // Logout
                    userCTRL.setCurrentUser(null);
                    view.displayLogout();
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
            // case "1": … implement View Available Projects …
            // case "2": … implement Apply for Project …
            // ...
        }
    }

    private static void runOfficerMenu(Scanner sc, UserCTRL userCTRL) {
        OfficerView view = new OfficerView();
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        // … other controllers …
    
        while (true) {
            view.displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "10" -> // “View My Officer Projects”
                    projectCTRL.viewOfficerProjects();
                case "15" -> {
                    // Logout
                    view.displayLogout();
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
            // … applicant options 1–9 …
            // … more officer actions …
        }
    }
    
    private static void runManagerMenu(Scanner sc, UserCTRL userCTRL) {
        ManagerView view = new ManagerView();
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        // … other controllers …
    
        while (true) {
            view.displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "5" -> // View All Projects
                    projectCTRL.viewAllProjects();
                case "6" -> // View My Created Projects
                    projectCTRL.viewMyCreatedProjects();
                case "16" -> {
                    // Logout
                    view.displayLogout();
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
            // … other manager actions …
        }
    }
}
