import java.util.*;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static Graph graph = initializeGraph();
    public static void main(String[] args) {
        
        
        // Initialize the graph
        
        
        // Initialize safety database
        SafetyDatabase.initializeDatabase();
        
        // Initialize weights for feedback system
        Map<String, Double> weights = new HashMap<>();
        weights.put("streetlights", 0.4);
        weights.put("crowd", 0.3);
        weights.put("cctv", 0.3);
        
        FeedbackSystem feedbackSystem = new FeedbackSystem(weights);
        
        // Initialize LoginSystem
        LoginSystem loginSystem = new LoginSystem();
        
        // Create interfaces
        AdminInterface adminInterface = new AdminInterface(graph, feedbackSystem, scanner);
        UserInterface userInterface = new UserInterface(graph, scanner, loginSystem);
        FeedbackInterface feedbackInterface = new FeedbackInterface(graph, feedbackSystem, scanner, loginSystem);
        
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n=== Safe Route Finder System ===");
            System.out.println("1. Admin Access");
            System.out.println("2. User Interface");
            System.out.println("3. Feedback System");
            System.out.println("4. Find safest nearest spot");
            System.out.println("5. Exit");
            
            System.out.print("\nChoose an option (1-5): ");
            String choice = scanner.next();
            
            switch (choice) {
                case "1":
                    handleAdminLogin(scanner, loginSystem, adminInterface);
                    break;
                case "2":
                    userInterface.showUserMenu();
                    break;
                case "3":
                    feedbackInterface.showFeedbackMenu();
                    break;
                case "4":
                    findSafestNearestSpot();
                    break;
                case "5":
                    exit = true;
                    System.out.println("Thank you for using Safe Route Finder. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        scanner.close();
    }

    private static void findSafestNearestSpot() {
        System.out.println("\nEnter your current location name (e.g., Home, Office): ");
        String currentLocationName = scanner.next();
        Location currentLocation = new Location(currentLocationName);
        
        // Find the safest nearest spot using the A* algorithm
        Location safestLocation = graph.findSafestNearestSpot(currentLocation);
        if (safestLocation != null) {
            // Calculate the distance to the safest location
            double distance = calculateDistance(currentLocation, safestLocation);
            System.out.println("The safest nearest spot is: " + safestLocation);
            System.out.println("Distance to the safest spot: " + distance + "km");
        } else {
            System.out.println("No nearest location found.");
        }
    }
    
    // Helper method to calculate distance between two locations
    private static double calculateDistance(Location start, Location end) {
        double totalDistance = 0;
        // Iterate over the edges of the start location to find the distance
        for (Edge edge : graph.adjList.getOrDefault(start, new ArrayList<>())) {
            if (edge.to.equals(end)) {
                totalDistance += edge.distance; // Add the distance to the end location
            }
        }
        return totalDistance;
    }
    
    private static void handleAdminLogin(Scanner scanner, LoginSystem loginSystem, AdminInterface adminInterface) {
        System.out.println("\n=== Admin Login ===");
        System.out.print("Username: ");
        String username = scanner.next();
        
        System.out.print("Password: ");
        String password = scanner.next();
        
        User user = loginSystem.login(username, password);
        
        if (user != null && user.isAdmin()) {
            System.out.println("Welcome, Administrator!");
            adminInterface.showAdminMenu();
        } else {
            System.out.println("Access denied. Admin privileges required.");
        }
    }
    
    private static Graph initializeGraph() {
        Graph graph = new Graph();

        // Define known locations
        Location home = LocationService.geocode("Home");
        Location office = LocationService.geocode("Office");
        Location market = LocationService.geocode("Market");
        Location mall = LocationService.geocode("Mall");
        Location restroom = LocationService.geocode("Restroom");
        Location park = LocationService.geocode("Park");
        Location uni = LocationService.geocode("University");

        // Add locations to the graph
        graph.addLocation(home);
        graph.addLocation(office);
        graph.addLocation(market);
        graph.addLocation(mall);
        graph.addLocation(restroom);
        graph.addLocation(park);
        graph.addLocation(uni);

        // Calculate average safety rating for edges
        double homeRating = SafetyDatabase.calculateSafetyRating("Home");
        double officeRating = SafetyDatabase.calculateSafetyRating("Office");
        double marketRating = SafetyDatabase.calculateSafetyRating("Market");
        double mallRating = SafetyDatabase.calculateSafetyRating("Mall");
        double restroomRating = SafetyDatabase.calculateSafetyRating("Restroom");
        double parkRating = SafetyDatabase.calculateSafetyRating("Park");
        double uniRating = SafetyDatabase.calculateSafetyRating("University");

        // Bidirectional graph edges using average safety ratings
        graph.addEdge(home, office, 0.75,15);
        graph.addEdge(office, home, 0.75,15);

        graph.addEdge(home, market, 0.75,8);
        graph.addEdge(market, home, 0.75,8);

        graph.addEdge(home, mall, 0.7,10);
        graph.addEdge(mall, home, 0.7,10);

        graph.addEdge(mall, restroom, 0.35,2);
        graph.addEdge(restroom, mall, 0.35,2);

        graph.addEdge(mall, park, 0.45,5);
        graph.addEdge(park, mall, 0.45,5);

        graph.addEdge(park, uni, 0.6,10);
        graph.addEdge(uni, park, 0.6,10);

        graph.addEdge(market, restroom, 0.4,1);
        graph.addEdge(restroom, market, 0.4,1);

        graph.addEdge(office, uni, 0.7,7);
        graph.addEdge(uni, office, 0.7,7);

        graph.addEdge(market, mall, 0.55,4);
        graph.addEdge(mall, market, 0.55,4);
        
        return graph;
    }
}
