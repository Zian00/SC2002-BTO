package models.repositories;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import models.OfficerApplication;
import models.enumerations.RegistrationStatus;




public class OfficerApplicationCSVRepository {
	
    private static final String CSV_FILE = "assets/officerApplication.csv";
    
    /**
     * Reads a CSV with columns in the following order:
     * 1. officerApplicationID,
     * 2. officerNRIC,
     * 3. projectID,
     * 4. status
     */
    public List<OfficerApplication> readOfficerApplicationsFromCSV() {
        List<OfficerApplication> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            // Skip header line
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                
                String[] tokens = line.split(",", -1);
                int id = Integer.parseInt(tokens[0].trim());
                String nric = tokens[1].trim();
                int projectId = Integer.parseInt(tokens[2].trim());
                RegistrationStatus status = RegistrationStatus.valueOf(tokens[3].trim());
                
                // Create OfficerApplication instance and assign values
                OfficerApplication app = new OfficerApplication();
                app.setOfficerApplicationId(id);
                app.setOfficerNRIC(nric);
                app.setProjectID(projectId);
                app.setStatus(status);
                
                list.add(app);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void writeOfficerApplicationsToCSV(List<OfficerApplication> applications) {
        try (PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(CSV_FILE)))) {
            // Write header
            pw.println("officerApplicationID,officerNRIC,projectID,status");
            
            // Write each application
            for (OfficerApplication app : applications) {
                pw.printf("%d,%s,%d,%s%n",
                    app.getOfficerApplicationId(),
                    app.getOfficerNRIC(),
                    app.getProjectID(),
                    app.getStatus()
                );
            }
        } catch (IOException e) {
            System.err.println("Error writing officerApplication.csv: " + e.getMessage());
        }
    }
}