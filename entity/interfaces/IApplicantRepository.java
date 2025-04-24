package entity.interfaces;

import entity.Applicant;
import java.util.List;

public interface IApplicantRepository {
    List<Applicant> getAllApplicants();
    void saveApplicant(Applicant applicant);
}
