package models;
import java.util.List;
import models.enumerations.MaritalState;
import models.enumerations.Role;

public class HDBManager extends User {

	public HDBManager(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
			String filterSettings, Role role) {
		super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
	}

	private List<Integer> BTOProjectIDList;

	public BTOProject getProjectById(List<BTOProject> projects, int id) {
        if (projects == null) return null;
        return projects.stream()
                .filter(p -> p.getProjectID() == id)
                .findFirst()
                .orElse(null);
    }

	/**
	 * 
	 * @param BTOProjectID
	 */
	public void setBTOProjectID(int BTOProjectID) {
		// TODO - implement HDBManager.setBTOProjectID
		throw new UnsupportedOperationException();
	}

}