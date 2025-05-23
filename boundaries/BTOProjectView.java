package boundaries;

import entity.BTOProject;
import entity.FilterSettings;
import entity.User;
import entity.enumerations.MaritalState;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * View class for BTO project interactions in the BTO system.
 * <p>
 * Provides menu displays, prompts, and output methods for applicants, officers,
 * and managers to view, filter, create, edit, and manage BTO projects.
 * </p>
 */
public class BTOProjectView {

    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * Displays the BTO project menu for applicants.
     */
    public void displayApplicantMenu() {
        System.out.println("\n=== BTO Project Menu ===");
        System.out.println("1. Display All Available BTO Projects");
        System.out.println("2. Display filtered BTO Projects");
        System.out.println("3. Apply for a BTO Project");
        System.out.println("4. Submit Enquiry for a BTO project");
        System.out.println("5. Display My Past BTO Applicaitons");
        System.out.println("6. Back");
        System.out.print("Select an option: ");
    }

    /**
     * Displays the BTO project menu for HDB officers.
     */
    public void displayOfficerMenu() {
        System.out.println("\n=== BTO Project Menu ===");
        System.out.println("1. Display All BTO Projects");
        System.out.println("2. Display filtered BTO Projects");
        System.out.println("3. Apply for a BTO Project");
        System.out.println("4. Submit Enquiry for a BTO project");
        System.out.println("5. Register as HDB Officer of a BTO Projects ");
        System.out.println("6. Display BTO Projects I'm handling");
        System.out.println("7. Back");
        System.out.println("Select an option: ");
    }

    /**
     * Displays the BTO project menu for HDB managers.
     */
    public void displayManagerMenu() {
        System.out.println("\n=== BTO Project Menu ===");
        System.out.println("1. Display All BTO Projects");
        System.out.println("2. Display Filtered All BTO Projects");
        System.out.println("3. Display Filtered My BTO Projects");
        System.out.println("4. Display My BTO Projects");
        System.out.println("5. Add BTO Project");
        System.out.println("6. Edit BTO Project");
        System.out.println("7. Delete BTO Project");
        System.out.println("8. Back");
        System.out.print("Select an option: ");
    }

    /**
     * Displays all projects with divider lines.
     *
     * @param projects the list of BTOProject objects to display
     */
    public void displayAllProject(List<BTOProject> projects) {
        System.out.println("===================================");
        System.out.println("   Available Projects in System    ");
        System.out.println("===================================");
        try {
            if (projects == null || projects.isEmpty()) {
                System.out.println("No available projects in system.");
            } else {
                for (BTOProject project : projects) {
                    if (project == null) {
                        System.out.println("Encountered a null project entry.");
                        continue;
                    }
                    System.out.println(project);
                    System.out.println("-----------------------------------");
                }
            }
        } catch (Exception e) {
            System.out.println("An error occurred while displaying projects: " + e.getMessage());
            System.out.println("===================================");
        }
    }

    /**
     * Displays projects managed by the manager.
     *
     * @param projects the list of BTOProject objects managed by the manager
     */
    public void displayManagerProjects(List<BTOProject> projects) {
        System.out.println("===================================");
        System.out.println("            My Projects           ");
        System.out.println("===================================");
        if (projects == null || projects.isEmpty()) {
            System.out.println("No available projects.");
        } else {
            for (BTOProject project : projects) {
                System.out.println(project);
                System.out.println("-----------------------------------");
            }
        }
        System.out.println("===================================");
    }

    /**
     * Displays only the project ID and name for each project.
     *
     * @param projects the list of BTOProject objects
     */
    public void displayProjectIdNameList(List<BTOProject> projects) {
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects available.");
            return;
        }
        System.out.println("\n=== Projects (choose ID to delete) ===");
        for (BTOProject p : projects) {
            System.out.printf("%d: %s%n", p.getProjectID(), p.getProjectName());
        }
        System.out.println("===============");
    }

    /**
     * Displays available projects for applicant with no filter, based on
     * eligibility.
     *
     * @param user the user viewing the projects
     * @param projects the list of BTOProject objects
     */
    public void displayAvailableForApplicantNoFilter(User user, List<BTOProject> projects) {
        // Determine eligibility based on marital status and age only.
        boolean canSee2 = false, canSee3 = false;
        MaritalState ms = user.getMaritalStatus();
        int age = user.getAge();

        if (ms == MaritalState.SINGLE && age >= 35) {
            canSee2 = true;
        } else if (ms == MaritalState.MARRIED && age >= 21) {
            canSee2 = true;
            canSee3 = true;
        } else {
            System.out.println("You are not eligible to view any projects.");
            return;
        }

        System.out.println("===================================");
        System.out.println("      Available Projects         ");
        System.out.println("===================================");

        if (projects == null || projects.isEmpty()) {
            System.out.println("No available projects.");
        } else {
            for (BTOProject p : projects) {
                // Check visibility: only display projects marked as visible.
                if (!p.isVisibility()) {
                    continue;
                }
                System.out.println("Project ID:   " + p.getProjectID());
                System.out.println("Name:         " + p.getProjectName());
                System.out.println("Neighborhood: " + p.getNeighborhood());

                // Display room details based solely on eligibility.
                if (canSee2) {
                    System.out.printf("2-Room units: %d (Price: $%d)%n",
                            p.getAvailable2Room(), p.getTwoRoomPrice());
                }
                if (canSee3) {
                    System.out.printf("3-Room units: %d (Price: $%d)%n",
                            p.getAvailable3Room(), p.getThreeRoomPrice());
                }
                System.out.println("-----------------------------------");
            }
        }
        System.out.println("===================================");
    }

    /**
     * Displays projects for an Applicant, showing only the room-types they’re
     * eligible to view.
     *
     * @param user the applicant user
     * @param projects the list of BTOProject objects
     */
    public void displayAvailableForApplicant(User user, List<BTOProject> projects) {
        // Determine eligibility
        boolean canSee2 = false, canSee3 = false;
        MaritalState ms = user.getMaritalStatus();
        int age = user.getAge();

        if (ms == MaritalState.SINGLE && age >= 35) {
            canSee2 = true;
        } else if (ms == MaritalState.MARRIED && age >= 21) {
            canSee2 = true;
            canSee3 = true;
        } else {
            System.out.println("You are not eligible.");
            return;
        }

        // Load filter settings from user's CSV
        FilterSettings fs = FilterSettings.fromCsv(user.getFilterSettings());
        String filteredRoom = fs.getRoomType();
        if (filteredRoom != null) {
            filteredRoom = filteredRoom.replace("\"", "").trim();
        }

        System.out.println("===================================");
        System.out.println("      Available Projects         ");
        System.out.println("===================================");

        if (projects == null || projects.isEmpty()) {
            System.out.println("No available projects.");
        } else {
            for (BTOProject p : projects) {
                System.out.println("Project ID:   " + p.getProjectID());
                System.out.println("Name:         " + p.getProjectName());
                System.out.println("Neighborhood: " + p.getNeighborhood());

                // Follow filter: if filter is "2-Room" or "3-Room", display only that details.
                if ("2-Room".equalsIgnoreCase(filteredRoom)) {
                    if (canSee2) {
                        System.out.printf("2-Room units: %d (Price: $%d)%n",
                                p.getAvailable2Room(), p.getTwoRoomPrice());
                    } else {
                        System.out.println("Not eligible for 2‑Room.");
                    }
                } else if ("3-Room".equalsIgnoreCase(filteredRoom)) {
                    if (canSee3) {
                        System.out.printf("3-Room units: %d (Price: $%d)%n",
                                p.getAvailable3Room(), p.getThreeRoomPrice());
                    } else {
                        System.out.println("Not eligible for 3‑Room.");
                    }
                } else { // No filter => display both eligible types
                    if (canSee2) {
                        System.out.printf("2-Room units: %d (Price: $%d)%n",
                                p.getAvailable2Room(), p.getTwoRoomPrice());
                    }
                    if (canSee3) {
                        System.out.printf("3-Room units: %d (Price: $%d)%n",
                                p.getAvailable3Room(), p.getThreeRoomPrice());
                    }
                }
                System.out.println("-----------------------------------");
            }
        }
        System.out.println("===================================");
    }

    public void displayFilteredProject(User user, List<BTOProject> projects) {

        // Load filter settings from user's CSV
        FilterSettings fs = FilterSettings.fromCsv(user.getFilterSettings());
        String filteredRoom = fs.getRoomType();
        if (filteredRoom != null) {
            filteredRoom = filteredRoom.replace("\"", "").trim();
        }

        System.out.println("===================================");
        System.out.println("            All Projects           ");
        System.out.println("===================================");

        if (projects == null || projects.isEmpty()) {
            System.out.println("No available projects.");
        } else {
            for (BTOProject p : projects) {
                System.out.println("Project ID:   " + p.getProjectID());
                System.out.println("Name:         " + p.getProjectName());
                System.out.println("Neighborhood: " + p.getNeighborhood());

                // Follow filter: if filter is "2-Room" or "3-Room", display only that details.
                if ("2-Room".equalsIgnoreCase(filteredRoom)) {
                    System.out.printf("2-Room units: %d (Price: $%d)%n",
                            p.getAvailable2Room(), p.getTwoRoomPrice());
                } else if ("3-Room".equalsIgnoreCase(filteredRoom)) {
                    System.out.printf("3-Room units: %d (Price: $%d)%n",
                            p.getAvailable3Room(), p.getThreeRoomPrice());
                } else { // No filter => display both eligible types
                    System.out.printf("2-Room units: %d (Price: $%d)%n",
                            p.getAvailable2Room(), p.getTwoRoomPrice());
                    System.out.printf("3-Room units: %d (Price: $%d)%n",
                            p.getAvailable3Room(), p.getThreeRoomPrice());
                }
                System.out.println("-----------------------------------");
            }
        }
        System.out.println("===================================");
    }

    /**
     * Prompts the user to enter a project ID.
     *
     * @param sc Scanner for user input
     * @return the entered project ID
     */
    public int promptProjectID(Scanner sc) {
        return promptIntInRange(
                sc,
                "Enter Project ID: ",
                1,
                Integer.MAX_VALUE,
                null // no default, must enter something
        );
    }

    /**
     * Prompts the user to enter a non-empty string.
     *
     * @param sc Scanner for user input
     * @param prompt the prompt message
     * @return the entered non-empty string
     */
    private String promptNonEmpty(Scanner sc, String prompt) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim();
            if (in.contains(",")) {
                System.out.println("Cannot contain commas.");
                continue;
            } else if (!in.isEmpty()) {
                return in;
            }

            System.out.println("Cannot be blank.");
        }
    }

    /**
     * Prompts the user to enter an integer in a specified range.
     *
     * @param sc Scanner for user input
     * @param prompt the prompt message
     * @param min minimum value
     * @param max maximum value
     * @param defaultVal default value if input is blank
     * @return the entered integer
     */
    private int promptIntInRange(Scanner sc, String prompt, int min, int max, Integer defaultVal) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim();
            // if user just presses enter, the doesnt edit at all
            if (in.isEmpty() && defaultVal != null) {
                return defaultVal;
            }
            try {
                int v = Integer.parseInt(in);
                if (v < min || v > max) {
                    System.out.printf("Must be between %d and %d.%n", min, max);
                } else {
                    return v;
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a whole number.");
            }
        }
    }

    /**
     * Prompts the user to enter a date in yyyy-MM-dd format.
     *
     * @param sc Scanner for user input
     * @param prompt the prompt message
     * @param defaultVal default value if input is blank
     * @return the entered LocalDate
     */
    private LocalDate promptDate(Scanner sc, String prompt, LocalDate defaultVal) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim();
            if (in.isEmpty() && defaultVal != null) {
                return defaultVal;
            }
            try {
                return LocalDate.parse(in, DATE_FMT);
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use yyyy-MM-dd.");
            }
        }
    }

    /**
     * Prompts the user to enter a date not before a minimum date.
     *
     * @param sc Scanner for user input
     * @param prompt the prompt message
     * @param minDate the minimum allowed date
     * @return the entered LocalDate
     */
    private LocalDate promptDateNotBefore(Scanner sc, String prompt, LocalDate minDate) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim();
            try {
                LocalDate d = LocalDate.parse(in, DATE_FMT);
                if (d.isBefore(minDate)) {
                    System.out.println("Date must be on or after " + minDate.format(DATE_FMT));
                } else {
                    return d;
                }
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date. Use yyyy-MM-dd.");
            }
        }
    }

    /**
     * Prompts the user to enter a boolean value.
     *
     * @param sc Scanner for user input
     * @param prompt the prompt message
     * @param defaultVal default value if input is blank
     * @return the entered boolean value
     */
    private boolean promptBoolean(Scanner sc, String prompt, Boolean defaultVal) {
        while (true) {
            System.out.print(prompt);
            String in = sc.nextLine().trim().toLowerCase();
            if (in.isEmpty() && defaultVal != null) {
                return defaultVal;
            }
            if (in.equals("true") || in.equals("false")) {
                return Boolean.parseBoolean(in);
            }
            System.out.println("Enter 'true' or 'false'.");
        }
    }

    /**
     * Prompts the user to create a new BTO project.
     *
     * @param sc Scanner for user input
     * @return the created BTOProject object
     */
    public BTOProject promptNewProject(Scanner sc) {
        BTOProject p = new BTOProject();

        p.setProjectName(
                promptNonEmpty(sc, "Name: "));
        p.setNeighborhood(
                promptNonEmpty(sc, "Neighborhood: "));
        p.setAvailable2Room(
                promptIntInRange(sc, "2-Room units: ", 0, Integer.MAX_VALUE, null));
        p.setTwoRoomPrice(
                promptIntInRange(sc, "2-Room price: ", 0, Integer.MAX_VALUE, null));
        p.setAvailable3Room(
                promptIntInRange(sc, "3-Room units: ", 0, Integer.MAX_VALUE, null));
        p.setThreeRoomPrice(
                promptIntInRange(sc, "3-Room price: ", 0, Integer.MAX_VALUE, null));
        // Ask for the opening date (must not be blank)
        LocalDate open = promptDate(sc, "Application opening date (yyyy-MM-dd): ", null);
        p.setApplicationOpeningDate(open.format(DATE_FMT));

        // Use promptDateNotBefore to enforce:
        // 1. Input is not blank
        // 2. The date is on or after the opening date
        LocalDate close = promptDateNotBefore(sc,
                String.format("Application closing date (yyyy-MM-dd) [>= %s]: ", open.format(DATE_FMT)),
                open);
        p.setApplicationClosingDate(close.format(DATE_FMT));

        p.setAvailableOfficerSlots(
                promptIntInRange(sc, "Officer slots (0-10): ", 0, 10, null));
        p.setVisibility(
                promptBoolean(sc, "Visible? (true/false): ", null));

        // Safe defaults
        p.setPendingOfficer(new ArrayList<>());
        p.setApprovedOfficer(new ArrayList<>());

        return p;
    }

    /**
     * Prompts the user to edit details of an existing BTO project.
     *
     * @param sc Scanner for user input
     * @param p the BTOProject to edit
     */
    public void editProjectDetails(Scanner sc, BTOProject p) {
        System.out.println("Editing project " + p.getProjectID() + ". Leave blank to keep current.");

        // For name prompt input, if it's blank, keep the current name.
        System.out.print(String.format("Name (%s): ", p.getProjectName()));
        String nameInput = sc.nextLine().trim();
        if (!nameInput.isEmpty()) {
            p.setProjectName(nameInput);
        }

        // same for neighborhood
        System.out.print(String.format("Neighborhood (%s): ", p.getNeighborhood()));
        String nbInput = sc.nextLine().trim();
        if (!nbInput.isEmpty()) {
            p.setNeighborhood(nbInput);
        }

        p.setAvailable2Room(
                promptIntInRange(sc,
                        String.format("2-Room units (%d): ", p.getAvailable2Room()),
                        0, Integer.MAX_VALUE,
                        p.getAvailable2Room()));
        p.setTwoRoomPrice(
                promptIntInRange(sc,
                        String.format("2-Room price (%d): ", p.getTwoRoomPrice()),
                        0, Integer.MAX_VALUE,
                        p.getTwoRoomPrice()));
        p.setAvailable3Room(
                promptIntInRange(sc,
                        String.format("3-Room units (%d): ", p.getAvailable3Room()),
                        0, Integer.MAX_VALUE,
                        p.getAvailable3Room()));
        p.setThreeRoomPrice(
                promptIntInRange(sc,
                        String.format("3-Room price (%d): ", p.getThreeRoomPrice()),
                        0, Integer.MAX_VALUE,
                        p.getThreeRoomPrice()));

        // parse the existing dates once
        LocalDate openDefault = LocalDate.parse(p.getApplicationOpeningDate(), DATE_FMT);
        // LocalDate closeDefault = LocalDate.parse(p.getApplicationClosingDate(),
        // DATE_FMT);

        // Prompt for a new opening date, or blank to keep the old one
        LocalDate open = promptDate(
                sc,
                String.format("Application opening date (%s): ", p.getApplicationOpeningDate()),
                openDefault);
        p.setApplicationOpeningDate(open.format(DATE_FMT));

        // Prompt for a new closing date, but enforce >= opening date
        // now ask for closing date, must be on or after the opening date
        LocalDate close = promptDateNotBefore(
                sc,
                String.format("Application closing date (%s): ", p.getApplicationClosingDate()),
                open);
        p.setApplicationClosingDate(close.format(DATE_FMT));

        p.setAvailableOfficerSlots(
                promptIntInRange(sc,
                        String.format("Officer slots (%d) [0-10]: ", p.getAvailableOfficerSlots()),
                        0, 10,
                        p.getAvailableOfficerSlots()));

        p.setVisibility(
                promptBoolean(sc,
                        String.format("Visible? (%b): ", p.isVisibility()),
                        p.isVisibility()));
    }

    /**
     * Displays a message to the user.
     *
     * @param msg the message to display
     */
    public void showMessage(String msg) {
        System.out.println(msg);
    }

    /**
     * Displays projects handled by the officer.
     *
     * @param projects the list of BTOProject objects handled by the officer
     */
    public void displayHandledProjects(List<BTOProject> projects) {
        if (projects.isEmpty()) {
            System.out.println("You have no approved officer assignments.");
            return;
        }

        System.out.println("=== Projects You're Handling ===");
        for (BTOProject p : projects) {
            System.out.println(p); // relies on your detailed toString()
            System.out.println("-----------------------------------");
        }
    }

    /**
     * Prompts the user to enter filter settings for projects.
     *
     * @param user the user setting the filters
     * @param sc Scanner for user input
     * @return the entered FilterSettings
     */
    public FilterSettings promptFilterSettings(User user, Scanner sc) {
        // 1) Parse the existing CSV into a FilterSettings object
        FilterSettings existing = FilterSettings.fromCsv(user.getFilterSettings());

        // Remove any quotation marks from the room type before display
        String displayedRoomType = existing.getRoomType();
        if (displayedRoomType != null) {
            displayedRoomType = displayedRoomType.replace("\"", "");
        }
        // 2) Print _all_ three fields of the existing filter:
        System.out.println("Existing filters:");
        System.out.println(" Room type: "
                + (displayedRoomType == null || displayedRoomType.isEmpty() ? "any" : displayedRoomType));
        System.out.println("  Minimum price: $"
                + (existing.getMinPrice() == null ? "none" : existing.getMinPrice()));
        System.out.println("  Maximum price: $"
                + (existing.getMaxPrice() == null ? "none" : existing.getMaxPrice()));
        System.out.println();

        System.out.print("Apply new filters? (y/N): ");
        String answer = sc.nextLine().trim();
        while (!answer.equalsIgnoreCase("y")
                && !answer.equalsIgnoreCase("n")
                && !answer.isEmpty()) { // consider blank as "n"
            System.out.println("Invalid input. Please enter 'y' for yes or 'n' for no.");
            System.out.print("Apply new filters? (y/N): ");
            answer = sc.nextLine().trim();
        }
        if (!answer.equalsIgnoreCase("y")) {
            return existing; // keep old filters
        }

        // 2) choose room
        String roomType = null;
        OUTER:
        while (true) {
            System.out.print("Room type? 1)2-Room  2)3-Room  0)any: ");
            String in = sc.nextLine().trim();
            if (null == in) {
                System.out.println("Choose 1,2 or 0.");
            } else {
                switch (in) {
                    case "1" -> {
                        roomType = "2-Room";
                        break OUTER;
                    }
                    case "2" -> {
                        roomType = "3-Room";
                        break OUTER;
                    }
                    case "0" -> {
                        break OUTER;
                    }
                    default ->
                        System.out.println("Choose 1,2 or 0.");
                }
            }
        }

        // 3) min price
        Integer minP = null;
        while (true) {
            System.out.print("Minimum price (blank=none): ");
            String in = sc.nextLine().trim();
            if (in.isEmpty()) {
                break;
            }
            try {
                int v = Integer.parseInt(in);
                if (v < 0) {
                    System.out.println("Cannot be negative."); 
                }else {
                    minP = v;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Whole number only.");
            }
        }

        // 4) max price
        Integer maxP = null;
        while (true) {
            System.out.print("Maximum price (blank=none): ");
            String in = sc.nextLine().trim();
            if (in.isEmpty()) {
                break;
            }
            try {
                int v = Integer.parseInt(in);
                if (v < 0) {
                    System.out.println("Cannot be negative."); 
                }else if (minP != null && v < minP) {
                    System.out.println("Must be ≥ minimum price."); 
                }else {
                    maxP = v;
                    break;
                }
            } catch (NumberFormatException e) {
                System.out.println("Whole number only.");
            }
        }

        return new FilterSettings(roomType, minP, maxP);
    }

    /**
     * Displays eligible BTO projects for officer application, based on marital
     * status and age.
     *
     * @param projects the list of eligible BTOProject objects
     * @param ms the marital status of the officer
     * @param age the age of the officer
     */
    public void displayEligibleProjectsForOfficer(List<BTOProject> projects, MaritalState ms, int age) {
        System.out.println("=== Eligible BTO Projects for Application ===");
        if (projects == null || projects.isEmpty()) {
            System.out.println("No eligible projects available.");
            return;
        }

        boolean canSee2 = false, canSee3 = false;
        if (ms == MaritalState.SINGLE && age >= 35) {
            canSee2 = true;
        } else if (ms == MaritalState.MARRIED && age >= 21) {
            canSee2 = true;
            canSee3 = true;
        } else {
            System.out.println("You are not eligible to view any projects.");
            return;
        }

        for (BTOProject p : projects) {
            System.out.println("Project ID:   " + p.getProjectID());
            System.out.println("Name:         " + p.getProjectName());
            System.out.println("Neighborhood: " + p.getNeighborhood());

            if (canSee2) {
                System.out.printf("2-Room units: %d (Price: $%d)%n",
                        p.getAvailable2Room(), p.getTwoRoomPrice());
            }
            if (canSee3) {
                System.out.printf("3-Room units: %d (Price: $%d)%n",
                        p.getAvailable3Room(), p.getThreeRoomPrice());
            }
            System.out.println("-----------------------------------");
        }
    }
}
