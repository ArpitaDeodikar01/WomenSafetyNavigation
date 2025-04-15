import java.util.HashMap;
import java.util.Map;

public class SafetyDatabase {
    static Map<String, Map<String, Double>> safetyData = new HashMap<>();

    static void addSafetyAttributes(String locationName, Map<String, Double> attributes) {
        safetyData.put(locationName, attributes);
    }

    static double calculateSafetyRating(String locationName) {
        Map<String, Double> attributes = safetyData.get(locationName);
        if (attributes == null)
            return 0.0;

        double total = 0.0;
        for (double val : attributes.values()) {
            total += val;
        }
        return total / attributes.size(); // Average safety rating
    }
    
    static Map<String, Double> getLocationAttributes(String locationName) {
        return safetyData.getOrDefault(locationName, new HashMap<>());
    }
    
    static void initializeDatabase() {
        // Add safety attributes for each location
        addSafetyAttributes("Home", Map.of("streetlights", 0.9, "crowd", 0.7, "cctv", 0.8));
        addSafetyAttributes("Office", Map.of("streetlights", 0.6, "crowd", 0.5, "cctv", 0.7));
        addSafetyAttributes("Market", Map.of("streetlights", 0.7, "crowd", 0.6, "cctv", 0.6));
        addSafetyAttributes("Mall", Map.of("streetlights", 0.5, "crowd", 0.8, "cctv", 0.6));
        addSafetyAttributes("Restroom", Map.of("streetlights", 0.3, "crowd", 0.2, "cctv", 0.1));
        addSafetyAttributes("Park", Map.of("streetlights", 0.7, "crowd", 0.5, "cctv", 0.6));
        addSafetyAttributes("University", Map.of("streetlights", 0.6, "crowd", 0.8, "cctv", 0.6));
    }
}
