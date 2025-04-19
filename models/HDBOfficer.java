package models;
import models.enumerations.MaritalState;
import models.enumerations.Role;

public class HDBOfficer extends Applicant {

	public HDBOfficer(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
			String filterSettings, Role role) {
		super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
	}



}