import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class FeedbackSystem {
    Map<String, Double> weights;

    FeedbackSystem(Map<String, Double> initialWeights) {
        this.weights = new HashMap<>(initialWeights);
    }

    void updateWeightsInteractive(Scanner scanner) {
        System.out.println("Which parameters do you want to adjust?");
        System.out.println("Available: streetlights / crowd / cctv");
        System.out.println("Type parameters separated by commas (e.g., streetlights,crowd):");
        String[] selectedParams = scanner.nextLine().toLowerCase().split(",");

        for (String param : selectedParams) {
            param = param.trim(); // clean whitespace

            if (weights.containsKey(param)) {
                System.out.println("Feedback for " + param + " (safe / unsafe):");
                String feedback = scanner.nextLine().toLowerCase();

                try {
                    String encryptedFeedback = EncryptionUtil.encrypt(feedback);
                    String decryptedFeedback = EncryptionUtil.decrypt(encryptedFeedback);

                    double change = decryptedFeedback.equals("safe") ? 0.1 : -0.1;
                    double current = weights.get(param);
                    double newValue = Math.max(0.1, Math.min(1.0, current + change));
                    weights.put(param, newValue);

                    System.out.printf("Encrypted feedback: %s%n", encryptedFeedback);
                    System.out.printf("Updated %s weight from %.2f to %.2f%n", param, current, newValue);

                } catch (Exception e) {
                    System.out.println("Encryption error: " + e.getMessage());
                }

            } else {
                System.out.println("Invalid parameter: " + param);
            }
        }
    }

    void applyFeedbackToLocation(Scanner scanner, Location location, Graph graph) {
        String locName = location.name;
        Map<String, Double> currentAttributes = SafetyDatabase.safetyData.get(locName);

        if (currentAttributes == null) {
            System.out.println("No safety data found for this location.");
            return;
        }

        Map<String, Double> updatedAttributes = new HashMap<>(currentAttributes);

        System.out.println("Provide feedback for " + locName);
        for (String param : currentAttributes.keySet()) {
            boolean validFeedback = false;
            String feedback = "";

            while (!validFeedback) {
                System.out.println("Feedback for " + param + " (safe / unsafe):");
                feedback = scanner.nextLine().toLowerCase();

                if (feedback.equals("safe") || feedback.equals("unsafe")) {
                    validFeedback = true;
                } else {
                    System.out.println("Invalid feedback. Please enter 'safe' or 'unsafe'.");
                }
            }

            double change = feedback.equals("safe") ? 0.1 : -0.1;
            double current = currentAttributes.get(param);
            double newValue = Math.max(0.1, Math.min(1.0, current + change));
            updatedAttributes.put(param, newValue);
        }

        // Update the SafetyDatabase with the new attributes
        SafetyDatabase.addSafetyAttributes(locName, updatedAttributes);

        // Recalculate the new average safety rating for this location
        double newRating = SafetyDatabase.calculateSafetyRating(locName);

        // Update all edges that connect to/from this location
        for (Location other : graph.adjList.keySet()) {
            // Update the edges from other locations to this location
            for (Edge edge : graph.adjList.get(other)) {
                if (edge.to.equals(location)) {
                    double otherRating = SafetyDatabase.calculateSafetyRating(other.name);
                    edge.safetyRating = (otherRating + newRating) / 2;
                }
            }

            // Update the edges from this location to others
            for (Edge edge : graph.adjList.get(location)) {
                double otherRating = SafetyDatabase.calculateSafetyRating(edge.to.name);
                edge.safetyRating = (otherRating + newRating) / 2;
            }
        }

        System.out.println("Updated safety rating for " + locName + ": " + newRating);
    }

    void adminUpdateLocationSafety(Scanner scanner, Graph graph) {
        System.out.println("\n==== Admin Safety Parameter Update ====");
        System.out.println("Available locations:");
        
        // Get all locations from the safety database
        for (String locName : SafetyDatabase.safetyData.keySet()) {
            System.out.println("- " + locName);
        }
        
        System.out.println("\nEnter location name to update:");
        String locationName = scanner.nextLine();
        
        Map<String, Double> attributes = SafetyDatabase.getLocationAttributes(locationName);
        if (attributes.isEmpty()) {
            System.out.println("Location not found!");
            return;
        }
        
        Map<String, Double> updatedAttributes = new HashMap<>(attributes);
        System.out.println("\nCurrent safety parameters for " + locationName + ":");
        
        for (Map.Entry<String, Double> entry : attributes.entrySet()) {
            System.out.printf("%s: %.2f\n", entry.getKey(), entry.getValue());
        }
        
        System.out.println("\nEnter new values (between 0.0 and 1.0):");
        for (String param : attributes.keySet()) {
            System.out.printf("New value for %s (current: %.2f): ", param, attributes.get(param));
            try {
                double newValue = Double.parseDouble(scanner.nextLine());
                newValue = Math.max(0.0, Math.min(1.0, newValue)); // Clamp between 0 and 1
                updatedAttributes.put(param, newValue);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Keeping current value.");
            }
        }
        
        // Update the SafetyDatabase with new values
        SafetyDatabase.addSafetyAttributes(locationName, updatedAttributes);
        
        // Recalculate safety ratings for all edges
        Location location = LocationService.geocode(locationName);
        double newRating = SafetyDatabase.calculateSafetyRating(locationName);
        
        // Update all edges that connect to/from this location
        for (Location other : graph.adjList.keySet()) {
            // Update the edges from other locations to this location
            for (Edge edge : graph.adjList.get(other)) {
                if (edge.to.equals(location)) {
                    double otherRating = SafetyDatabase.calculateSafetyRating(other.name);
                    edge.safetyRating = (otherRating + newRating) / 2;
                }
            }

            // Update the edges from this location to others
            if (graph.adjList.containsKey(location)) {
                for (Edge edge : graph.adjList.get(location)) {
                    double otherRating = SafetyDatabase.calculateSafetyRating(edge.to.name);
                    edge.safetyRating = (otherRating + newRating) / 2;
                }
            }
        }
        
        System.out.println("✅ Successfully updated safety parameters for " + locationName);
        System.out.println("New safety rating: " + newRating);
    }

    Map<String, Double> getWeights() {
        return new HashMap<>(weights);
    }
}
