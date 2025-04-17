public class Edge {
    Location from;
    Location to;
    double safetyRating;
    double distance; // New parameter to represent the distance between locations

    // Constructor now includes distance
    public Edge(Location from, Location to, double safetyRating, double distance) {
        this.from = from;
        this.to = to;
        this.safetyRating = safetyRating;
        this.distance = distance;
    }

    // Getter for distance
    public double getDistance() {
        return distance;
    }

    // You can also add other getter methods if necessary for other attributes
}
