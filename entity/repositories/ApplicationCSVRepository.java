package entity.repositories;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import entity.BTOApplication;
import entity.enumerations.ApplicationStatus;
import entity.enumerations.ApplicationType;

public class ApplicationCSVRepository {
    private static final String CSV_FILE = "assets/BTOApplication.csv";

    /**
     * This Java function reads BTO applications from a CSV file and populates a list of BTOApplication
     * objects with the data.
     * 
     * @return A List of BTOApplication objects is being returned.
     */
    public List<BTOApplication> readApplicationFromCSV() {
        List<BTOApplication> applications = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            // Skip header
            br.readLine();

            String line;
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;

                String[] data = line.split(",");
                BTOApplication app = new BTOApplication();
                app.setApplicationId(Integer.parseInt(data[0].trim()));
                app.setApplicantNRIC(data[1].trim());
                app.setProjectID(Integer.parseInt(data[2].trim()));
                app.setApplicationType(ApplicationType.valueOf(data[3].trim()));
                app.setStatus(ApplicationStatus.valueOf(data[4].trim()));
                app.setFlatType(data[5].trim());

                applications.add(app);
            }
        } catch (IOException e) {
            System.out.println("Error reading applications: " + e.getMessage());
        }
        return applications;
    }

    /**
     * The `writeApplicationToCSV` function writes a list of BTO applications to a CSV file with
     * specific data fields.
     * 
     * @param applications A list of BTOApplication objects containing application data such as
     * application ID, applicant NRIC, project ID, application type, status, and flat type. The method
     * `writeApplicationToCSV` writes this application data to a CSV file.
     */
    public void writeApplicationToCSV(List<BTOApplication> applications) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            pw.println("applicationID,applicantNRIC,projectID,applicationType,status,flatType");

            // Write data
            for (BTOApplication app : applications) {
                pw.printf("%d,%s,%d,%s,%s,%s%n",
                        app.getApplicationId(),
                        app.getApplicantNRIC(),
                        app.getProjectID(),
                        app.getApplicationType(),
                        app.getStatus(),
                        app.getFlatType());
            }
        } catch (IOException e) {
            System.out.println("Error writing applications: " + e.getMessage());
        }
    }
}