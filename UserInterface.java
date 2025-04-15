import java.util.List;
import java.util.Scanner;

public class UserInterface {
    private Graph graph;
    private Scanner scanner;
    private LoginSystem loginSystem;
    
    public UserInterface(Graph graph, Scanner scanner, LoginSystem loginSystem) {
        this.graph = graph;
        this.scanner = scanner;
        this.loginSystem = loginSystem;
    }
    
    public void showUserMenu() {
        boolean exit = false;
        
        while (!exit) {
            System.out.println("\n=== Safe Route Finder - User Menu ===");
            System.out.println("1. Register new account");
            System.out.println("2. Find safest path between locations");
            System.out.println("3. View available routes");
            System.out.println("4. Exit to main menu");
            
            System.out.print("\nChoose an option (1-4): ");
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    loginSystem.registerNewUser(scanner);
                    break;
                case "2":
                    findSafestPath();
                    break;
                case "3":
                    graph.displayAvailablePaths();
                    break;
                case "4":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }
    
    private void findSafestPath() {
        graph.displayAvailablePaths();
        System.out.println("Enter start location (Home / Office / Market / Mall / Restroom / Park / University):");
        String startInput = scanner.nextLine();
        Location start = LocationService.geocode(startInput);

        System.out.println("Enter destination location (Home / Office / Market / Mall / Restroom / Park / University):");
        String endInput = scanner.nextLine();
        Location end = LocationService.geocode(endInput);

        // Find and display safest path
        List<Location> path = graph.findSafestPath(start, end);
        graph.displaySafestPathWithWeights(path);
        
        System.out.println("\nTo provide feedback on this route, please use the Feedback option from the main menu.");
    }
}