import java.util.*;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        // Initialize the graph
        Graph graph = initializeGraph();
        
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
            System.out.println("4. Exit");
            
            System.out.print("\nChoose an option (1-4): ");
            String choice = scanner.nextLine();
            
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
                    exit = true;
                    System.out.println("Thank you for using Safe Route Finder. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        
        scanner.close();
    }
    
    private static void handleAdminLogin(Scanner scanner, LoginSystem loginSystem, AdminInterface adminInterface) {
        System.out.println("\n=== Admin Login ===");
        System.out.print("Username: ");
        String username = scanner.nextLine();
        
        System.out.print("Password: ");
        String password = scanner.nextLine();
        
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
        graph.addEdge(home, office, (homeRating + officeRating) / 2);
        graph.addEdge(office, home, (homeRating + officeRating) / 2);

        graph.addEdge(home, market, (homeRating + marketRating) / 2);
        graph.addEdge(market, home, (homeRating + marketRating) / 2);

        graph.addEdge(home, mall, (homeRating + mallRating) / 2);
        graph.addEdge(mall, home, (homeRating + mallRating) / 2);

        graph.addEdge(mall, restroom, (mallRating + restroomRating) / 2);
        graph.addEdge(restroom, mall, (mallRating + restroomRating) / 2);

        graph.addEdge(mall, park, (mallRating + parkRating) / 2);
        graph.addEdge(park, mall, (mallRating + parkRating) / 2);

        graph.addEdge(park, uni, (parkRating + uniRating) / 2);
        graph.addEdge(uni, park, (parkRating + uniRating) / 2);

        graph.addEdge(market, restroom, (marketRating + restroomRating) / 2);
        graph.addEdge(restroom, market, (marketRating + restroomRating) / 2);

        graph.addEdge(office, uni, (officeRating + uniRating) / 2);
        graph.addEdge(uni, office, (officeRating + uniRating) / 2);

        graph.addEdge(market, mall, (marketRating + mallRating) / 2);
        graph.addEdge(mall, market, (marketRating + mallRating) / 2);
        
        return graph;
    }
}