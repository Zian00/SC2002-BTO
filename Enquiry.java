import java.util.Date;

public class Enquiry {

    private String enquiryText;
    private Applicant submittedBy;
    private BTOProject project;
    private Date timestamp;

    public void edit(String newText) {
        this.enquiryText = newText;
    }

    public void delete() {
        // Logic to delete this enquiry
    }

    // Getters and setters
    public String getEnquiryText() {
        return enquiryText;
    }
    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }
    public Applicant getSubmittedBy() {
        return submittedBy;
    }
    public void setSubmittedBy(Applicant submittedBy) {
        this.submittedBy = submittedBy;
    }
    public BTOProject getProject() {
        return project;
    }
    public void setProject(BTOProject project) {
        this.project = project;
    }
    public Date getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
