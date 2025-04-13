package controllers;

import java.time.LocalDateTime;
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
    private EnquiryCSVRepository repo = new EnquiryCSVRepository();

    /**
     * Load all enquiries from CSV and remember the logged‑in user.
     */
    public EnquiryCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.enquiries   = repo.readEnquiriesFromCSV();
    }

    /**
     * List all enquiries by this user, optionally filtered by project name.
     */
    public void displayEnquiry(String projName) {
        enquiries.stream()
            .filter(e -> e.getSubmittedBy().getNRIC().equals(currentUser.getNRIC()))
            .filter(e -> projName == null 
                      || e.getProject().getProjectName().equalsIgnoreCase(projName))
            .forEach(System.out::println);
    }

    /**
     * Submit a new enquiry with the given message.
     * Note: you'll need to set the project separately if required.
     */
    @Override
    public void createEnquiry(String message) {
        Enquiry e = new Enquiry();
        e.setEnquiryId(nextId());
        e.setEnquiryText(message);
        e.setSubmittedBy(currentUser);
        e.setProject(null);  // or set via a setter if you have project context
        e.setResponse(null);
        e.setTimestamp(LocalDateTime.now().toString());

        enquiries.add(e);
        repo.writeEnquiriesToCSV(enquiries);
        System.out.println("Enquiry submitted (ID: " + e.getEnquiryId() + ").");
    }

    /**
     * Edit an existing enquiry's text.
     */
    @Override
    public void editEnquiry(Enquiry enquiry, String newText) {
        enquiry.setEnquiryText(newText);
        repo.writeEnquiriesToCSV(enquiries);
        System.out.println("Enquiry updated.");
    }

    /**
     * Delete an enquiry.
     */
    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        enquiries.remove(enquiry);
        repo.writeEnquiriesToCSV(enquiries);
        System.out.println("Enquiry deleted.");
    }

    /**
     * Record a response to an enquiry (for officers/managers).
     */
    @Override
    public void responseEnquiry(Enquiry enquiry, String response) {
        enquiry.setResponse(response);
        repo.writeEnquiriesToCSV(enquiries);
        System.out.println("Response recorded.");
    }

    /**
     * Helper for CLI: find an enquiry by its ID.
     */
    public Enquiry getEnquiryById(int id) {
        return enquiries.stream()
            .filter(e -> e.getEnquiryId() == id)
            .findFirst()
            .orElse(null);
    }

    /**
     * Compute the next unique enquiry ID.
     */
    private int nextId() {
        return enquiries.stream()
            .mapToInt(Enquiry::getEnquiryId)
            .max()
            .orElse(0) + 1;
    }
}
