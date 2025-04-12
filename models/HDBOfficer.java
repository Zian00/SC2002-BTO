package models;
import java.util.List;
import models.enumerations.MaritalState;
import models.enumerations.Role;

public class HDBOfficer extends Applicant {

	public HDBOfficer(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
			String filterSettings, Role role) {
		super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
	}

	private List<Integer> BTOInChargeIDList;

	public List<Integer> getBTOInChargeIDList() {
		// TODO - implement HDBOfficer.getBTOInChargeIDList
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param BTOProjectID
	 */
	public void setBTOInChargeIDList(int BTOProjectID) {
		// TODO - implement HDBOfficer.setBTOInChargeIDList
		throw new UnsupportedOperationException();
	}

}