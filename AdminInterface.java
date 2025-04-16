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
            System.out.println("\n=== Admin Control Panel ===");
            System.out.println("1. Update location safety parameters");
            System.out.println("2. View all routes");
            System.out.println("3. Test route finding");
            System.out.println("4. View safety database");
            System.out.println("5. Exit to main menu");
            
            System.out.print("\nChoose an option (1-5): ");
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
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void testRouteFinding() {
        graph.displayAvailablePaths();
        System.out.println("Enter start location (Home / Office / Market / Mall / Restroom / Park / University):");
        String startInput = scanner.next();
        Location start = LocationService.geocode(startInput);

        System.out.println("Enter destination location (Home / Office / Market / Mall / Restroom / Park / University):");
        String endInput = scanner.next();
        Location end = LocationService.geocode(endInput);

        // Find and display safest path
        List<Location> path = graph.findSafestPath(start, end);
        graph.displaySafestPathWithWeights(path);
    }
    
    private void viewSafetyDatabase() {
        System.out.println("\n=== Safety Database ===");
        for (String location : SafetyDatabase.safetyData.keySet()) {
            System.out.println("\nLocation: " + location);
            Map<String, Double> attributes = SafetyDatabase.safetyData.get(location);
            for (String attr : attributes.keySet()) {
                System.out.printf("- %s: %.2f\n", attr, attributes.get(attr));
            }
            System.out.printf("Overall Safety Rating: %.2f\n", SafetyDatabase.calculateSafetyRating(location));
        }
    }
}
