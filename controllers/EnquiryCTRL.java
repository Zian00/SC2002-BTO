package controllers;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import models.Enquiry;
import models.User;
import models.interfaces.IEnquiryResponse;
import models.interfaces.IEnquirySubmission;
import models.repositories.EnquiryCSVRepository;

public class EnquiryCTRL implements IEnquiryResponse, IEnquirySubmission {

    private List<Enquiry> enquiries;
    private User currentUser;
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
    }

    public List<Enquiry> getEnquiries(String projName) {
        return enquiries;
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

    @Override
    public void editEnquiry(Enquiry enquiry, String newText) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'editEnquiry'");
    }

    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'deleteEnquiry'");
    }

    @Override
    public void responseEnquiry(Enquiry enquiry, String response) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'responseEnquiry'");
    }
}
