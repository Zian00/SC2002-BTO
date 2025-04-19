package controllers;

import boundaries.EnquiryView;
import entity.BTOProject;
import entity.Enquiry;
import entity.User;
import entity.enumerations.Role;
import entity.interfaces.IEnquiryResponse;
import entity.interfaces.IEnquirySubmission;
import entity.repositories.EnquiryCSVRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public final class EnquiryCTRL implements IEnquiryResponse, IEnquirySubmission {

    private List<Enquiry> enquiries;
    private final User currentUser;
    private final EnquiryCSVRepository enquiryRepo = new EnquiryCSVRepository();
    
    public void loadEnquiryData() {
        try {
            enquiries = enquiryRepo.readEnquiriesFromCSV();
        } catch (Exception e) {
            System.err.println("Failed to load enquiry data: " + e.getMessage());
        }
    }

    public void saveEnquiryData() {
        try {
            enquiryRepo.writeEnquiriesToCSV(enquiries);
        } catch (Exception e) {
            System.err.println("Failed to save enquiry data: " + e.getMessage());
        }
    }
    
    public EnquiryCTRL(User currentUser) {
        this.currentUser = currentUser;
        loadEnquiryData();
    }

    public List<Enquiry> getEnquiries() {
        return enquiries;
    }

    // --------------------------------------------------------------------------------------------------
    // Enquiry Menu for Users
    // --------------------------------------------------------------------------------------------------
    public void runEnquiryMenu(Scanner sc, UserCTRL userCTRL, BTOProjectCTRL projectCTRL,
            EnquiryView enquiryView, EnquiryCTRL enquiryCTRL) {
        while (true) {
            Role role = userCTRL.getCurrentUser().getRole();
            var projectList = projectCTRL.getAllProjects();
            switch (role) {
                case APPLICANT -> enquiryView.displayApplicantMenu();
                case HDBOFFICER -> enquiryView.displayOfficerMenu();
                case HDBMANAGER -> enquiryView.displayManagerMenu();
            }

            String c = sc.nextLine().trim();
            switch (role) {
                case APPLICANT -> {
                    var userEnquiries = enquiryCTRL.getFilteredEnquiriesByNRIC();
                    switch (c) {
                        case "1" -> { // Only display Enquiry by User
                            enquiryView.displayFilteredEnquiries(projectList, userEnquiries);
                        }
                        case "2" -> { // Edit Selected Enquiry
                            // Filter userEnquiries with no response and display
                            var editableEnquiries = enquiryCTRL.getEditableEnquiriesByNRIC();
                            if (editableEnquiries.isEmpty()) {
                                enquiryView.showMessage("No editable enquiries found.");
                                break;
                            }
                            enquiryView.displayFilteredEnquiries(projectList, editableEnquiries);
                            // Get user selection on which enquiry to edit
                            int selectedId = enquiryView.promptEnquirySelection(editableEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(editableEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Get new enquiry text to update
                            String newText = enquiryView.promptNewEnquiryText(selected.getEnquiryText(), sc);
                            if (newText == null || newText.isEmpty()) {
                                enquiryView.showMessage("Enquiry update cancelled!");
                            } else {
                                if (enquiryCTRL.editEnquiry(selected, newText)) {
                                    enquiryView.showMessage("Enquiry updated successfully!");
                                    enquiryView.displayEnquiry(selected);
                                } else {
                                    enquiryView.showMessage("Failed to edit enquiry, please try again.");
                                }
                            }
                        }
                        case "3" -> { // Delete Enquiry
                            // Filter userEnquiries with no response and display
                            var editableEnquiries = enquiryCTRL.getEditableEnquiriesByNRIC();
                            if (editableEnquiries.isEmpty()) {
                                enquiryView.showMessage("No deletable enquiries found.");
                                break;
                            }
                            enquiryView.displayFilteredEnquiries(projectList, editableEnquiries);
                            // Get user selection on which enquiry to delete
                            int selectedId = enquiryView.promptEnquirySelection(editableEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(editableEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Confirm deletion
                            if (enquiryView.promptDeletionConfirmation(sc)) {
                                enquiryCTRL.deleteEnquiry(selected);
                                enquiryView.showMessage("Enquiry deleted successfully!");
                            } else {
                                enquiryView.showMessage("Deletion cancelled.");
                            }
                        }
                        case "4" -> { // back to central menu
                            return;
                        }
                    }
                }
                case HDBOFFICER -> {
                    var userEnquiries = enquiryCTRL.getFilteredEnquiriesByNRIC();
                    switch (c) {
                        case "1" -> { // Only display Enquiry by User
                            enquiryView.displayFilteredEnquiries(projectList, userEnquiries);
                        }
                        case "2" -> { // Edit Selected Enquiry
                            // Filter userEnquiries with no response and display
                            var editableEnquiries = enquiryCTRL.getEditableEnquiriesByNRIC();
                            if (editableEnquiries.isEmpty()) {
                                enquiryView.showMessage("No editable enquiries found.");
                                break;
                            }
                            enquiryView.displayFilteredEnquiries(projectList, editableEnquiries);
                            // Get user selection on which enquiry to edit
                            int selectedId = enquiryView.promptEnquirySelection(editableEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(editableEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Get new enquiry text to update
                            String newText = enquiryView.promptNewEnquiryText(selected.getEnquiryText(), sc);
                            if (newText == null || newText.isEmpty()) {
                                enquiryView.showMessage("Enquiry update cancelled!");
                            } else {
                                if (enquiryCTRL.editEnquiry(selected, newText)) {
                                    enquiryView.showMessage("Enquiry updated successfully!");
                                    enquiryView.displayEnquiry(selected);
                                } else {
                                    enquiryView.showMessage("Failed to edit enquiry, please try again.");
                                }
                            }
                        }
                        case "3" -> { // Delete Enquiry
                            // Filter userEnquiries with no response and display
                            var editableEnquiries = enquiryCTRL.getEditableEnquiriesByNRIC();
                            if (editableEnquiries.isEmpty()) {
                                enquiryView.showMessage("No deletable enquiries found.");
                                break;
                            }
                            enquiryView.displayFilteredEnquiries(projectList, editableEnquiries);
                            // Get user selection on which enquiry to delete
                            int selectedId = enquiryView.promptEnquirySelection(editableEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(editableEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Confirm deletion
                            if (enquiryView.promptDeletionConfirmation(sc)) {
                                enquiryCTRL.deleteEnquiry(selected);
                                enquiryView.showMessage("Enquiry deleted successfully!");
                            } else {
                                enquiryView.showMessage("Deletion cancelled.");
                            }
                        }
                        case "4" -> { // Respond to Enquiries in-charge by User
                            // Filter userManagedEnquiries with no response & Enquiry is in-chaged current
                            // user and display
                            var userManagedEnquiries = enquiryCTRL.getFilteredEnquiriesByOfficer(projectList);
                            if (userManagedEnquiries.isEmpty()) {
                                enquiryView.showMessage("No enquiries to respond to.");
                                break;
                            }
                            enquiryView.displayAllEnquiries(projectList, userManagedEnquiries);
                            // Get user selection on which enquiry to respond to
                            int selectedId = enquiryView.promptEnquirySelection(userManagedEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(userManagedEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Get response text to update enquiry
                            String responseText = enquiryView.promptResponseText(sc);
                            if (responseText == null || responseText.isEmpty()) {
                                enquiryView.showMessage("Response cancelled!");
                            } else {
                                if (enquiryCTRL.responseEnquiry(selected, responseText)) {
                                    enquiryView.showMessage("Response added successfully!");
                                    enquiryView.displayEnquiryWithResponse(selected);
                                } else {
                                    enquiryView.showMessage("Failed to add response, please try again.");
                                }
                            }

                        }
                        case "5" -> { // back to central menu
                            return;
                        }
                    }
                }
                case HDBMANAGER -> {
                    switch (c) {
                        case "1" -> { // View All Enquiries
                            enquiryView.displayAllEnquiries(projectList, enquiryCTRL.getEnquiries());
                        }
                        case "2" -> { // Respond to Enquiries in-charge by User
                            // Filter userManagedEnquiries with no response & Enquiry is in-chaged current
                            // user and display
                            var userManagedEnquiries = enquiryCTRL.getFilteredEnquiriesByManager(projectList);
                            if (userManagedEnquiries.isEmpty()) {
                                enquiryView.showMessage("No enquiries to respond to.");
                                break;
                            }
                            enquiryView.displayAllEnquiries(projectList, userManagedEnquiries);
                            // Get user selection on which enquiry to respond to
                            int selectedId = enquiryView.promptEnquirySelection(userManagedEnquiries, sc);
                            Enquiry selected = enquiryCTRL.findEnquiryById(userManagedEnquiries, selectedId);
                            if (selected == null) {
                                enquiryView.showMessage("Invalid Enquiry ID selected.");
                                break;
                            }
                            // Get response text to update enquiry
                            String responseText = enquiryView.promptResponseText(sc);
                            if (responseText == null || responseText.isEmpty()) {
                                enquiryView.showMessage("Response cancelled!");
                            } else {
                                if (enquiryCTRL.responseEnquiry(selected, responseText)) {
                                    enquiryView.showMessage("Response added successfully!");
                                    enquiryView.displayEnquiryWithResponse(selected);
                                } else {
                                    enquiryView.showMessage("Failed to add response, please try again.");
                                }
                            }
                        }
                        case "3" -> { // back to central menu
                            return;
                        }
                    }
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
    
    // Returns selected Enquiry
    public Enquiry findEnquiryById(List<Enquiry> enquiries, int selectedId) {
        return enquiries.stream()
                .filter(e -> e.getEnquiryId() == selectedId)
                .findFirst()
                .orElse(null);
    }

    // Returns a list of filtered enquiries by logged in user
    public List<Enquiry> getFilteredEnquiriesByNRIC() {
    if (enquiries == null) {
        return new ArrayList<>();
    }
    String userNRIC = currentUser.getNRIC();
    return enquiries.stream()
            .filter(enquiry -> enquiry.getSubmittedByNRIC().equalsIgnoreCase(userNRIC))
            .collect(Collectors.toList());
    }

    // Returns a list of filtered enquiries by logged in user (Role: Manager)
    public List<Enquiry> getFilteredEnquiriesByManager(List<BTOProject> projectList) {
        if (enquiries == null) {
            return new ArrayList<>();
        }
        String userNRIC = currentUser.getNRIC();
        // Filter projects managed by the current user
        List<BTOProject> managedProjects = projectList.stream()
            .filter(p -> p.getManagerID().equalsIgnoreCase(userNRIC))
            .collect(Collectors.toList());
        // Collect all project IDs from the managed projects and place it into a Set to remove duplicated and increase search time
        Set<Integer> managedProjectIds = managedProjects.stream()
            .map(BTOProject::getProjectID)
            .collect(Collectors.toSet());
        // Filter enquiries by:
        // -- projectID that is managed by current user using project IDs
        // -- no response 
        return enquiries.stream()
            .filter(e -> managedProjectIds.contains(e.getProjectId())
                        && (e.getResponse() == null || e.getResponse().isEmpty()))
            .collect(Collectors.toList());
    }
    
    // Returns a list of filtered enquiries by logged in user (Role: Officer)
    public List<Enquiry> getFilteredEnquiriesByOfficer(List<BTOProject> projectList) {
        if (enquiries == null) {
            return new ArrayList<>();
        }
        String userNRIC = currentUser.getNRIC();
        // Filter projects managed by the current user
        List<BTOProject> managedProjects = projectList.stream()
            .filter(p -> p.getApprovedOfficer().stream()
            .anyMatch(officer -> officer.equalsIgnoreCase(userNRIC)))
            .collect(Collectors.toList());
        // Collect all project IDs from the managed projects and place it into a Set to remove duplicated and increase search time
        Set<Integer> managedProjectIds = managedProjects.stream()
            .map(BTOProject::getProjectID)
            .collect(Collectors.toSet());
        // Filter enquiries by:
        // -- projectID that is managed by current user using project IDs
        // -- no response 
        return enquiries.stream()
            .filter(e -> managedProjectIds.contains(e.getProjectId())
                        && (e.getResponse() == null || e.getResponse().isEmpty()))
            .collect(Collectors.toList());
    }

    // Returns a list of filtered enquiries by logged in user and that it has not been responded yet
    public List<Enquiry> getEditableEnquiriesByNRIC() {
        if (enquiries == null) {
            return new ArrayList<>();
        }
        String userNRIC = currentUser.getNRIC();
        return enquiries.stream()
                .filter(e -> e.getSubmittedByNRIC().equalsIgnoreCase(userNRIC)
                          && (e.getResponse() == null || e.getResponse().isEmpty()))
                .collect(Collectors.toList());
    }

    /**
     * Creates a new enquiry.
     * @param projectId the project ID this enquiry is associated with
     * @param message   the enquiry message text
     */
    @Override
    public Enquiry createEnquiry(int projectId, String message) {
        try {
            // Generate new enquiry id.
            int newId = 1;
            if (enquiries != null && !enquiries.isEmpty()) {
                newId = enquiries.stream().mapToInt(Enquiry::getEnquiryId).max().getAsInt() + 1;
            }
            
            // Create and populate new enquiry.
            Enquiry newEnquiry = new Enquiry();
            newEnquiry.setEnquiryId(newId);
            newEnquiry.setEnquiryText(message);
            newEnquiry.setSubmittedByNRIC(currentUser.getNRIC());
            
            // Set project id as supplied.
            newEnquiry.setProjectId(projectId);
            
            // Set timestamp - using current time.
            LocalDateTime now = LocalDateTime.now();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            newEnquiry.setTimestamp(now.format(formatter));
            
            // Add new enquiry to the list.
            if (enquiries == null) {
                enquiries = new ArrayList<>();
            }
            enquiries.add(newEnquiry);
            
            // Persist changes.
            saveEnquiryData();
            
            // Notify that creation succeeded.
            System.out.println("Enquiry created successfully!");
            return newEnquiry;
        } catch (Exception e) {
            System.err.println("Error creating enquiry: " + e.getMessage());
            return null;
        }
    }

    // Update existing enquiry with new enquiry text
    @Override
    public boolean editEnquiry(Enquiry enquiry, String newText) {
        if(enquiries != null){
            for(Enquiry en : enquiries){
                if (en.getEnquiryId() == enquiry.getEnquiryId()) {
                    try {
                        // Get current time
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        // Update new Enquiry Text with latest edited date & time
                        en.setEnquiryText(newText + " [Edited as of: " + now.format(formatter) + "]");

                        // Persist changes.
                        saveEnquiryData();
                        return true;
                    }catch (Exception e) {
                        System.err.println("Error editing enquiry: " + e.getMessage());
                        return false;
                    }
                }
            }
        }
        return false;
    }

    // Delete existing enquiry
    @Override
    public boolean deleteEnquiry(Enquiry enquiry) {
        if (enquiries != null) {
            Iterator<Enquiry> iterator = enquiries.iterator();
            while (iterator.hasNext()) {
                Enquiry en = iterator.next();
                if (en.getEnquiryId() == enquiry.getEnquiryId()) {
                    iterator.remove();  // safely remove enquiry
                    saveEnquiryData();  // persist changes
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean responseEnquiry(Enquiry enquiry, String response) {
        if(enquiries != null){
            for(Enquiry en : enquiries){
                if (en.getEnquiryId() == enquiry.getEnquiryId()) {
                    try {
                        // Get current time
                        LocalDateTime now = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                        // Update new Enquiry Text with latest edited date & time
                        en.setResponse(response + " [Responded on: " + now.format(formatter) + "]");

                        // Persist changes.
                        saveEnquiryData();
                        return true;
                    }catch (Exception e) {
                        System.err.println("Error responding enquiry: " + e.getMessage());
                        return false;
                    }
                }
            }
        }
        return false;
    }
}
