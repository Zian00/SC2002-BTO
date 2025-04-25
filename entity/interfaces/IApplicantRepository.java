// This code snippet is defining an interface in Java named `IApplicantRepository` that specifies the
// methods that a class implementing this interface must provide. The interface has two methods:
// 1. `getAllApplicants()`: This method is expected to return a list of `Applicant` objects.
// 2. `saveApplicant(Applicant applicant)`: This method is used to save an `Applicant` object.
package entity.interfaces;

import entity.Applicant;
import java.util.List;

// This code snippet is defining a Java interface named `IApplicantRepository`. This interface
// specifies two methods that any class implementing this interface must provide:
public interface IApplicantRepository {
    List<Applicant> getAllApplicants();
    void saveApplicant(Applicant applicant);
}
