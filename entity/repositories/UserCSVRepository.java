package entity.repositories;

import entity.User;
import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class UserCSVRepository {

    private static final String CSV_FILE = "assets/userList.csv";

    /**
     * The function reads user data from a CSV file, parses the data, and creates
     * User objects based on
     * the information read.
     * 
     * @return The method `readUserFromCSV` returns a `List<User>` containing user
     *         objects read from a
     *         CSV file.
     */
    public List<User> readUserFromCSV() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Skip the header:
            if ((line = br.readLine()) != null) {
                // header line read
            }
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty())
                    continue;
                String[] tokens = line.split(",", 7); // im trying sth
                // String[] tokens = line.split(",", -1); // Filter Settings is default as blank
                // so -1 to prevent field getting deleted
                // Ensure tokens length is correct
                if (tokens.length < 7) {
                    System.out.println("Skipping invalid record: " + line);
                    continue;
                }
                // Parse tokens (make sure enum values match your CSV data)
                String name = tokens[0].trim();
                String nric = tokens[1].trim();
                int age = Integer.parseInt(tokens[2].trim());
                // Assume marital status in CSV is uppercase or adjust accordingly:
                MaritalState maritalStatus = MaritalState.valueOf(tokens[3].trim().toUpperCase());
                String password = tokens[4].trim();
                Role role = Role.valueOf(tokens[5].trim().toUpperCase());
                String filterSettings = tokens[6].trim();
                if (filterSettings.startsWith("\"") && filterSettings.endsWith("\"") && filterSettings.length() >= 2) {
                    filterSettings = filterSettings.substring(1, filterSettings.length() - 1);
                }
                User user = new User(nric, name, password, age, maritalStatus, filterSettings, role);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // System.out.println("Loaded " + users.size() + " user(s).");
        return users;
    }

    /**
     * The `writeUserToCSV` function writes a list of User objects to a CSV file,
     * handling special
     * cases like commas in the filter settings.
     * 
     * @param users A list of User objects containing information such as name,
     *              NRIC, age, marital
     *              status, password, role, and filter settings.
     */
    public void writeUserToCSV(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            // 1) Header
            pw.println("Name,NRIC,Age,Marital Status,Password,role,filterSettings");
            // 2) Each user
            for (User user : users) {
                String fs = user.getFilterSettings();
                if (fs == null) {
                    fs = "";
                } else if (fs.contains(",")) {
                    // wrap in quotes so your commas stay in one cell
                    fs = "\"" + fs + "\"";
                }
                pw.printf("%s,%s,%d,%s,%s,%s,%s%n",
                        user.getName(),
                        user.getNRIC(),
                        user.getAge(),
                        user.getMaritalStatus(),
                        user.getPassword(),
                        user.getRole(),
                        fs // <- use the local filtersettings
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}