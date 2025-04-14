package models.repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import models.BTOProject;

public class BTOProjectCSVRepository {

    private static final String CSV_FILE = "assets/projectList.csv";

    /**
     * Reads a CSV with columns in the following order:
     * 1. ID,
     * 2. Project Name,
     * 3. Neighborhood,
     * 4. Type 1 (identifier only),
     * 5. Number of Units for Type 1,
     * 6. Selling Price for Type 1,
     * 7. Type 2 (identifier only),
     * 8. Number of Units for Type 2,
     * 9. Selling Price for Type 2,
     * 10. Application Opening Date,
     * 11. Application Closing Date,
     * 12. ManagerID,
     * 13. Officer Slot,
     * 14. Pending Officer by NRIC (can be multiple, comma delimited),
     * 15. Approved Officer by NRIC (can be multiple, comma delimited),
     * 16. Visibility
     */
    public List<BTOProject> readBTOProjectFromCSV() {
        List<BTOProject> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            // Skip header line
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
    
                // Split into 16 parts; empty trailing fields will be preserved.
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
    
                int id = Integer.parseInt(tokens[0].trim());
                String name = tokens[1].trim();
                String location = tokens[2].trim();
    
                // Column 4 (Type 1) is an identifier – not used in logic
                int unitsType1 = Integer.parseInt(tokens[4].trim());
                int priceType1 = Integer.parseInt(tokens[5].trim());
    
                // Column 7 (Type 2) is an identifier – not used in logic
                int unitsType2 = Integer.parseInt(tokens[7].trim());
                int priceType2 = Integer.parseInt(tokens[8].trim());
    
                String openDate = tokens[9].trim();
                String closeDate = tokens[10].trim();
                String manager = tokens[11].trim();
                int officerSlots = Integer.parseInt(tokens[12].trim());
    
                // Pending Officers (can be empty; if not, officers are split by comma)
                // Update the officer parsing section to properly handle quoted fields
                String pendingRaw = tokens[13].trim().replaceAll("^\"|\"$", "");
                List<String> pending = new ArrayList<>();
                if (!pendingRaw.isEmpty()) {
                    String[] pendingArray = pendingRaw.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    for (String s : pendingArray) {
                        if (!s.isBlank()) {
                            pending.add(s.trim().replace("\"", ""));
                        }
                    }
                }
    
                // Approved Officers (can be empty; if not, officers are split by comma)
                String approvedRaw = tokens[14].trim().replaceAll("^\"|\"$", "");
                List<String> approved = new ArrayList<>();
                if (!approvedRaw.isEmpty()) {
                    String[] approvedArray = approvedRaw.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    for (String s : approvedArray) {
                        if (!s.isBlank()) {
                            approved.add(s.trim().replace("\"", ""));
                        }
                    }
                }
    
                boolean isVisible = Boolean.parseBoolean(tokens[15].trim().toLowerCase());
    
                // Create BTOProject instance and assign values
                BTOProject p = new BTOProject();
                p.setProjectID(id);
                p.setProjectName(name);
                p.setNeighborhood(location);
                p.setApplicationOpeningDate(openDate);
                p.setApplicationClosingDate(closeDate);
                p.setManager(manager);
                p.setVisibility(isVisible);
                p.setAvailableOfficerSlots(officerSlots);
    
                // Directly assign unit numbers and prices without checking type identifiers.
                p.setAvailable2Room(unitsType1);
                p.setTwoRoomPrice(priceType1);
                p.setAvailable3Room(unitsType2);
                p.setThreeRoomPrice(priceType2);
    
                p.setPendingOfficer(pending);
                p.setApprovedOfficer(approved);
    
                list.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    // TODO: Implement writeBTOProjectToCSV if needed. DEFINITELY NEED

    public void writeBTOProjectToCSV(List<BTOProject> projects) {
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new FileWriter(CSV_FILE)))) {

            // 1) Write header (match your CSV columns exactly)
            pw.println(
                "projectID,projectName,neighborhood," +
                "available2Room,available3Room,twoRoomPrice,threeRoomPrice," +
                "applicationOpeningDate,applicationClosingDate," +
                "availableOfficerSlots,visibility,manager," +
                "pendingOfficer,approvedOfficer"
            );

            // 2) Write each project
            for (BTOProject p : projects) {
                String pending = p.getPendingOfficer() == null
                    ? "" : String.join(";", p.getPendingOfficer());
                String approved = p.getApprovedOfficer() == null
                    ? "" : String.join(";", p.getApprovedOfficer());

                pw.printf(
  "%d,%s,%s,%d,%d,%d,%d,%s,%s,%s,%d,%s,%s,%b%n",
   p.getProjectID(),
   p.getProjectName(),
   p.getNeighborhood(),
   p.getAvailable2Room(), p.getTwoRoomPrice(),
   p.getAvailable3Room(), p.getThreeRoomPrice(),
   p.getApplicationOpeningDate(),
   p.getApplicationClosingDate(),
   p.getManager(),
   p.getAvailableOfficerSlots(),
   String.join(";", p.getPendingOfficer()),
   String.join(";", p.getApprovedOfficer()),
   p.isVisibility()
);
            }

        } catch (IOException e) {
            System.out.println("Error writing ProjectList.csv (is it open elsewhere?)");
            e.printStackTrace();
        }
    }
}