import java.util.Scanner;

public class FeedbackInterface {
    private Graph graph;
    private FeedbackSystem feedbackSystem;
    private Scanner scanner;
    private LoginSystem loginSystem;

    public FeedbackInterface(Graph graph, FeedbackSystem feedbackSystem, Scanner scanner, LoginSystem loginSystem) {
        this.graph = graph;
        this.feedbackSystem = feedbackSystem;
        this.scanner = scanner;
        this.loginSystem = loginSystem;
    }

    public void showFeedbackMenu() {
        boolean exit = false;

        while (!exit) {
            System.out.println("\n=== Route Feedback System === \n");
            System.out.println("1. Login to provide feedback >>");
            System.out.println("2. View available routes >>");
            System.out.println("3. Exit to main menu >>");

            System.out.print("\nChoose an option (1-3): ");
            String choice = scanner.next();

            switch (choice) {
                case "1":
                    loginAndProvideFeedback();
                    break;
                case "2":
                    graph.displayAvailablePaths();
                    break;
                case "3":
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid option! :( Please try again!!");
            }
        }
    }

    private void loginAndProvideFeedback() {
        System.out.println("\n=== Login to provide feedback ===");
        System.out.print("Username: ");
        String username = scanner.next();

        System.out.print("Password: ");
        String password = scanner.next();

        User user = loginSystem.login(username, password);

        if (user != null) {
            provideFeedback();
        } else {
            System.out.println("Login failed. You must be a registered user to provide feedback.");
        }
    }

    private void provideFeedback() {
        System.out.println("\n Logged in Succesfully!! :) \n=== Location Feedback System ===");
        System.out.println("Enter the location you'd like to give feedback on:");
        String feedbackLocName = scanner.next();
        Location feedbackLoc = LocationService.geocode(feedbackLocName);

        feedbackSystem.applyFeedbackToLocation(scanner, feedbackLoc, graph);
        System.out.println("Thank you for your feedback! :)");
    }
}
