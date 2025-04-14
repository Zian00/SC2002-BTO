package views;

import java.util.List;
import java.util.Scanner;
import models.BTOProject;
import models.Enquiry;

public class EnquiryView {

	public void displayApplicantMenu() {
		System.out.println("\n=== Enquiry Menu ===");
		System.out.println("1. Display All My Enquiries");
		System.out.println("2. Edit My Enquiry");
		System.out.println("3. Delete My Enquiry");
		System.out.println("4. Back");
		System.out.print("Select an option: ");
	}
	public void displayAdminMenu() { // Not implemented properly, need to come back to this - Kaibao
		System.out.println("\n=== Enquiry Menu ===");
		System.out.println("1. Display All Enquiries");
		System.out.println("2. Back");
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
     * Displays a confirmation that an enquiry was created, with its details.
     * @param enquiry The newly created enquiry.
     */
    public void displayEnquiryCreated(Enquiry enquiry) {
        System.out.println("--------------------------------------------------");
        System.out.println("Enquiry created successfully!");
        System.out.println("Enquiry ID   : " + enquiry.getEnquiryId());
        System.out.println("Enquiry Text : " + enquiry.getEnquiryText());
        System.out.println("Timestamp    : " + enquiry.getTimestamp());
        System.out.println("--------------------------------------------------");
    }

	public void displayFilteredEnquiries(List<BTOProject> projects, List<Enquiry> enquiries) {
    if (enquiries == null || enquiries.isEmpty()) {
        System.out.println("No enquiries found.");
        return;
    }
    for (Enquiry enquiry : enquiries) {
        System.out.println("--------------------------------------------------");
        System.out.println("Enquiry ID   : " + enquiry.getEnquiryId());
        
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
        System.out.println("Timestamp    : " + enquiry.getTimestamp());
		if (enquiry.getResponse() != null && !enquiry.getResponse().isEmpty()) {
            System.out.println("Response     : " + enquiry.getResponse());
        }else{
		    System.out.println("Response     : (No response yet)");	
		}
    }
    System.out.println("--------------------------------------------------");
}

	/**
	 * 
	 * @param newText
	 */
	public void edit(String newText) {
		// TODO - implement EnquiryView.edit
		throw new UnsupportedOperationException();
	}

	public void delete() {
		// TODO - implement EnquiryView.delete
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param text
	 */
	public void setResponse(String text) {
		// TODO - implement EnquiryView.setResponse
		throw new UnsupportedOperationException();
	}

}