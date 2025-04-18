package controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import models.BTOApplication;
import models.BTOProject;
import models.OfficerApplication;
import models.User;
import models.enumerations.RegistrationStatus;
import models.repositories.ApplicationCSVRepository;
import models.repositories.BTOProjectCSVRepository;
import models.repositories.OfficerApplicationCSVRepository;
import models.enumerations.*;

public class OfficerApplicationCTRL {
    

    private List<OfficerApplication> officerApplicationList;
    private List<BTOApplication> btoApplicationList;
    private List<BTOProject> projects;
    private User currentUser;
    private OfficerApplicationCSVRepository officerRepo;
    private ApplicationCSVRepository appRepo;
    private BTOProjectCSVRepository projRepo;

    /**
     * Load all applications and projects, keep track of the logged-in user
     */




    public Role getCurrentUserRole()
    {
        return currentUser.getRole();
    }

    public OfficerApplicationCTRL(User currentUser) {
        this.currentUser = currentUser;
        this.officerRepo = new OfficerApplicationCSVRepository();
        this.appRepo = new ApplicationCSVRepository();
        this.projRepo = new BTOProjectCSVRepository();
        
        // Load all data
        this.officerApplicationList = officerRepo.readOfficerApplicationsFromCSV();
        this.btoApplicationList = appRepo.readApplicationFromCSV();
        this.projects = projRepo.readBTOProjectFromCSV();
    }

    /**
     * View all officer applications submitted by current user
     */
    public List<OfficerApplication> viewUserOfficerApplications() {
        List<OfficerApplication> userApplications = officerApplicationList.stream()
                .filter(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()))
                .collect(Collectors.toList());
                
        // Enhance each application with project information
        for (OfficerApplication app : userApplications) {
            // Find the corresponding project
            BTOProject project = projects.stream()
                    .filter(p -> p.getProjectID() == app.getProjectID())
                    .findFirst()
                    .orElse(null);
                    
            if (project != null) {
                // Set additional project information in the application
                app.setProjectName(project.getProjectName());
                app.setProjectLocation(project.getNeighborhood());
                
            }
        }
        
        return userApplications;
    }
    
    /**
     * Register as an officer for a project if eligible
     * @param projectId The project to register for
     * @return true if registration was successful, false otherwise
     */
    public boolean registerAsOfficer(int projectId) {
        // Find the project
        BTOProject project = projects.stream()
                .filter(p -> p.getProjectID() == projectId )
                .findFirst()
                .orElse(null);
                
        if (project == null) {
            System.out.println("Project not found.");
            return false;
        }
        
        // Check if already applied for this project as applicant
        boolean hasAppliedAsApplicant = btoApplicationList.stream()
                .anyMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()) && 
                               a.getProjectID() == projectId);
                               
        if (hasAppliedAsApplicant) {
            System.out.println("Cannot register as officer for a project you've applied to as an applicant.");
            return false;
        }
        
        // Check if already registered for this project as officer
        boolean alreadyRegistered = officerApplicationList.stream()
                .anyMatch(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) && 
                               a.getProjectID() == projectId);
                               
        if (alreadyRegistered) {
            System.out.println("You've already registered as an officer for this project.");
            return false;
        }
        
        // Check if already approved as officer for another project with overlapping dates
        boolean isOfficerElsewhere = false;
        for (BTOProject p : projects) {
            if (p.getApprovedOfficer() != null && 
                p.getApprovedOfficer().contains(currentUser.getNRIC()) &&
                datesOverlap(p, project)) {
                isOfficerElsewhere = true;
                break;
            }
        }
        
        if (isOfficerElsewhere) {
            System.out.println("Already an officer for another project with overlapping application period.");
            return false;
        }
        
        // Check if there are available slots
        if (project.getAvailableOfficerSlots() <= 0) {
            System.out.println("No available officer slots for this project.");
            return false;
        }
        
        // Create new officer application
        OfficerApplication app = new OfficerApplication();
        app.setOfficerApplicationId(getNextOfficerApplicationID());
        app.setOfficerNRIC(currentUser.getNRIC());
        app.setProjectID(projectId);
        app.setStatus(RegistrationStatus.PENDING);
        
        // Add to pending list in project
        List<String> pendingOfficers = project.getPendingOfficer();
        if (pendingOfficers == null) {
            pendingOfficers = new ArrayList<>();
        }
        pendingOfficers.add(currentUser.getNRIC());
        project.setPendingOfficer(pendingOfficers);
        
        // Save changes
        officerApplicationList.add(app);
        officerRepo.writeOfficerApplicationsToCSV(officerApplicationList);
        projRepo.writeBTOProjectToCSV(projects);
        
        return true;
    }
    
    /**
     * Get all pending officer applications for projects managed by current user
     */
    public List<OfficerApplication> getPendingOfficerApplicationsForManager() {
        List<Integer> managedProjectIds = projects.stream()
                .filter(p -> p.getManager().equals(currentUser.getNRIC()))
                .map(BTOProject::getProjectID)
                .collect(Collectors.toList());
                
        return officerApplicationList.stream()
                .filter(app -> managedProjectIds.contains(app.getProjectID()) &&
                               app.getStatus() == RegistrationStatus.PENDING)
                .collect(Collectors.toList());
    }
    
    /**
     * Process an officer application decision (approve/reject)
     * @param applicationId ID of the application to process
     * @param decision "A" for approve, "R" for reject
     * @return true if successful, false otherwise
     */
    public boolean processOfficerApplicationDecision(int applicationId, String decision) {
        // Find the application
        Optional<OfficerApplication> appOpt = officerApplicationList.stream()
		
                .filter(a -> a.getOfficerApplicationId() == applicationId && 
                             a.getStatus() == RegistrationStatus.PENDING)
                .findFirst();
                
        if (appOpt.isEmpty()) {
            System.out.println("Application not found or not pending.");
            return false;
        }
        
        OfficerApplication app = appOpt.get();
        
        // Find the project
        BTOProject project = projects.stream()
                .filter(p -> p.getProjectID() == app.getProjectID())
                .findFirst()
                .orElse(null);
                
        if (project == null) {
            System.out.println("Project not found.");
            return false;
        }
        
        // Verify this manager manages this project
        if (!project.getManager().equals(currentUser.getNRIC())) {
            System.out.println("You do not manage this project.");
            return false;
        }
        
        if (decision.equalsIgnoreCase("A")) {
            // Approve application
            
            // Update application status
            app.setStatus(RegistrationStatus.APPROVED);
            
            // Update project officer lists
            List<String> pendingOfficers = project.getPendingOfficer();
            if (pendingOfficers != null) {
                pendingOfficers.remove(app.getOfficerNRIC());
            }
            
            List<String> approvedOfficers = project.getApprovedOfficer();
            if (approvedOfficers == null) {
                approvedOfficers = new ArrayList<>();
            }
            approvedOfficers.add(app.getOfficerNRIC());
            
            project.setPendingOfficer(pendingOfficers);
            project.setApprovedOfficer(approvedOfficers);
            
        } else if (decision.equalsIgnoreCase("R")) {
            // Reject application
            
            // Update application status
            app.setStatus(RegistrationStatus.REJECTED);
            
            // Remove from pending list
            List<String> pendingOfficers = project.getPendingOfficer();
            if (pendingOfficers != null) {
                pendingOfficers.remove(app.getOfficerNRIC());
                project.setPendingOfficer(pendingOfficers);
            }
            
        } else {
            System.out.println("Invalid decision. Use 'A' to approve or 'R' to reject.");
            return false;
        }
        
        // Save changes
        officerRepo.writeOfficerApplicationsToCSV(officerApplicationList);
        projRepo.writeBTOProjectToCSV(projects);
        
        return true;
    }
    
    /**
     * Check if current user is an approved officer for a project
     * @param projectId Project to check
     * @return true if user is an approved officer, false otherwise
     */
    public boolean isApprovedOfficerForProject(int projectId) {
        BTOProject project = projects.stream()
                .filter(p -> p.getProjectID() == projectId)
                .findFirst()
                .orElse(null);
                
        if (project == null || project.getApprovedOfficer() == null) {
            return false;
        }
        
        return project.getApprovedOfficer().contains(currentUser.getNRIC());
    }
    
    /**
     * Check if dates overlap between two projects
     */
    private boolean datesOverlap(BTOProject p1, BTOProject p2) {
        // Simple string comparison - assumes consistent date format
        return !(p1.getApplicationClosingDate().compareTo(p2.getApplicationOpeningDate()) < 0 ||
                 p2.getApplicationClosingDate().compareTo(p1.getApplicationOpeningDate()) < 0);
    }
    
    /**
     * Get the next available officer application ID
     */
    private int getNextOfficerApplicationID() {
        return officerApplicationList.stream()
                .mapToInt(OfficerApplication::getOfficerApplicationId)
                .max()
                .orElse(0) + 1;
    }
    
    /**
     * Get all projects user can apply to be an officer for
     */
    public List<BTOProject> getEligibleOfficerProjects() {
        return projects.stream()
                .filter(p -> p.isVisibility() && p.getAvailableOfficerSlots() > 0)
                .filter(p -> {
                    // Not already applied for this project as applicant
                    boolean notAppliedAsApplicant = btoApplicationList.stream()
                            .noneMatch(a -> a.getApplicantNRIC().equals(currentUser.getNRIC()) && 
                                           a.getProjectID() == p.getProjectID());
                    
                    // Not already registered for this project as officer
                    boolean notRegisteredAsOfficer = officerApplicationList.stream()
                            .noneMatch(a -> a.getOfficerNRIC().equals(currentUser.getNRIC()) && 
                                           a.getProjectID() == p.getProjectID());
                    
                    // No overlapping commitments as officer for other projects
                    boolean noOverlappingCommitments = projects.stream()
                            .filter(other -> other.getApprovedOfficer() != null && 
                                           other.getApprovedOfficer().contains(currentUser.getNRIC()))
                            .noneMatch(other -> datesOverlap(other, p));
                    
                    return notAppliedAsApplicant && notRegisteredAsOfficer && noOverlappingCommitments;
                })
                .collect(Collectors.toList());
    }
}