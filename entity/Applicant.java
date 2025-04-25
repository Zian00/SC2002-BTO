package entity;

import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import entity.interfaces.IApplicantRepository;

public class Applicant extends User {

    private int applicationID;
    private final IApplicantRepository applicantRepository;

    public Applicant(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
                     String filterSettings, Role role, IApplicantRepository applicantRepository) {
        super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
        this.applicantRepository = applicantRepository;
    }

    public int getApplicationID() {
        return this.applicationID;
    }

    public void setApplicationID(int applicationID) {
        this.applicationID = applicationID;
    }

    /**
     * The `save()` function saves the current applicant object using the applicant repository.
     */
    public void save() {
        applicantRepository.saveApplicant(this);
    }
}