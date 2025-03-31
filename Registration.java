

public class Registration {

    private HDBOfficer officer;
    private BTOProject project;
    private RegistrationStatus status;

    public void updateStatus(RegistrationStatus newStatus) {
        this.status = newStatus;
    }

    // Getters and setters
    public HDBOfficer getOfficer() {
        return officer;
    }
    public void setOfficer(HDBOfficer officer) {
        this.officer = officer;
    }
    public BTOProject getProject() {
        return project;
    }
    public void setProject(BTOProject project) {
        this.project = project;
    }
    public RegistrationStatus getStatus() {
        return status;
    }
    public void setStatus(RegistrationStatus status) {
        this.status = status;
    }
}
