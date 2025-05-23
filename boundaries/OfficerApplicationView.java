// The `package boundaries;` statement at the beginning of the Java file is declaring the package name
// for this particular Java class. In Java, packages are used to organize classes into namespaces,
// providing a way to group related classes together.
package boundaries;

import entity.BTOProject;
import entity.OfficerApplication;
import java.util.List;

/**
 * View class for officer application interactions in the BTO system.
 * <p>
 * Provides menu displays and output methods for officers and managers to view,
 * check, and manage officer applications for BTO projects.
 * </p>
 */
public class OfficerApplicationView {

    /**
     * Displays the main menu options for HDB Officers in the officer application module.
     */
    public void displayOfficerMenu() {
        System.out.println("\n=== Officer Application Menu ===");
        System.out.println("1. View All Officer Applications");
        System.out.println("2. Check Registration Status");
        System.out.println("3. Back");
        System.out.print("Select an option: ");
    }

    /**
     * Displays the main menu options for HDB Managers in the officer application module.
     */
    public void displayManagerMenu() {
        System.out.println("\n=== Officer Application Menu ===");
        System.out.println("1. Display All Pending/Successful Applications Managed By You");
        System.out.println("2. Approval / Rejection for Application");
        System.out.println("3. Back");
        System.out.print("Select an option: ");
    }

    /**
     * Displays all applications submitted by a specific officer.
     *
     * @param officerApplicationList List of officer applications by the current user.
     */
    public void displayOfficerApplications(List<OfficerApplication> officerApplicationList) {
        if (officerApplicationList.isEmpty()) {
            System.out.println("You have not submitted any officer applications.");
            return;
        }
    
        System.out.println("\n=== Your Officer Applications ===");
        for (OfficerApplication app : officerApplicationList) {
            System.out.println("Application ID: " + app.getOfficerApplicationId());
            System.out.println("Project ID: " + app.getProjectID());
            // Display the additional project information
            System.out.println("Project Name: " + (app.getProjectName() != null ? app.getProjectName() : "Unknown"));
            System.out.println("Project Location: " + (app.getProjectLocation() != null ? app.getProjectLocation() : "Unknown"));
            System.out.println("Status: " + app.getStatus());
            System.out.println("-------------------------------");
        }
    }

    /**
     * Displays available projects that an officer can apply for.
     *
     * @param eligibleProjects List of projects officer can apply to.
     */
    public void displayEligibleProjects(List<BTOProject> eligibleProjects) {
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects available for officer application.");
            return;
        }

        System.out.println("\n=== Available Projects for Officer Application ===");
        for (BTOProject project : eligibleProjects) {
            System.out.println("Project ID: " + project.getProjectID());
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Available Officer Slots: " + project.getAvailableOfficerSlots());
            System.out.println("Application Period: " + project.getApplicationOpeningDate() +
                    " to " + project.getApplicationClosingDate());
            System.out.println("-------------------------------");
        }
    }

    /**
     * Displays pending applications for projects managed by a specific manager.
     *
     * @param pendingApplications List of pending applications for manager's projects.
     * @param projects            List of all BTO projects for reference.
     */
    public void displayManagerPendingApplications(List<OfficerApplication> pendingApplications,
            List<BTOProject> projects) {
        if (pendingApplications.isEmpty()) {
            System.out.println("No pending officer applications for your projects.");
            return;
        }

        System.out.println("\n=== Pending Officer Applications for Your Projects ===");
        for (OfficerApplication app : pendingApplications) {
            // Find project name for better display
            String projectName = projects.stream()
                    .filter(p -> p.getProjectID() == app.getProjectID())
                    .map(BTOProject::getProjectName)
                    .findFirst()
                    .orElse("Unknown Project");

            System.out.println("Application ID: " + app.getOfficerApplicationId());
            System.out.println("Officer NRIC: " + app.getOfficerNRIC());
            System.out.println("Project: " + app.getProjectID() + " - " + projectName);
            System.out.println("Status: " + app.getStatus());
            System.out.println("-------------------------------");
        }
    }

    /**
     * Displays all officer applications in the system.
     *
     * @param officerApplicationList Complete list of all officer applications.
     * @param projects               List of all BTO projects for reference.
     */
    public void displayAllApplications(List<OfficerApplication> officerApplicationList, List<BTOProject> projects) {
        if (officerApplicationList.isEmpty()) {
            System.out.println("No officer applications found in the system.");
            return;
        }

        System.out.println("\n=== All Officer Applications ===");
        for (OfficerApplication app : officerApplicationList) {
            // Find project name for better display
            String projectName = projects.stream()
                    .filter(p -> p.getProjectID() == app.getProjectID())
                    .map(BTOProject::getProjectName)
                    .findFirst()
                    .orElse("Unknown Project");

            System.out.println("Application ID: " + app.getOfficerApplicationId());
            System.out.println("Officer NRIC: " + app.getOfficerNRIC());
            System.out.println("Project: " + app.getProjectID() + " - " + projectName);
            System.out.println("Status: " + app.getStatus());
            System.out.println("-------------------------------");
        }
    }
}