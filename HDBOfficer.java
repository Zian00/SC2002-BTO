public class HDBOfficer extends Applicant implements IOfficerRegistration {

    private BTOProject officerProject;

    @Override
    public void registerForProject(BTOProject project) {
        // Registration logic for the project
        this.officerProject = project;
    }

    @Override
    public RegistrationStatus viewRegistrationStatus() {
        // Return the registration status (stub)
        return RegistrationStatus.Pending;
    }

    public void replyEnquiry(Enquiry enquiry, String response) {
        // Reply to an enquiry
        // Stub implementation
    }

    public void updateFlatAvailability() {
        // Update flat availability logic
    }

    public BTOApplication retrieveApplication(String NRIC)
    {
        //return BTOApplication given provided NRIC
        
    }

    public void updateApplicationStatus(BTOApplication app, ApplicationStatus status) {
        // Update the application status
        app.setStatus(status);
    }

    public void updateTypeOfFlat(Applicant dude, int flattype)
    {
        //update applicats flat type
    }

    public Receipt generateReceipt(BTOApplication app) {
        // Generate and return a receipt based on the application
        Receipt receipt = new Receipt();
        // Populate receipt details from app and applicant details
        return receipt;
    }
}
