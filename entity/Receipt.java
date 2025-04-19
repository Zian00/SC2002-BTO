package entity;

import entity.enumerations.*;

public class Receipt {

	private int receiptID;
	private String NRIC;
	private String applicantName;
	private int age;
	private MaritalState maritalStatus;
	private String flatType;
	private int projectID;
	private String projectName;
	private String neighborhood;
	private String applicationOpeningDate;
	private String applicationClosingDate;
	private String manager;

	public int getReceiptID() {
		return receiptID;
	}

	public void setReceiptID(int receiptID) {
		this.receiptID = receiptID;
	}

	public String getNRIC() {
		return NRIC;
	}

	public void setNRIC(String NRIC) {
		this.NRIC = NRIC;
	}

	public String getApplicantName() {
		return applicantName;
	}

	public void setApplicantName(String applicantName) {
		this.applicantName = applicantName;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public MaritalState getMaritalStatus() {
		return maritalStatus;
	}

	public void setMaritalStatus(MaritalState maritalStatus) {
		this.maritalStatus = maritalStatus;
	}

	public String getFlatType() {
		return flatType;
	}

	public void setFlatType(String flatType) {
		this.flatType = flatType;
	}

	public int getProjectID() {
		return projectID;
	}

	public void setProjectID(int projectID) {
		this.projectID = projectID;
	}

	public String getProjectName() {
		return projectName;
	}

	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}

	public String getNeighborhood() {
		return neighborhood;
	}

	public void setNeighborhood(String neighborhood) {
		this.neighborhood = neighborhood;
	}

	public String getApplicationOpeningDate() {
		return applicationOpeningDate;
	}

	public void setApplicationOpeningDate(String applicationOpeningDate) {
		this.applicationOpeningDate = applicationOpeningDate;
	}

	public String getApplicationClosingDate() {
		return applicationClosingDate;
	}

	public void setApplicationClosingDate(String applicationClosingDate) {
		this.applicationClosingDate = applicationClosingDate;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
	}

}