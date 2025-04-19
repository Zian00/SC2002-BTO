package models;

import models.enumerations.RegistrationStatus;

public class OfficerApplication {
    private int officerApplicationID;
    private String officerNRIC;
    private int projectID;
    private RegistrationStatus status;

    // New fields to store project information
    private String projectName;
    private String projectLocation;

    public int getOfficerApplicationId() {
        return officerApplicationID;
    }

    public void setOfficerApplicationId(int id) {
        this.officerApplicationID = id;
    }

    public String getOfficerNRIC() {
        return officerNRIC;
    }

    public void setOfficerNRIC(String nric) {
        this.officerNRIC = nric;
    }

    public int getProjectID() {
        return projectID;
    }

    public void setProjectID(int pid) {
        this.projectID = pid;
    }

    public RegistrationStatus getStatus() {
        return status;
    }

    public void setStatus(RegistrationStatus s) {
        this.status = s;
    }

    // New getters and setters for project information
    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String name) {
        this.projectName = name;
    }

    public String getProjectLocation() {
        return projectLocation;
    }

    public void setProjectLocation(String location) {
        this.projectLocation = location;
    }

    @Override
    public String toString() {
        return String.format(
                "RegID:%d Project:%d [%s at %s] Status:%s ",
                officerApplicationID, projectID, projectName, projectLocation, status);
    }
}