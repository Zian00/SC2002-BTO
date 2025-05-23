package entity.repositories;

import entity.BTOProject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

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
    /**
     * The function reads BTO project data from a CSV file, parses the information,
     * and creates a list
     * of BTOProject objects.
     * 
     * @return The method `readBTOProjectFromCSV` returns a `List` of `BTOProject`
     *         objects read from a
     *         CSV file.
     */
    public List<BTOProject> readBTOProjectFromCSV() {
        List<BTOProject> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            // Skip header line
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;

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
                p.setManagerID(manager);
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
            System.out.println("Error reading ProjectList.csv: " + e.getMessage());
        }
        return list;
    }

    /**
     * The `writeBTOProjectToCSV` function writes a list of BTOProject objects to a
     * CSV file in a
     * specific 16-column format.
     * 
     * @param projects The `writeBTOProjectToCSV` method takes a list of
     *                 `BTOProject` objects as input
     *                 and writes the data from these objects to a CSV file in a
     *                 specific format. Each `BTOProject`
     *                 object represents a project with various attributes such as
     *                 project ID, project name,
     *                 neighborhood
     */
    public void writeBTOProjectToCSV(List<BTOProject> projects) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE)))) {
            // header (match your CSV columns exactly)
            // 1) Write the exact original header
            pw.println(
                    "ID,Project Name,Neighborhood,Type 1,Number of units for Type 1,"
                            + "Selling price for Type 1,Type 2,Number of units for Type 2,"
                            + "Selling price for Type 2,Application opening date,"
                            + "Application closing date,ManagerID,Officer Slot,"
                            + "Pending Officer by NRIC,Approved Officer by NRIC,Visibility");

            // 2) Write each project in the 16‑column format
            for (BTOProject p : projects) {
                String pending = p.getPendingOfficer() == null
                        ? ""
                        : String.join(",", p.getPendingOfficer());
                String approved = p.getApprovedOfficer() == null
                        ? ""
                        : String.join(",", p.getApprovedOfficer());

                pw.printf(
                        "%d,%s,%s,%s,%d,%d,%s,%d,%d,%s,%s,%s,%d,\"%s\",\"%s\",%s%n",
                        // 1. ID
                        p.getProjectID(),
                        // 2. Project Name
                        p.getProjectName(),
                        // 3. Neighborhood
                        p.getNeighborhood(),
                        // 4. Type 1 identifier
                        "2-Room",
                        // 5. Number of units for Type 1
                        p.getAvailable2Room(),
                        // 6. Price for Type 1
                        p.getTwoRoomPrice(),
                        // 7. Type 2 identifier
                        "3-Room",
                        // 8. Number of units for Type 2
                        p.getAvailable3Room(),
                        // 9. Price for Type 2
                        p.getThreeRoomPrice(),
                        // 10. Opening date
                        p.getApplicationOpeningDate(),
                        // 11. Closing date
                        p.getApplicationClosingDate(),
                        // 12. ManagerID
                        p.getManagerID(),
                        // 13. Officer Slot
                        p.getAvailableOfficerSlots(),
                        // 14. Pending Officer by NRIC (quoted comma‑list)
                        pending,
                        // 15. Approved Officer by NRIC (quoted comma‑list)
                        approved,
                        // 16. Visibility (uppercase)
                        String.valueOf(p.isVisibility()).toUpperCase());
            }

        } catch (IOException e) {
            System.err.println("Error writing ProjectList.csv: " + e.getMessage());
        }
    }

}