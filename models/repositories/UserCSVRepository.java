package models.repositories;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import models.User;
import models.enumerations.MaritalState;
import models.enumerations.Role;

public class UserCSVRepository {

    private static final String CSV_FILE = "assets/userList.csv";

    public List<User> readUserFromCSV() {
        List<User> users = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            // Read and skip header line
            if ((line = br.readLine()) != null) {
                // header read; do nothing
            }
            while ((line = br.readLine()) != null) {
                if (line.trim().isEmpty()) continue;
                String[] tokens = line.split(",");
                if (tokens.length < 7)
                    continue; // or handle error
                String name = tokens[0].trim();
                String nric = tokens[1].trim();
                int age = Integer.parseInt(tokens[2].trim());
                // Adjust the case/format as needed:
                MaritalState maritalStatus = MaritalState.valueOf(tokens[3].trim().toUpperCase());
                String password = tokens[4].trim();
                Role role = Role.valueOf(tokens[5].trim().toUpperCase());
                String filterSettings = tokens[6].trim();

                // Assuming you have a constructor in User with these parameters
                User user = new User(nric, name, password, age, maritalStatus, filterSettings, role);
                users.add(user);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return users;
    }

    public void writeUserToCSV(List<User> users) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE))) {
            // Write header
            pw.println("Name,NRIC,Age,Marital Status,Password,role,filterSettings");
            for (User user : users) {
                String line = String.join(",",
                        user.getName(),
                        user.getNRIC(),
                        String.valueOf(user.getAge()),
                        user.getMaritalStatus().toString(),
                        user.getPassword(),
                        user.getRole().toString(),
                        user.getFilterSettings() == null ? "" : user.getFilterSettings());
                pw.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}