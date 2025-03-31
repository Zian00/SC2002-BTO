import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User implements IProjectManagement, IEnquiryManagement, IProjectFilter {

    private List<BTOProject> projects = new ArrayList<>();

    @Override
    public void createProject(BTOProject project) {
        projects.add(project);
    }
 
    @Override
    public void editProject(BTOProject project) {
        // Edit the project details here
    }

    @Override
    public void deleteProject(BTOProject project) {
        projects.remove(project);
    }

    @Override
    public void toggleVisibility(BTOProject project) {
        project.setVisibility(!project.isVisibility());
    }

    @Override
    public List<BTOProject> filterProjects(String criteria) {
        // Filter projects based on criteria and return the list
        List<BTOProject> filtered = new ArrayList<>();
        // Add filtering logic here
        return filtered;
    }

    public List<Registration> viewOfficerRegistrations() {
        // Return a list of officer registrations
        return new ArrayList<>();
    }

    public void approveOfficerRegistration(Registration reg) {
        reg.updateStatus(RegistrationStatus.Approved);
    }

    public void rejectOfficerRegistration(Registration reg) {
        reg.updateStatus(RegistrationStatus.Rejected);
    }

    public void approveApplication(BTOApplication app) {
        app.setStatus(ApplicationStatus.Successful);
    }

    public void rejectApplication(BTOApplication app) {
        app.setStatus(ApplicationStatus.Unsuccessful);
    }

    public Report generateReport() {
        Report report = new Report();
        report.setContent("Report Content");
        return report;
    }

    @Override
    public void submitEnquiry(String text) {
        // Submit an enquiry as a manager
        Enquiry enquiry = new Enquiry();
        enquiry.setEnquiryText(text);
        // Additional logic...
    }

    @Override
    public void editEnquiry(Enquiry enquiry, String newText) {
        enquiry.edit(newText);
    }

    @Override
    public void deleteEnquiry(Enquiry enquiry) {
        enquiry.delete();
    }

    public void replyEnquiry(Enquiry enquiry, String response) {
        // Reply to an enquiry (similar to HDBOfficer)
    }
}
