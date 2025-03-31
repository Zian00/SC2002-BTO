public class BTOApplication {

    private ApplicationStatus status;
    private Applicant applicant;
    private BTOProject project;
    private String flatType;

    public void apply() {
        // Application logic here
    }

    public void withdraw() {
        // Withdraw logic here
    }

    public void bookFlat(String flatType) {
        this.flatType = flatType;
        // Additional booking logic here
    }

    // Getters and setters
    public ApplicationStatus getStatus() {
        return status;
    }
    public void setStatus(ApplicationStatus status) {
        this.status = status;
    }
    public Applicant getApplicant() {
        return applicant;
    }
    public void setApplicant(Applicant applicant) {
        this.applicant = applicant;
    }
    public BTOProject getProject() {
        return project;
    }
    public void setProject(BTOProject project) {
        this.project = project;
    }
    public String getFlatType() {
        return flatType;
    }
    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }
}
