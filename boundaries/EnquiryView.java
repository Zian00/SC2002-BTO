package boundaries;

import entity.BTOProject;
import entity.Enquiry;
import java.util.List;
import java.util.Scanner;

/**
 * View class for enquiry interactions in the BTO system.
 * <p>
 * Provides menu displays and output methods for applicants, officers, and managers
 * to view, create, edit, delete, and respond to enquiries for BTO projects.
 * </p>
 */
public class EnquiryView {

    /**
     * Displays the enquiry menu for applicants.
     */
    public void displayApplicantMenu() {
        System.out.println("\n=== Enquiry Menu ===");
        System.out.println("1. Display All My Enquiries");
        System.out.println("2. Edit My Enquiry");
        System.out.println("3. Delete My Enquiry");
        System.out.println("4. Back");
        System.out.print("Select an option: ");
    }

    /**
     * Displays the enquiry menu for HDB officers.
     */
    public void displayOfficerMenu() {
        System.out.println("\n=== Enquiry Menu ===");
        System.out.println("1. Display All My Enquiries");
        System.out.println("2. Edit My Enquiry");
        System.out.println("3. Delete My Enquiry");
        System.out.println("4. Respond to an Enquiry");
        System.out.println("5. Back");
        System.out.print("Select an option: ");
    }

    /**
     * Displays the enquiry menu for HDB managers.
     */
    public void displayManagerMenu() {
        System.out.println("\n=== Enquiry Menu ===");
        System.out.println("1. Display All Enquiries");
        System.out.println("2. Respond to an Enquiry");
        System.out.println("3. Back");
        System.out.println("Select an option: ");
    }

    /**
     * Prompts the user to enter the enquiry text.
     * @param sc Scanner instance to get user input.
     * @return the enquiry text.
     */
    public String promptEnquiryCreation(Scanner sc) {
        System.out.print("Enter your enquiry text: ");
        return sc.nextLine().trim();
    }

    /**
     * Displays a list of enquiries and then prompts for an enquiry ID to edit.
     * Returns the selected enquiry ID.
     * @param editableEnquiries List of editable enquiries.
     * @param sc Scanner instance to get user input.
     * @return the selected enquiry ID, or -1 if invalid.
     */
    public int promptEnquirySelection(List<Enquiry> editableEnquiries, Scanner sc) {
        System.out.print("Enter the Enquiry ID: ");
        try {
            return Integer.parseInt(sc.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
    
    /**
     * Prompts the user for confirmation before deleting an enquiry.
     * @param sc Scanner instance to get user input.
     * @return true if user confirms deletion, false otherwise.
     */
    public boolean promptDeletionConfirmation(Scanner sc) {
        System.out.print("Are you sure you want to delete this enquiry? (Y/N): ");
        String input = sc.nextLine().trim().toUpperCase();
        return input.equals("Y");
    }

    /**
     * Displays the current text and prompts the user for new enquiry text.
     * If the user enters nothing, update is cancelled.
     * @param currentText The current enquiry text.
     * @param sc Scanner instance to get user input.
     * @return the new enquiry text, or empty string if cancelled.
     */
    public String promptNewEnquiryText(String currentText, Scanner sc) {
        System.out.println("\nCurrent Enquiry Text: " + currentText);
        System.out.print("Enter new enquiry text (leave empty to cancel): ");
        return sc.nextLine().trim();
    }

    /**
     * Prompts the user to enter a response text for an enquiry.
     * @param sc Scanner instance to get user input.
     * @return the response text, or empty string if cancelled.
     */
    public String promptResponseText(Scanner sc) {
        System.out.print("Enter response text (leave empty to cancel): ");
        return sc.nextLine().trim();
    }

    /**
     * Displays a confirmation that an enquiry was created, with its details.
     * @param enquiry The newly created enquiry.
     */
    public void displayEnquiry(Enquiry enquiry) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("Enquiry created successfully!");
        System.out.println("Enquiry ID   : " + enquiry.getEnquiryId());
        System.out.println("Enquiry Text : " + enquiry.getEnquiryText());
        System.out.println("Timestamp    : " + enquiry.getTimestamp());
        System.out.println("--------------------------------------------------");
    }

    /**
     * Displays a confirmation that an enquiry was created, with its details and response.
     * @param enquiry The newly created enquiry.
     */
    public void displayEnquiryWithResponse(Enquiry enquiry) {
        System.out.println("\n--------------------------------------------------");
        System.out.println("Enquiry created successfully!");
        System.out.println("Enquiry ID   : " + enquiry.getEnquiryId());
        System.out.println("Enquiry Text : " + enquiry.getEnquiryText());
        System.out.println("Timestamp    : " + enquiry.getTimestamp());
        System.out.println("Response     : " + enquiry.getResponse());
        System.out.println("--------------------------------------------------");
    }

    /**
     * Displays all enquiries with project names.
     * @param projects List of all BTO projects.
     * @param enquiries List of all enquiries.
     */
    public void displayAllEnquiries(List<BTOProject> projects, List<Enquiry> enquiries) {
        System.out.println("\n"); // Line break
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }
        for (Enquiry enquiry : enquiries) {
            System.out.println("--------------------------------------------------");
            System.out.println("Enquiry ID   : " + enquiry.getEnquiryId());
            System.out.println("Timestamp    : " + enquiry.getTimestamp());
            
            // Get project name by matching project id.
            BTOProject matchingProject = projects.stream()
                .filter(p -> p.getProjectID() == enquiry.getProjectId())
                .findFirst()
                .orElse(null);
            String projectName = (matchingProject != null) 
                ? matchingProject.getProjectName() 
                : "Unknown Project";
            System.out.println("BTO Project  : " + projectName);
            
            System.out.println("Enquiry Text : " + enquiry.getEnquiryText());
            if (enquiry.getResponse() != null && !enquiry.getResponse().isEmpty()) {
                System.out.println("Response     : " + enquiry.getResponse());
            }else{
                System.out.println("Response     : (No response yet)");	
            }
        }
    }

    /**
     * Displays all enquiries that map to a project ID.
     * @param projects List of all BTO projects.
     * @param enquiries List of filtered enquiries.
     */
    public void displayFilteredEnquiries(List<BTOProject> projects, List<Enquiry> enquiries) {
        System.out.println("\n"); // Line break
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("No enquiries found.");
            return;
        }
        for (Enquiry enquiry : enquiries) {
            System.out.println("--------------------------------------------------");
            System.out.println("Enquiry ID   : " + enquiry.getEnquiryId());
            System.out.println("Timestamp    : " + enquiry.getTimestamp());
            
            // Get project name by matching project id.
            BTOProject matchingProject = projects.stream()
                .filter(p -> p.getProjectID() == enquiry.getProjectId())
                .findFirst()
                .orElse(null);
            String projectName = (matchingProject != null) 
                ? matchingProject.getProjectName() 
                : "Unknown Project";
            System.out.println("BTO Project  : " + projectName);
            
            System.out.println("Enquiry Text : " + enquiry.getEnquiryText());
            if (enquiry.getResponse() != null && !enquiry.getResponse().isEmpty()) {
                System.out.println("Response     : " + enquiry.getResponse());
            }else{
                System.out.println("Response     : (No response yet)");	
            }
        }
        System.out.println("--------------------------------------------------");
    }
    
    /**
     * Displays any message passed in.
     * @param message The message to display.
     */
    public void showMessage(String message) {
        System.out.println(message);
    }
}