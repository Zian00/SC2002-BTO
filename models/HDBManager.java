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

	public List<Integer> getBTOProjectID() {
		// TODO - implement HDBManager.getBTOProjectID
		throw new UnsupportedOperationException();
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