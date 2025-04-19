package entity.repositories;

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

import entity.Enquiry;

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
    /**
     * This Java function reads Enquiry data from a CSV file, parses the lines, and
     * creates Enquiry
     * objects to store the information.
     * 
     * @return This method `readEnquiriesFromCSV` returns a List of Enquiry objects
     *         read from a CSV
     *         file.
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

    // helper method for " " bug
    /**
     * The `unescapeCSV` function removes surrounding quotes and unescapes inner
     * quotes in a CSV value.
     * 
     * @param value The `value` parameter in the `unescapeCSV` method is a string
     *              that represents a
     *              value from a CSV file. The method is designed to unescape any
     *              escaped characters, particularly
     *              double quotes, within the value. If the value is surrounded by
     *              quotes, the method removes the
     *              outer quotes and un
     * @return The `unescapeCSV` method returns the unescaped and potentially
     *         dequoted version of the
     *         input `value` string. If the input string is null or empty, an empty
     *         string is returned. If the
     *         input string is surrounded by quotes, the quotes are removed and any
     *         escaped quotes within the
     *         string are unescaped before returning the modified string.
     */
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
    /**
     * The `writeEnquiriesToCSV` function writes a list of Enquiry objects to a CSV
     * file, including
     * specific fields and escaping certain values.
     * 
     * @param enquiryList A list of Enquiry objects containing information such as
     *                    Enquiry ID, Enquiry
     *                    Text, Submitted By NRIC, Project ID, Response, and
     *                    Timestamp.
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

    /**
     * The function escapeCSV takes a string value and escapes it for CSV format by
     * adding double
     * quotes and doubling any existing double quotes within the value.
     * 
     * @param value The `escapeCSV` method takes a String value as input and escapes
     *              special characters
     *              for CSV (Comma-Separated Values) format. It checks if the value
     *              contains commas, double quotes,
     *              or newline characters. If any of these special characters are
     *              found, it escapes them by doubling
     *              the double quotes
     * @return The `escapeCSV` method returns the escaped CSV value of the input
     *         string `value`. If the
     *         `value` is null, an empty string is returned. If the `value` contains
     *         a comma (`,`), double
     *         quote (`"`), or newline character (`\n`), it escapes the double
     *         quotes by replacing them with
     *         double double quotes (`""`) and wraps the value in double quotes
     *         before returning
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