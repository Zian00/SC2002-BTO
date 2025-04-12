package models;

import models.enumerations.MaritalState;
import models.enumerations.Role;

public class Applicant extends User {

	public Applicant(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
			String filterSettings, Role role) {
		super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
	}

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