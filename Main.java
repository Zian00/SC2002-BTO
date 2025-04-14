import controllers.BTOProjectCTRL;
import controllers.UserCTRL;
import java.util.Scanner;
import models.enumerations.Role;
import views.ApplicantView;
import views.BTOApplicationView;
import views.BTOProjectView;
import views.EnquiryView;
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
                            // 2) Logged‑in Main menu
                            runCentralMenu(sc, userCTRL);
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



    private static void runCentralMenu(Scanner sc, UserCTRL userCTRL) {
        Role role = userCTRL.getCurrentUser().getRole();

        // Instantiate Views
        UserView baseView = switch (role) {
            case APPLICANT  -> new ApplicantView();
            case HDBOFFICER -> new OfficerView();
            case HDBMANAGER -> new ManagerView();
        };
        BTOProjectView projectView = new BTOProjectView();
        BTOApplicationView btoApplicationView = new BTOApplicationView();
        EnquiryView enquiryView = new EnquiryView();

        // Instantiate Controllers
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        // BTOApplicationCTRL applicationCTRL = new BTOApplicationCTRL(userCTRL.getCurrentUser());
        // EnquiryCTRL enquiryCTRL = new EnquiryCTRL(userCTRL.getCurrentUser());
        // OfficerApplicationCTRL officerApplicationCTRL = new OfficerApplicationCTRL(userCTRL.getCurrentUser());

        while (true) {
            baseView.displayMenu();
            System.out.print("Select an option: ");
            String opt = sc.nextLine().trim();

            // --- Common options 1–4 ---
            switch (opt) {
                case "1" -> runProjectMenu(sc, userCTRL, projectCTRL, projectView);
                // case "2" -> runApplicationMenu(sc, userCTRL, applicationCTRL, btoApplicationView);
                // case "3" -> runEnquiryMenu(sc, userCTRL, enquiryCTRL, enquiryView);
                case "4" -> { 
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null) return;  // back to login
                }
            }

            if (role != null) // --- Role‑specific extra options ---
            switch (role) {
                case APPLICANT:
                    switch (opt){
                        case "5" -> {
                            // Logout
                            userCTRL.setCurrentUser(null);
                            baseView.displayLogout();
                            return;
                        }
                    }
                case HDBOFFICER:
                    switch (opt) {
                        // case "5" -> officerApplicationCTRL.
                        case "6" -> {
                            // Logout
                            userCTRL.setCurrentUser(null);
                            baseView.displayLogout();
                            return;
                        }
                    }   break;
                case HDBMANAGER:
                    switch (opt) {
                        // case "5" -> officerApplicationCTRL.
                        case "6" -> {
                            // Logout
                            userCTRL.setCurrentUser(null);
                            baseView.displayLogout();
                            return;
                        }
                    }   break;
                default:
                    break;
            }
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

    private static void runProjectMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL, BTOProjectView projectView) {
        while (true) {
            switch (userCTRL.getCurrentUser().getRole()) {
                case APPLICANT -> projectView.displayApplicantMenu();
                case HDBOFFICER -> projectView.displayOfficerMenu();
                case HDBMANAGER -> projectView.displayManagerMenu();
            }
            String c = sc.nextLine().trim();
            switch (c) {
                case "1" -> {
                    var filtered = projectCTRL.getFilteredProjects();
                    projectView.displayAvailableForApplicant(
                        userCTRL.getCurrentUser(), filtered);
                }
                // case "2" -> {
                //     var mine = projectCTRL.getMyCreatedProjects();
                //     projectView.displayAllProject(mine);
                // }
                // case "3" -> {
                //     int pid = projectView.promptProjectID(sc);
                //     String res = new BTOApplicationCTRL(userCTRL.getCurrentUser())
                //                      .applyForProject(pid);
                //     projectView.showMessage(res);
                // }
                case "4" -> {
                    return;  // back to central menu
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
}
