package models;

import java.util.List;

public class BTOProject {

    private int projectID;
    private String projectName;
    private String neighborhood;
    private int available2Room;
    private int available3Room;
    private int twoRoomPrice;
    private int threeRoomPrice;
    private String applicationOpeningDate;
    private String applicationClosingDate;
    private int availableOfficerSlots;
    private boolean visibility; //default visible, then manager can turn off - bryan
    private String manager;
    private List<String> pendingOfficer;
    private List<String> approvedOfficer;

    public int getProjectID() {
        return projectID;
    }
    public void setProjectID(int projectID) {
        this.projectID = projectID;
    }

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
    public int getTwoRoomPrice() {
        return twoRoomPrice;
    }
    public void setTwoRoomPrice(int twoRoomPrice) {
        this.twoRoomPrice = twoRoomPrice;
    }
    public int getThreeRoomPrice() {
        return threeRoomPrice;
    }
    public void setThreeRoomPrice(int threeRoomPrice) {
        this.threeRoomPrice = threeRoomPrice;
    }

    public String getApplicationOpeningDate() {
        return applicationOpeningDate;
    }
    public void setApplicationOpeningDate(String applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }

    public String getApplicationClosingDate() {
        return applicationClosingDate;
    }
    public void setApplicationClosingDate(String applicationClosingDate) {
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

    public String getManager() {
        return manager;
    }
    public void setManager(String manager) {
        this.manager = manager;
    }

    public List<String> getPendingOfficer() {
        return pendingOfficer;
    }
    public void setPendingOfficer(List<String> pendingOfficer) {
        this.pendingOfficer = pendingOfficer;
    }

    public List<String> getApprovedOfficer() {
        return approvedOfficer;
    }
    public void setApprovedOfficer(List<String> approvedOfficer) {
        this.approvedOfficer = approvedOfficer;
    }

    @Override
    public String toString() {
        return """
               Project ID: %d
               Project Name: %s
               Location: %s
               No. of 2 Rooms: %d (Price: $%d)
               No. of 3 Rooms: %d (Price: $%d)
               Application Open Date: %s
               Application Close Date: %s
               Officer Slots Available: %d
               Visible: %b
               Manager: %s
               Pending Officers: %s
               Approved Officers: %s
               """.formatted(
                projectID,
                projectName,
                neighborhood,
                available2Room,
                twoRoomPrice,
                available3Room,
                threeRoomPrice,
                applicationOpeningDate,
                applicationClosingDate,
                availableOfficerSlots,
                visibility,
                manager,
                pendingOfficer,
                approvedOfficer
        );
    }
}
