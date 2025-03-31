import java.util.Date;

public class BTOProject {

    private String projectName;
    private String neighborhood;
    private int available2Room;
    private int available3Room;
    private Date applicationOpeningDate;
    private Date applicationClosingDate;
    private int availableOfficerSlots;
    private boolean visibility;
    private HDBManager manager;

    // Getters and setters
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
    public int getAvailable2Room() {
        return available2Room;
    }
    public void setAvailable2Room(int available2Room) {
        this.available2Room = available2Room;
    }
    public int getAvailable3Room() {
        return available3Room;
    }
    public void setAvailable3Room(int available3Room) {
        this.available3Room = available3Room;
    }
    public Date getApplicationOpeningDate() {
        return applicationOpeningDate;
    }
    public void setApplicationOpeningDate(Date applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }
    public Date getApplicationClosingDate() {
        return applicationClosingDate;
    }
    public void setApplicationClosingDate(Date applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }
    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }
    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
    }
    public boolean isVisibility() {
        return visibility;
    }
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }
    public HDBManager getManager() {
        return manager;
    }
    public void setManager(HDBManager manager) {
        this.manager = manager;
    }
}
