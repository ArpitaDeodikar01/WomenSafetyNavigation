import java.util.*;

public class Graph {
    Map<Location, List<Edge>> adjList = new HashMap<>();

    public void displayAvailablePaths() {
        System.out.println("\n Available Bidirectional Routes: \n");
        System.out.println("┌────┬────────────────────────────┐");
        System.out.printf("│ %-2s │ %-26s │\n", "#", "Route");
        System.out.println("├────┼────────────────────────────┤");

        int index = 1;
        Set<String> printed = new HashSet<>();
        for (Location source : adjList.keySet()) {
            for (Edge edge : adjList.get(source)) {
                String key = source.name + "-" + edge.to.name;
                String reverseKey = edge.to.name + "-" + source.name;

                if (!printed.contains(key) && !printed.contains(reverseKey)) {
                    String route = source.name + "->|<- " + edge.to.name; // ⇄ shows bidirectionality
                    System.out.printf("│ %-2d │ %-26s │\n", index++, route);
                    printed.add(key);
                }
            }
        }

        System.out.println("└────┴────────────────────────────┘");
    }

    void addLocation(Location location) {
        adjList.putIfAbsent(location, new ArrayList<>());
    }

    void addEdge(Location from, Location to, double safetyRating, double distance) {
        adjList.get(from).add(new Edge(from, to, safetyRating, distance));
        adjList.get(to).add(new Edge(to, from, safetyRating, distance)); // Bidirectional
    }

    public Location findSafestNearestSpot(Location currLocation) {
        Map<Location, Double> gScore = new HashMap<>();
        gScore.put(currLocation, 0.0);

        PriorityQueue<Location> openSet = new PriorityQueue<>(Comparator
                .comparingDouble(loc -> gScore.getOrDefault(loc, Double.MAX_VALUE) + getHeuristic(currLocation, loc)));

        Set<Location> visited = new HashSet<>();
        openSet.add(currLocation);

        while (!openSet.isEmpty()) {
            Location current = openSet.poll();

            if (visited.contains(current))
                continue;
            visited.add(current);

            if (!current.equals(currLocation)) {
                return current;
            }

            for (Edge edge : adjList.getOrDefault(current, new ArrayList<>())) {
                Location neighbor = edge.to;
                double cost = edge.distance + (1.0 - edge.safetyRating);
                double tentativeG = gScore.get(current) + cost;

                if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                    gScore.put(neighbor, tentativeG);
                    openSet.add(neighbor);
                }
            }
        }

        return null;
    }

    double getHeuristic(Location from, Location to) {
        List<Edge> edges = adjList.get(from);   
        if (edges != null) {
            for (Edge edge : edges) {
                if (edge.to.equals(to)) {
                    return edge.distance + (1.0 - edge.safetyRating);
                }
            }
        }
        return Double.MAX_VALUE;
    }

    List<Location> findSafestPath(Location start, Location end){//Djkistra{
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

            for (Edge edge : adjList.getOrDefault(current, new ArrayList<>())) {
                double dangerScore = 1.0 - edge.safetyRating + edge.distance;
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

    double getSafetyScore(Location location) {
        double totalSafety = 0;
        double totalDistance = 0;
        List<Edge> edges = adjList.get(location);
        if (edges == null || edges.isEmpty())
            return 0;

        for (Edge edge : edges) {
            totalSafety += edge.safetyRating;
            totalDistance += edge.distance;
        }

        return totalSafety / edges.size() + totalDistance / edges.size();
    }

    void updateEdgeSafety(Location from, Location to, double newSafetyRating) {
        for (Edge edge : adjList.getOrDefault(from, new ArrayList<>())) {
            if (edge.to.equals(to)) {
                edge.safetyRating = newSafetyRating;
                break;
            }
        }
    }

    void displaySafestPathWithWeights(List<Location> path) {
        if (path == null || path.size() < 2) {
            System.out.println("No valid path found. Try Again!! :/");
            return;
        }

        System.out.println("\n====Safest Path with Safety Ratings:==== \n");
        double totalDangerScore = 0.0;
        for (int i = 0; i < path.size() - 1; i++) {
            Location from = path.get(i);
            Location to = path.get(i + 1);
            double safety = getEdgeSafety(from, to);
            double danger = 1.0 - safety;
            totalDangerScore += danger;
            System.out.printf("%d. %s ->  %s  |  Safety: %.2f  |  Danger: %.2f\n",
                    i + 1, from.name, to.name, safety, danger);
        }

        System.out.printf("\n====Total Danger Score of Path: %.2f (lower is safer)====\n", totalDangerScore);
    }

    double getEdgeSafety(Location from, Location to) {
        for (Edge edge : adjList.getOrDefault(from, new ArrayList<>())) {
            if (edge.to.equals(to)) {
                return edge.safetyRating;
            }
        }
        return 0.0;
    }
}
