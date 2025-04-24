package entity.repositories;

import entity.Applicant;
import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import entity.interfaces.IApplicantRepository;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ApplicantCSVRepository implements IApplicantRepository {

    private static final String CSV_FILE = "assets/userList.csv";

    @Override
    public void saveApplicant(Applicant applicant) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(CSV_FILE, true))) {
            pw.printf("%s,%s,%d,%s,%s,%s,%s%n",
                    applicant.getName(),
                    applicant.getNRIC(),
                    applicant.getAge(),
                    applicant.getMaritalStatus(),
                    applicant.getPassword(),
                    applicant.getRole(),
                    applicant.getFilterSettings());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Applicant> getAllApplicants() {
        List<Applicant> applicants = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE))) {
            String line;
            br.readLine(); // Skip header line if present
            while ((line = br.readLine()) != null) {
                String[] tokens = line.split(",", -1);
                if (tokens.length < 7) continue; // Skip invalid rows
                String name = tokens[0].trim();
                String nric = tokens[1].trim();
                int age = Integer.parseInt(tokens[2].trim());
                MaritalState maritalStatus = MaritalState.valueOf(tokens[3].trim().toUpperCase());
                String password = tokens[4].trim();
                Role role = Role.valueOf(tokens[5].trim().toUpperCase());
                String filterSettings = tokens[6].trim();
                applicants.add(new Applicant(nric, name, password, age, maritalStatus, filterSettings, role, this));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return applicants;
    }
}
