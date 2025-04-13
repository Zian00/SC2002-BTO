package controllers;

import java.util.List;
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

    public void displayEnquiries(String projName) {
    }

    @Override
    public void createEnquiry(String message) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'createEnquiry'");
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
