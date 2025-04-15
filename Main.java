import controllers.BTOApplicationCTRL;
import controllers.BTOProjectCTRL;
import controllers.EnquiryCTRL;
import controllers.UserCTRL;
import java.util.Scanner;
import models.Enquiry;
import models.enumerations.FlatType;
import models.enumerations.Role;
import views.ApplicantView;
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
                // =====================================
                // Main Menu Options
                // =====================================
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

    // --------------------------------------------------------------------------------------------------
    // Central Menu: Navigation for Logged-in Users
    // --------------------------------------------------------------------------------------------------
    private static void runCentralMenu(Scanner sc, UserCTRL userCTRL) {
        Role role = userCTRL.getCurrentUser().getRole();

        // Instantiate Views
        UserView baseView = switch (role) {
            case APPLICANT  -> new ApplicantView();
            case HDBOFFICER -> new OfficerView();
            case HDBMANAGER -> new ManagerView();
        };
        BTOProjectView projectView = new BTOProjectView();
        // BTOApplicationView btoApplicationView = new BTOApplicationView();
        EnquiryView enquiryView = new EnquiryView();

        // Instantiate Controllers
        BTOProjectCTRL projectCTRL = new BTOProjectCTRL(userCTRL.getCurrentUser());
        BTOApplicationCTRL  applicationCTRL = new BTOApplicationCTRL(userCTRL.getCurrentUser());
        EnquiryCTRL enquiryCTRL = new EnquiryCTRL(userCTRL.getCurrentUser());
        // OfficerApplicationCTRL officerApplicationCTRL = new OfficerApplicationCTRL(userCTRL.getCurrentUser());

        while (true) {
            baseView.displayMenu();
            System.out.print("Select an option: ");
            String opt = sc.nextLine().trim();

            // --- Common options 1–4 ---
            switch (opt) {
                case "1" -> runProjectMenu(sc, userCTRL, projectCTRL, projectView, applicationCTRL, enquiryView, enquiryCTRL);
                // case "2" -> runApplicationMenu(sc, userCTRL, applicationCTRL, btoApplicationView);
                case "3" -> runEnquiryMenu(sc, userCTRL, projectCTRL, enquiryView, enquiryCTRL);
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

    // --------------------------------------------------------------------------------------------------
    // Change Password Handler
    // --------------------------------------------------------------------------------------------------
    private static void handleChangePassword(Scanner sc, UserCTRL userCTRL) {
        System.out.print("Enter new password: ");
        String newPass = sc.nextLine().trim();
        userCTRL.changePassword(newPass);
    }

    // --------------------------------------------------------------------------------------------------
    // Projects Menu for Users
    // --------------------------------------------------------------------------------------------------
    private static void runProjectMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL, 
                                    BTOProjectView projectView, BTOApplicationCTRL applicationCTRL,
                                    EnquiryView enquiryView, EnquiryCTRL enquiryCTRL) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            switch (role) {
                case APPLICANT -> projectView.displayApplicantMenu();
                case HDBOFFICER -> projectView.displayOfficerMenu();
                case HDBMANAGER -> projectView.displayManagerMenu();
            }
            String c = sc.nextLine().trim();
            switch (role) {
                case APPLICANT -> {
                    var availableProjects = projectCTRL.getFilteredProjects();
                    switch (c){
                        case "1" -> { // Only display projects User can apply
                            projectView.displayAvailableForApplicant(
                                userCTRL.getCurrentUser(), availableProjects);
                        }
                        case "2" -> { // Apply for BTO
                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                userCTRL.getCurrentUser(), availableProjects);
                            
                            // Get project selection
                            System.out.print("Enter project ID to apply: ");
                            int projectId = Integer.parseInt(sc.nextLine());
                            
                            // Get flat type selection
                            System.out.println("Select flat type:");
                            System.out.println("1. 2-Room");
                            System.out.println("2. 3-Room");
                            int flatChoice = Integer.parseInt(sc.nextLine());
                            FlatType flatType = (flatChoice == 1) ? FlatType.TWOROOM : FlatType.THREEROOM;
                            
                            // Submit application
                            boolean ok = applicationCTRL.apply(projectId, flatType);
                            if (ok) {
                                System.out.println("Application submitted! Status: PENDING.");
                            }
                        }
                        case "3" -> { // Submit Enquiry for a project
                            // Show available projects
                            projectView.displayAvailableForApplicant(
                                userCTRL.getCurrentUser(), availableProjects);
                            
                            // Get project selection
                            System.out.print("Enter project ID to submit Enquiry: ");
                            int projectId = Integer.parseInt(sc.nextLine());

                            String enquiryText = enquiryView.promptEnquiryCreation(sc);
                            Enquiry newEnquiry = enquiryCTRL.createEnquiry(projectId, enquiryText);
                            enquiryView.displayEnquiryCreated(newEnquiry);
                        }
                        case "4" -> {
                            return;  // back to central menu
                        }
                    }
                }
                case HDBOFFICER -> {
                    switch (c){
                        case "6" -> {
                            return;  // back to central menu
                        }
                    }
                }
                case HDBMANAGER ->{
                    switch (c){
                        case "5" -> {
                            return;  // back to central menu
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    // --------------------------------------------------------------------------------------------------
    // Enquiry Menu for Users
    // --------------------------------------------------------------------------------------------------
    private static void runEnquiryMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
                                        EnquiryView enquiryView, EnquiryCTRL enquiryCTRL) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            switch (role) {
                case APPLICANT,HDBOFFICER -> enquiryView.displayApplicantMenu();
                case HDBMANAGER -> enquiryView.displayAdminMenu();
            }
            
            String c = sc.nextLine().trim();
            switch (role) {
                case APPLICANT -> {
                    var userEnquiries = enquiryCTRL.getFilteredEnquiriesByNRIC();
                    switch (c){
                        case "1" -> { // Only display Enquiry by User
                            var projectList = projectCTRL.getAllProjects();
                            enquiryView.displayFilteredEnquiries(projectList, userEnquiries);
                        }
                        case "2" -> { // Apply for BTO
                            // Show enquiries by user
                            // Select enquiry to edit
                            // Show edit options
                        }
                        case "3" -> { // Delete Enquiry
                            // Show enquiries by user
                            // Select enquiry to delete
                            // Confirm deletion 
                        }
                        case "4" -> {
                            return;  // back to central menu
                        }
                    }
                }
                case HDBOFFICER -> {
                    switch (c){
                        case "4" -> {
                            return;  // back to central menu
                        }
                    }
                }
                case HDBMANAGER ->{
                    switch (c){
                        case "2" -> {
                            return;  // back to central menu
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
}
