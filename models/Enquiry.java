package models;

import java.time.LocalDateTime;

public class Enquiry {

    private int enquiryId;
    private String enquiryText;
    // change this:
    // private Applicant submittedBy;
    // to:
    private User submittedBy;
    private BTOProject project;
    private String response;
    private String timestamp;

    public Enquiry() { }

    public int getEnquiryId() { return enquiryId; }
    public void setEnquiryId(int enquiryId) { this.enquiryId = enquiryId; }

    public String getEnquiryText() { return enquiryText; }
    public void setEnquiryText(String enquiryText) { this.enquiryText = enquiryText; }

    // updated to User
    public User getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(User submittedBy) {
        this.submittedBy = submittedBy;
    }

    public BTOProject getProject() { return project; }
    public void setProject(BTOProject project) { this.project = project; }

    public String getResponse() { return response; }
    public void setResponse(String response) { this.response = response; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    @Override
    public String toString() {
        return String.format(
            "Enquiry[ID=%d, Project=%s, By=%s, Text=\"%s\", Response=\"%s\", Time=%s]",
            enquiryId,
            project == null ? "<none>" : project.getProjectName(),
            submittedBy == null ? "<unknown>" : submittedBy.getName(),
            enquiryText,
            response == null ? "<no response>" : response,
            timestamp
        );
    }
}
