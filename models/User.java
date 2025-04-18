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

    // Initialize all fields
    public User(String NRIC, String Name, String password, int age, MaritalState maritalStatus, String filterSettings, Role role) {
        this.NRIC = NRIC;
        this.Name = Name;
        this.password = password;
        this.age = age;
        this.maritalStatus = maritalStatus;
        this.filterSettings = filterSettings;
        this.role = role;
    }

    // Getter methods
    public String getNRIC() { return NRIC; }
    public String getName() { return Name; }
    public String getPassword() { return password; }
    public int getAge() { return age; }
    public MaritalState getMaritalStatus() { return maritalStatus; }
    public String getFilterSettings() { return filterSettings; }
    public Role getRole() { return role; }

    // Setter method for password only
    public void setPassword(String password) {
        this.password = password;
    }

    //setter method for filtersettings
    public void setFilterSettings(String filterSettings) {
        this.filterSettings = filterSettings;
    }
}