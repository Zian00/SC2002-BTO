package models;

public class Applicant extends User {

	private int applicationID;

	public int getApplicationID() {
		return this.applicationID;
	}

	/**
	 * 
	 * @param applicationID
	 */
	public void setApplicationID(int applicationID) {
		this.applicationID = applicationID;
	}

}