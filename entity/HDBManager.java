package entity;

import entity.enumerations.MaritalState;
import entity.enumerations.Role;
import java.util.List;

public class HDBManager extends User {

        public HDBManager(String NRIC, String Name, String password, int age, MaritalState maritalStatus,
                        String filterSettings, Role role) {
                super(NRIC, Name, password, age, maritalStatus, filterSettings, role);
        }

        /**
         * The function `getProjectById` takes a list of BTOProject objects and an ID, and returns the
         * project with the matching ID or null if not found.
         * 
         * @param projects The `projects` parameter is a list of `BTOProject` objects.
         * @param id The `id` parameter is an integer value representing the unique identifier of the
         * project that you want to retrieve from the list of `BTOProject` objects.
         * @return The method `getProjectById` returns a `BTOProject` object from the given list of
         * projects based on the provided project ID. If the project with the specified ID is found in
         * the list, it is returned; otherwise, `null` is returned.
         */
        public BTOProject getProjectById(List<BTOProject> projects, int id) {
                if (projects == null)
                        return null;
                return projects.stream()
                                .filter(p -> p.getProjectID() == id)
                                .findFirst()
                                .orElse(null);
        }

}