import java.util.ArrayList;
import java.util.List;

public class Applicant extends User implements IEnquiryManagement, IApplicationManagement, IProjectFilter {

    // For simplicity, assuming only one application per applicant
    private BTOApplication application;

    @Override
    public List<BTOProject> filterProjects(String criteria) {
        // Filter projects based on criteria and return the list
        return new ArrayList<>();
    }

    @Override
    public void applyProject(BTOProject project) {
        // Logic for applying for a project
        // For instance, create a new application for this project
        this.application = new BTOApplication();
        this.application.setProject(project);
        // Set applicant reference
        this.application.setApplicant(this);
    }

    @Override
    public void withdrawApplication() {
        // Logic to withdraw an application
        this.application = null;
    }

    public List<BTOProject> viewProjects() {
        // Return available projects (stub implementation)
        return new ArrayList<>();
    }

    @Override
    public void submitEnquiry(String text) {
        // Submit a new enquiry
        Enquiry enquiry = new Enquiry();
        enquiry.setEnquiryText(text);
        enquiry.setSubmittedBy(this);
        // Additional logic...
    }

    @Override
    public void editEnquiry(Enquiry enquiry, String newText) {
        // Edit the enquiry text
        enquiry.edit(newText);
    }

    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        // Delete the enquiry (stub)
        enquiry.delete();
    }
}
