import java.util.*;

public class AdminInterface {
    private Graph graph;
    private FeedbackSystem feedbackSystem;
    private Scanner scanner;

    public AdminInterface(Graph graph, FeedbackSystem feedbackSystem, Scanner scanner) {
        this.graph = graph;
        this.feedbackSystem = feedbackSystem;
        this.scanner = scanner;
    }

    public void showAdminMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Admin Control Panel === \n");
            System.out.println("1. Update location safety parameters >>");
            System.out.println("2. View all routes >>");
            System.out.println("3. Test route finding >>");
            System.out.println("4. View safety database >>");
            System.out.println("5. Exit to main menu >>");

            System.out.print("\nChoose an option (1-5):  >>");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    feedbackSystem.adminUpdateLocationSafety(scanner, graph);
                    break;
                case "2":
                    graph.displayAvailablePaths();
                    break;
                case "3":
                    testRouteFinding();
                    break;
                case "4":
                    viewSafetyDatabase();
                    break;
                case "5":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option! :( Please try again!!");
            }
        }
    }

    private void testRouteFinding() {
        graph.displayAvailablePaths();
        System.out.println("Enter start location (Home / Office / Market / Mall / Restroom / Park / University):");
        String startInput = scanner.next();
        Location start = LocationService.geocode(startInput);

        System.out
                .println("Enter destination location (Home / Office / Market / Mall / Restroom / Park / University):");
        String endInput = scanner.next();
        Location end = LocationService.geocode(endInput);

        // Find and display safest path
        List<Location> path = graph.findSafestPath(start, end);
        graph.displaySafestPathWithWeights(path);
    }

    private void viewSafetyDatabase() {
        System.out.println("\n=== Safety Database ===");

        // Header with vertical lines
        String format = "| %-12s | %-10s | %-10s | %-10s | %-15s |\n";
        String line = "+--------------+------------+------------+------------+-----------------+";

        System.out.println(line);
        System.out.printf(format, "Location", "Lighting", "Crowd", "CCTV", "Overall Safety");
        System.out.println(line);

        for (String location : SafetyDatabase.safetyData.keySet()) {
            Map<String, Double> attributes = SafetyDatabase.safetyData.get(location);

            double lighting = attributes.getOrDefault("streetlights", 0.0);
            double crowd = attributes.getOrDefault("crowd", 0.0);
            double cctv = attributes.getOrDefault("cctv", 0.0);
            double overall = SafetyDatabase.calculateSafetyRating(location);

            System.out.printf(format, location,
                    String.format("%.2f", lighting),
                    String.format("%.2f", crowd),
                    String.format("%.2f", cctv),
                    String.format("%.2f", overall));
        }

        System.out.println(line);
    }

}
