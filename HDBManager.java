import java.util.ArrayList;
import java.util.List;

public class HDBManager extends User implements IProjectManagement, IEnquiryManagement, IProjectFilter {


    /* missing: 
     * Can only be handling one project within an application period (from
        application opening date, inclusive, to application closing date,
        inclusive) 
     */
    private List<BTOProject> projects = new ArrayList<>();
    //Able to create, edit, and delete BTO project listings. can
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
    //Able to toggle the visibility of the project to “on” or “off”. This will be
    //reflected in the project list that will be visible to applicants
    public void toggleVisibility(BTOProject project) {
        project.setVisibility(!project.isVisibility());
    }

    @Override
    public List<BTOProject> filterProjects(String criteria) {
        // Filter projects based on criteria and return the list
        //• Able to filter and view the list of projects that they have created only.
        //
        List<BTOProject> filtered = new ArrayList<>();
        // Add filtering logic here
        return filtered;
    }

    public List<Registration> viewOfficerRegistrations() {
        //Able to view pending and approved HDB Officer registration. 
        // Return a list of officer registrations
        return new ArrayList<>();
    }

    public void approveOfficerRegistration(Registration reg) {
        /*
         * Able to approve or reject HDB Officer’s registration as the HDB
            Manager in-charge of the project – update project’s remaining HDB
            Officer slots
         */
        reg.updateStatus(RegistrationStatus.Approved);
    }

    public void rejectOfficerRegistration(Registration reg) {
        
        reg.updateStatus(RegistrationStatus.Rejected);
    }

    public void approveApplication(BTOApplication app) {
        /*
         * Able to approve or reject Applicant’s BTO application – approval is
            limited to the supply of the flats (number of units for the respective flat
            types) 
         */
        //Able to approve or reject Applicant's request to withdraw the application. (but what is this based on)
        app.setStatus(ApplicationStatus.Successful);
    }

    public void rejectApplication(BTOApplication app) {
        //Able to approve or reject Applicant's request to withdraw the application. 
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

    //missing: need a method Able to view enquiries of ALL projects
}
