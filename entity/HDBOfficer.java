package entity;

import entity.enumerations.MaritalState;
import entity.enumerations.Role;

public class HDBOfficer extends Applicant {

	public HDBOfficer(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
			String filterSettings, Role role) {
		super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
	}

}