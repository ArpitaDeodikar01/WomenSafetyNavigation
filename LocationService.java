public class LocationService {
    static Location geocode(String address) {
        // Simplified version without latitude/longitude
        return switch (address.toLowerCase()) {
            case "home" -> new Location("Home");
            case "office" -> new Location("Office");
            case "market" -> new Location("Market");
            case "mall" -> new Location("Mall");
            case "restroom" -> new Location("Restroom");
            case "park" -> new Location("Park");
            case "university" -> new Location("University");
            default -> new Location(address);
        };
    }
}
