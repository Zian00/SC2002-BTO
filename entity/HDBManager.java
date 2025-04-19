package entity;
import java.util.List;

import entity.enumerations.MaritalState;
import entity.enumerations.Role;

public class HDBManager extends User {

	public HDBManager(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
			String filterSettings, Role role) {
		super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
	}


	public BTOProject getProjectById(List<BTOProject> projects, int id) {
        if (projects == null) return null;
        return projects.stream()
                .filter(p -> p.getProjectID() == id)
                .findFirst()
                .orElse(null);
    }

}