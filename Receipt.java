

public class Receipt {

    private String applicantName;
    private String NRIC;
    private int age;
    private MaritalStatus maritalStatus;
    private String flatType;
    private BTOProject projectDetails;

    // Getters and setters
    public String getApplicantName() {
        return applicantName;
    }
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    public String getNRIC() {
        return NRIC;
    }
    public void setNRIC(String NRIC) {
        this.NRIC = NRIC;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public MaritalStatus getMaritalStatus() {
        return maritalStatus;
    }
    public void setMaritalStatus(MaritalStatus maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public String getFlatType() {
        return flatType;
    }
    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }
    public BTOProject getProjectDetails() {
        return projectDetails;
    }
    public void setProjectDetails(BTOProject projectDetails) {
        this.projectDetails = projectDetails;
    }
}
