public class Edge {
    Location from;
    Location to;
    double safetyRating;
    double distance;

    Edge(Location from, Location to, double safetyRating,double distance) {
        this.from = from;
        this.to = to;
        this.safetyRating = safetyRating;
        this.distance = distance;
    }
}
