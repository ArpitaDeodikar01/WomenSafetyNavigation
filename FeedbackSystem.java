import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FeedbackSystem {
    private final Map<String, Double> weights;
    private static final double FEEDBACK_INCREMENT = 0.1;
    private static final double MIN_SAFETY_VALUE = 0.1;
    private static final double MAX_SAFETY_VALUE = 1.0;

    public FeedbackSystem(Map<String, Double> initialWeights) {
        this.weights = new HashMap<>(initialWeights);
    }

    public void updateWeightsInteractive(Scanner scanner) {
        System.out.println("\n=== Update Safety Weights === \n");
        System.out.println("Current weights:");
        weights.forEach((k, v) -> System.out.printf("- %s: %.2f%n", k, v));

        System.out.println("\nEnter parameters to adjust (comma separated, e.g., streetlights,crowd):");
        String[] selectedParams = scanner.nextLine().toLowerCase().split(",");

        for (String param : selectedParams) {
            param = param.trim();
            if (!weights.containsKey(param)) {
                System.out.println("Skipping invalid parameter: " + param);
                continue;
            }

            System.out.printf("Adjust %s (current: %.2f). Enter change [+/- value]: ", param, weights.get(param));
            try {
                String input = scanner.nextLine();
                double change = input.startsWith("-") ? -Double.parseDouble(input.substring(1))
                        : Double.parseDouble(input);

                double newValue = clampValue(weights.get(param) + change);
                weights.put(param, newValue);
                System.out.printf("Updated %s weight to %.2f%n", param, newValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input :( No changes made to " + param);
            }
        }
    }

    public void applyFeedbackToLocation(Scanner scanner, Location location, Graph graph) {
        if (location == null || graph == null) {
            System.out.println("Invalid location or graph reference :(");
            return;
        }

        String locName = location.name;
        Map<String, Double> currentAttributes = SafetyDatabase.getLocationAttributes(locName);

        if (currentAttributes == null || currentAttributes.isEmpty()) {
            System.out.println("No safety data found for " + locName);
            return;
        }

        System.out.println("\n=== Provide Feedback for " + locName + " ===");
        Map<String, Double> updatedAttributes = new HashMap<>();

        for (String param : currentAttributes.keySet()) {
            if (param.equals("weightedScore"))
                continue; // Skip calculated field

            double currentValue = currentAttributes.get(param);
            System.out.printf("Current %s rating: %.2f%n", param, currentValue);

            String feedback = getValidFeedback(scanner, param);
            double change = feedback.equals("safe") ? FEEDBACK_INCREMENT : -FEEDBACK_INCREMENT;
            double newValue = clampValue(currentValue + change);

            updatedAttributes.put(param, newValue);
            System.out.printf("Updated %s from %.2f to %.2f%n", param, currentValue, newValue);
        }

        updateLocationSafety(locName, updatedAttributes, graph);
    }

    public void adminUpdateLocationSafety(Scanner scanner, Graph graph) {
        System.out.println("\n=== Admin Safety Update ===");
        System.out.println("Available locations:");
        SafetyDatabase.safetyData.keySet().forEach(loc -> System.out.println("- " + loc));

        System.out.print("\nEnter location name: ");
        String locationName = scanner.next();

        Map<String, Double> attributes = SafetyDatabase.getLocationAttributes(locationName);
        if (attributes.isEmpty()) {
            System.out.println("Location not found! :()");
            return;
        }

        System.out.println("\nCurrent safety parameters:");
        attributes.forEach((k, v) -> System.out.printf("- %s: %.2f%n", k, v));

        Map<String, Double> updatedAttributes = new HashMap<>();
        for (String param : attributes.keySet()) {
            if (param.equals("weightedScore"))
                continue;

            System.out.printf("Enter new value for %s (current: %.2f): ", param, attributes.get(param));
            try {
                double newValue = Double.parseDouble(scanner.next());
                updatedAttributes.put(param, clampValue(newValue));
            } catch (NumberFormatException e) {
                System.out.println("Invalid input :(. Keeping current value.");
                updatedAttributes.put(param, attributes.get(param));
            }
        }

        updateLocationSafety(locationName, updatedAttributes, graph);
    }

    public Map<String, Double> getWeights() {
        return new HashMap<>(weights);
    }

    // Helper Methods
    private String getValidFeedback(Scanner scanner, String param) {
        while (true) {
            System.out.printf("Feedback for %s (safe/unsafe): ", param);
            String input = scanner.next().toLowerCase();
            if (input.equals("safe") || input.equals("unsafe")) {
                return input;
            }
            System.out.println("Invalid input. Please enter 'safe' or 'unsafe'.");
        }
    }

    private double clampValue(double value) {
        return Math.max(MIN_SAFETY_VALUE, Math.min(MAX_SAFETY_VALUE, value));
    }

    private void updateLocationSafety(String locationName, Map<String, Double> updatedAttributes, Graph graph) {
        // Update database with new attributes
        SafetyDatabase.addSafetyAttributes(locationName, updatedAttributes);

        // Recalculate safety rating
        double newRating = SafetyDatabase.calculateSafetyRating(locationName);
        Location location = LocationService.geocode(locationName);

        // Update all connected edges
        updateConnectedEdges(graph, location, newRating);

        System.out.printf("%n✅ Successfully updated %s%n", locationName);
        System.out.printf("New safety rating: %.2f%n", newRating);
    }

    private void updateConnectedEdges(Graph graph, Location location, double newRating) {
        // Update edges TO this location
        graph.adjList.forEach((source, edges) -> {
            edges.stream()
                    .filter(edge -> edge.to.equals(location))
                    .forEach(edge -> {
                        double sourceRating = SafetyDatabase.calculateSafetyRating(source.name);
                        edge.safetyRating = (sourceRating + newRating) / 2;
                    });
        });

        // Update edges FROM this location
        if (graph.adjList.containsKey(location)) {
            graph.adjList.get(location).forEach(edge -> {
                double destRating = SafetyDatabase.calculateSafetyRating(edge.to.name);
                edge.safetyRating = (newRating + destRating) / 2;
            });
        }
    }
}
