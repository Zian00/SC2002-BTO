package models;
import models.enumerations.*;

public class User {

	private String NRIC;
	private String Name;
	private String password;
	private int age;
	private MaritalState maritalStatus;
	private String filterSettings;
	private Role role;

    // Getter methods
    public String getNRIC() {
        return NRIC;
    }

    public String getName() {
        return Name;
    }

    public String getPassword() {
        return password;
    }

    public int getAge() {
        return age;
    }

    public MaritalState getMaritalStatus() {
        return maritalStatus;
    }

    public String getFilterSettings() {
        return filterSettings;
    }

    public Role getRole() {
        return role;
    }

    // Setter method for password only
    public void setPassword(String password) {
        this.password = password;
    }
}