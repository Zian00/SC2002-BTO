import controllers.BTOProjectCTRL;
import controllers.UserCTRL;

import java.util.List;
import java.util.Scanner;

import models.BTOProject;
import models.enumerations.Role;
import views.ApplicantView;
import views.BTOProjectView;
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
        BTOProjectView projectView = new BTOProjectView();
        // BTOApplicationCTRL applicationCTRL = new BTOApplicationCTRL(userCTRL.getCurrentUser());
        // EnquiryCTRL enquiryCTRL = new EnquiryCTRL(userCTRL.getCurrentUser());
        // TODO: instantiate controllers (BTOProjectCTRL, BTOApplicationCTRL, EnquiryCTRL)

        while (true) {
            view.displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // Filter projects (in the controller) and then have the view display them.
                    var filteredProjects = projectCTRL.getFilteredProjects();
                    projectView.displayAllProject(filteredProjects);
                }
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
        }
    }

    private static void runOfficerMenu(Scanner sc, UserCTRL userCTRL) {
        OfficerView view = new OfficerView();
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        BTOProjectView projectView = new BTOProjectView();
        // OfficerApplicationCTRL officerApplicationCTRL = new OfficerApplicationCTRL(userCTRL.getCurrentUser());
        // TODO: instantiate controllers (BTOProjectCTRL, BTOApplicationCTRL, EnquiryCTRL)
        // … other controllers …
    
        while (true) {
            view.displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> {
                    // Filter projects (in the controller) and then have the view display them.
                    var filteredProjects = projectCTRL.getFilteredProjects();
                    projectView.displayAllProject(filteredProjects);
                }
                // case "2" -> applicationCTRL.viewUserApplications();
                // case "3" -> enquiryCTRL.displayEnquiries(choice);
                case "4" -> {
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null) return;  // back to login
                }
                // case "5" -> officerApplicationCTRL;
                case "7" -> {

                    // View projects I'm handling
                    List<BTOProject> mine = projectCTRL.getHandledProjects();
                    projectView.displayHandledProjects(mine);
                    break;
                } 
                
                case "10" -> {
                    // Logout
                    userCTRL.setCurrentUser(null);
                    view.displayLogout();
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    private static void runManagerMenu(Scanner sc, UserCTRL userCTRL) {
        ManagerView view = new ManagerView();
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        BTOProjectView projectView = new BTOProjectView();
        // OfficerApplicationCTRL officerApplicationCTRL = new OfficerApplicationCTRL(userCTRL.getCurrentUser());
        // TODO: instantiate controllers (BTOProjectCTRL, BTOApplicationCTRL, EnquiryCTRL)
        // … other controllers …
    
        while (true) {
            view.displayMenu();
            String choice = sc.nextLine().trim();
            switch (choice) {
                case "1" -> { //special case 1 for manager
                    // View all projects, regardless of visibility
                    var allProjects = projectCTRL.getAllProjects();
                    projectView.displayAllProject(allProjects);
                }
                case "2" -> { //made the filter into 2
                    // Filter projects (in the controller) and then have the view display them.
                    var filteredProjects = projectCTRL.getFilteredProjects();
                    projectView.displayAllProject(filteredProjects);
                }
                // case "2" -> applicationCTRL.viewUserApplications();
                // case "3" -> enquiryCTRL.displayEnquiries(choice);
                case "4" -> {
                    handleChangePassword(sc, userCTRL);
                    if (userCTRL.getCurrentUser() == null) return;  // back to login
                }
                // case "5" -> officerApplicationCTRL;
                case "6" -> {
                    // Logout
                    userCTRL.setCurrentUser(null);
                    view.displayLogout();
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
}
