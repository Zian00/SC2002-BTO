package models.repositories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import models.BTOProject;

public class BTOProjectCSVRepository {

    private static final String CSV_FILE = "assets/ProjectList.csv";

    /**
     * Reads a CSV with columns:
     * ID, Project Name, Neighborhood, Type (2‑Room/3‑Room),
     * Number of units, Selling price, Opening date, Closing date, Manager
     */
    public List<BTOProject> readBTOProjectFromCSV() {
        List<BTOProject> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            // Skip header line
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
               
    
                String[] t = line.split(",", 12);
        
                int id = Integer.parseInt(t[0].trim()); // This is where it fails
             
                String name = t[1].trim();                            // Project Name
                String neigh = t[2].trim();                           // Neighborhood
                String type = t[3].trim();                            // Type
                int units = Integer.parseInt(t[4].trim());            // Number of units
                String openDate  = t[6].trim();                       // Opening date
                String closeDate = t[7].trim();                       // Closing date
                String mgr       = t[8].trim();                       // Manager
                boolean isVisible = Boolean.parseBoolean(t[11].trim().toLowerCase()); // New column

                // System.out.println("ISVISIBILE: " + isVisible); see visibility
                BTOProject p = new BTOProject();
                p.setProjectID(id);
                p.setProjectName(name);
                p.setNeighborhood(neigh);
                p.setApplicationOpeningDate(openDate);
                p.setApplicationClosingDate(closeDate);
                p.setManager(mgr);
                p.setVisibility(isVisible);
      

                // Assign units to the correct flat type
                if ("2-Room".equalsIgnoreCase(type)) {
                    p.setAvailable2Room(units);
                } else if ("3-Room".equalsIgnoreCase(type)) {
                    p.setAvailable3Room(units);
                }

                list.add(p);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    // writeBTOProjectToCSV(...) TO BE DONE
}
