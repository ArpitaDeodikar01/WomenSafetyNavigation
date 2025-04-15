public class Edge {
    Location from;
    Location to;
    double safetyRating;

    Edge(Location from, Location to, double safetyRating) {
        this.from = from;
        this.to = to;
        this.safetyRating = safetyRating;
    }
}
