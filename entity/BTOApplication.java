package entity;

import entity.enumerations.ApplicationStatus;
import entity.enumerations.ApplicationType;

public class BTOApplication {
    private int applicationId;
    private String applicantNRIC;
    private int projectID;
    private ApplicationType applicationType;
    private ApplicationStatus status;
    private String flatType;

    // getters & setters for each field:
    public int getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(int id) {
        this.applicationId = id;
    }

    public String getApplicantNRIC() {
        return applicantNRIC;
    }

    public void setApplicantNRIC(String nric) {
        this.applicantNRIC = nric;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int pid) {
        this.projectID = pid;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public void setApplicationType(ApplicationType t) {
        this.applicationType = t;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus s) {
        this.status = s;
    }

    public String getFlatType() {
        return flatType;
    }

    public void setFlatType(String ft) {
        this.flatType = ft;
    }

    /**
     * The `toString` method overrides the default implementation to return a formatted string
     * representation of the object's attributes.
     * 
     * @return The `toString()` method is being overridden to return a formatted string containing the
     * application ID, project ID, application type, status, and flat type.
     */
    @Override
    public String toString() {
        return String.format(
                "AppID:%d Project:%d Type:%s Status:%s Flat:%s",
                applicationId, projectID, applicationType, status, flatType);
    }

}
