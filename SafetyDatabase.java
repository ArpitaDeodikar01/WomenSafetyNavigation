import java.util.HashMap;
import java.util.Map;

public class SafetyDatabase {
    static Map<String, Map<String, Double>> safetyData = new HashMap<>();

    static void addSafetyAttributes(String locationName, Map<String, Double> attributes) {
        safetyData.put(locationName, attributes);
    }

    public static float calculateSafetyRating(String locationName) {
        Map<String, Double> attributes = safetyData.get(locationName);
        if (attributes == null)
            return 0.0f;

        double total = 0.0;
        for (double val : attributes.values()) {
            total += val;
        }
        return (float) (total / attributes.size()); // Average safety rating
    }
    
    static Map<String, Double> getLocationAttributes(String locationName) {
        return safetyData.getOrDefault(locationName, new HashMap<>());
    }
    
    static void initializeDatabase() {
        // Add safety attributes for each location
        addSafetyAttributes("Home", Map.of("streetlights", 0.9, "crowd", 0.9, "cctv", 0.9));
        addSafetyAttributes("Office", Map.of("streetlights", 0.6, "crowd", 0.6, "cctv", 0.6));
        addSafetyAttributes("Market", Map.of("streetlights", 0.6, "crowd", 0.6, "cctv", 0.6));
        addSafetyAttributes("Mall", Map.of("streetlights", 0.5, "crowd", 0.5, "cctv", 0.5));
        addSafetyAttributes("Restroom", Map.of("streetlights", 0.2, "crowd", 0.2, "cctv", 0.2));
        addSafetyAttributes("Park", Map.of("streetlights", 0.4, "crowd", 0.4, "cctv", 0.4));
        addSafetyAttributes("University", Map.of("streetlights", 0.8, "crowd", 0.8, "cctv", 0.8));
    }
}
