package controllers;
import java.util.List;
import javax.management.relation.Role;
import models.User;

public class UserCTRL {

	private List<User> userList;
	private User currentUser;

	/**
	 * 
	 * @param NRIC
	 * @param password
	 * @param Role
	 */
	public boolean login(String NRIC, String password, Role Role) {
		// TODO - implement UserCTRL.login
		throw new UnsupportedOperationException();
	}

	/**
	 * 
	 * @param newPassword
	 */
	public void changePassword(String newPassword) {
		// TODO - implement UserCTRL.changePassword
		throw new UnsupportedOperationException();
	}

	public void getCurrentUser() {
		// TODO - implement UserCTRL.getCurrentUser
		throw new UnsupportedOperationException();
	}

	public void setCurrentUser() {
		// TODO - implement UserCTRL.setCurrentUser
		throw new UnsupportedOperationException();
	}

}