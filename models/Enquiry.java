package models;

public class Enquiry {

    private int enquiryId;
    private String submittedByNRIC;
    private int projectID;
    private String enquiryText;
    private String response;
    private String timestamp;

    // Getters and Setters

    public int getEnquiryId() {
        return enquiryId;
    }

    public void setEnquiryId(int enquiryId) {
        this.enquiryId = enquiryId;
    }

    public String getSubmittedByNRIC() {
        return submittedByNRIC;
    }

    public void setSubmittedByNRIC(String submittedByNRIC) {
        this.submittedByNRIC = submittedByNRIC;
    }

    public int getProjectId() {
        return projectID;
    }

    public void setProjectId(int projectID) {
        this.projectID = projectID;
    }

    public String getEnquiryText() {
        return enquiryText;
    }

    public void setEnquiryText(String enquiryText) {
        this.enquiryText = enquiryText;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }
}
