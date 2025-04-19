import boundaries.UserView;
import controllers.UserCTRL;
import java.util.Scanner;

public class Main {

    // This `main` method is the entry point of the Java program. Here's a breakdown
    // of what it does:
    public static void main(String[] args) {
        try (Scanner sc = new Scanner(System.in)) {
            UserCTRL userCTRL = new UserCTRL();
            UserView userView = new UserView(); // Create a UserView instance
            userCTRL.loadUserData();

            OUTER: while (true) {
                // =====================================
                // Main Menu Options
                // =====================================
                System.out.println("\n=== HDB Hub ===");
                System.out.println("1. Login");
                System.out.println("2. Exit");
                System.out.print("Select an option: ");
                String choice = sc.nextLine().trim();
                switch (choice) {
                    case "1" -> {
                        if (userView.loginFlow(sc, userCTRL)) {
                            // 2) Logged‑in Main menu
                            userCTRL.runCentralMenu(sc, userCTRL);
                        }
                    }
                    case "2" -> {
                        System.out.println("Exiting... Goodbye!");
                        break OUTER;
                    }
                    default -> System.out.println("Invalid choice, please try again.");
                }
            }
        }
    }
}
