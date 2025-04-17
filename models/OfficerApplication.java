package models;

import models.enumerations.RegistrationStatus;

public class OfficerApplication {

	private int officerApplicationID;
	private String officerNRIC;
	private int projectID;
	private RegistrationStatus status;

	/**
	 * 
	 * @param newStatus
	 */
	public void updateStatus(RegistrationStatus newStatus) {
		// TODO - implement OfficerApplication.updateStatus
		throw new UnsupportedOperationException();
	}
	 public int getOfficerApplicationId() { return officerApplicationID; }
    public void setOfficerApplicationId(int id) { this.officerApplicationID = id; }

    public String getOfficerNRIC() { return officerNRIC; }
    public void setOfficerNRIC(String nric) { this.officerNRIC = nric; }

    public int getProjectID() { return projectID; }
    public void setProjectID(int pid) { this.projectID = pid; }

    public RegistrationStatus getStatus() { return status; }
    public void setStatus(RegistrationStatus s) { this.status = s; }


    @Override
    public String toString() {
        return String.format(
                "RegID:%d Project:%d Status:%s ",
                officerApplicationID, projectID,  status);
    }
}