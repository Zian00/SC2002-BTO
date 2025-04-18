package models.repositories;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import models.Enquiry;

public class EnquiryCSVRepository {
    private static final String CSV_FILE = "assets/enquiryList.csv";

    /**
     * Reads a CSV file with the following columns (in order):
     * 1. Enquiry ID,
     * 2. Enquiry Text,
     * 3. Submitted By NRIC,
     * 4. Project ID,
     * 5. Response,
     * 6. Timestamp
     */
    public List<Enquiry> readEnquiriesFromCSV() {
        List<Enquiry> enquiries = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(CSV_FILE), StandardCharsets.UTF_8))) {
            // Skip header line
            br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank())
                    continue;

                // Split into 6 parts; empty trailing fields will be preserved.
                String[] tokens = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1);
                if (tokens.length < 6) {
                    System.out.println("Warning: insufficient tokens in line: " + line);
                    continue;
                }

                try {
                    int id = Integer.parseInt(tokens[0].trim());
                    String enquiryText = unescapeCSV(tokens[1].trim());
                    String submittedByNRIC = tokens[2].trim();
                    int projectId = Integer.parseInt(tokens[3].trim());
                    String response = unescapeCSV(tokens[4].trim());
                    String timestamp = tokens[5].trim();

                    Enquiry enquiry = new Enquiry();
                    enquiry.setEnquiryId(id);
                    enquiry.setEnquiryText(enquiryText);
                    enquiry.setSubmittedByNRIC(submittedByNRIC);
                    enquiry.setProjectId(projectId);
                    enquiry.setResponse(response);
                    enquiry.setTimestamp(timestamp);

                    enquiries.add(enquiry);
                } catch (Exception e) {
                    System.out.println("Error parsing line: " + line);
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return enquiries;
    }

    //helper method for " " bug
    private String unescapeCSV(String value) {
        if (value == null || value.isEmpty()) {
            return "";
        }
        // If value is surrounded by quotes, remove them and unescape inner quotes
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
            value = value.replace("\"\"", "\"");
        }
        return value;
    }
    /**
     * Writes the list of Enquiry objects to CSV.
     * The CSV is written with the following columns:
     * EnquiryID, EnquiryText, SubmittedByNRIC, ProjectID, Response, Timestamp
     */
    public void writeEnquiriesToCSV(List<Enquiry> enquiryList) {
        try (PrintWriter pw = new PrintWriter(
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(CSV_FILE), StandardCharsets.UTF_8)))) {
            // Write header line
            pw.println("EnquiryID,EnquiryText,SubmittedByNRIC,ProjectID,Response,Timestamp");
            
            for (Enquiry enquiry : enquiryList) {
                StringBuilder sb = new StringBuilder();
                sb.append(enquiry.getEnquiryId()).append(",");
                // Only escape enquiryText and response, others are written directly.
                sb.append(escapeCSV(enquiry.getEnquiryText())).append(",");
                sb.append(enquiry.getSubmittedByNRIC()).append(",");
                sb.append(enquiry.getProjectId()).append(",");
                sb.append(escapeCSV(enquiry.getResponse())).append(",");
                sb.append(enquiry.getTimestamp());
                pw.println(sb.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Escapes a value for CSV output.
     * Surrounds the value with quotes if it contains a comma, quote, or newline. 
     */
    private String escapeCSV(String value) {
        if (value == null) {
            return "";
        }
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            value = value.replace("\"", "\"\"");
            return "\"" + value + "\"";
        } else {
            return value;
        }
    }
}