package entity;

import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import entity.interfaces.IApplicantRepository;

public class HDBOfficer extends Applicant {

    public HDBOfficer(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
                      String filterSettings, Role role, IApplicantRepository applicantRepository) {
        super(NRIC, Name, password, age, maritalStatus, filterSettings, role, applicantRepository);
    }
}