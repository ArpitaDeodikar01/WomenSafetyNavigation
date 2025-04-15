import java.util.*;

public class Graph {
    Map<Location, List<Edge>> adjList = new HashMap<>();

    public void displayAvailablePaths() {
        System.out.println("\n📍 Available Routes:");
        int index = 1;
        Set<String> printed = new HashSet<>();
        for (Location source : adjList.keySet()) {
            for (Edge edge : adjList.get(source)) {
                String key = source.name + "-" + edge.to.name;
                String reverseKey = edge.to.name + "-" + source.name;
                if (!printed.contains(key) && !printed.contains(reverseKey)) {
                    System.out.printf("%d. %s -> %s\n", index++, source.name, edge.to.name);
                    printed.add(key);
                }
            }
        }
    }

    void addLocation(Location location) {
        adjList.putIfAbsent(location, new ArrayList<>());
    }

    void addEdge(Location from, Location to, double safetyRating) {
        Edge edge = new Edge(from, to, safetyRating);
        adjList.get(from).add(edge);
    }

    List<Location> findSafestPath(Location start, Location end) {
        Map<Location, Double> dist = new HashMap<>();
        Map<Location, Location> prev = new HashMap<>();
        Set<Location> visited = new HashSet<>();

        for (Location loc : adjList.keySet()) {
            dist.put(loc, Double.MAX_VALUE);
        }
        dist.put(start, 0.0);

        PriorityQueue<Location> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start);

        while (!pq.isEmpty()) {
            Location current = pq.poll();
            if (!visited.add(current))
                continue;

            List<Edge> edges = adjList.getOrDefault(current, new ArrayList<>());
            for (Edge edge : edges) {
                double dangerScore = 1.0 - edge.safetyRating; // Higher safetyRating = safer path
                double newDist = dist.get(current) + dangerScore;

                if (newDist < dist.get(edge.to)) {
                    dist.put(edge.to, newDist);
                    prev.put(edge.to, current);
                    pq.add(edge.to);
                }
            }
        }

        List<Location> path = new ArrayList<>();
        for (Location at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path);
        return path;
    }

    void updateEdgeSafety(Location from, Location to, double newSafetyRating) {
        List<Edge> edges = adjList.getOrDefault(from, new ArrayList<>());
        for (Edge edge : edges) {
            if (edge.to.equals(to)) {
                edge.safetyRating = newSafetyRating;
                break;
            }
        }
    }

    void displaySafestPathWithWeights(List<Location> path) {
        if (path == null || path.size() < 2) {
            System.out.println("No valid path found.");
            return;
        }

        System.out.println("\n🔐 Safest Path with Safety Ratings:");
        double totalDangerScore = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Location from = path.get(i);
            Location to = path.get(i + 1);
            double safety = getEdgeSafety(from, to);
            double danger = 1.0 - safety;
            totalDangerScore += danger;
            System.out.printf("%d. %s → %s  |  Safety: %.2f  |  Danger: %.2f\n",
                    i + 1, from.name, to.name, safety, danger);
        }

        System.out.printf("\n🧭 Total Danger Score of Path: %.2f (lower is safer)\n", totalDangerScore);
    }

    double getEdgeSafety(Location from, Location to) {
        for (Edge edge : adjList.getOrDefault(from, new ArrayList<>())) {
            if (edge.to.equals(to)) {
                return edge.safetyRating;
            }
        }
        return 0.0; // if edge doesn't exist
    }
}
