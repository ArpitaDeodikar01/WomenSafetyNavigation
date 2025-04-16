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

    void addEdge(Location from, Location to, double safetyRating, double distance) {
        Edge edge = new Edge(from, to, safetyRating, distance);
        adjList.get(from).add(edge);
    }

    public Location findSafestNearestSpot(Location start) {
        // PriorityQueue stores locations based on a combined safety score (safetyRating and distance)
        PriorityQueue<Location> pq = new PriorityQueue<>(Comparator.comparingDouble(location -> getSafetyScoreWithDistance(location, start)));
        Set<Location> visited = new HashSet<>();
        
        // Add the start location to the queue with an initial distance of 0.
        pq.add(start);
    
        while (!pq.isEmpty()) {
            Location current = pq.poll();
    
            // If the location is already visited, skip it
            if (visited.contains(current)) {
                continue;
            }
            visited.add(current);
    
            // Check if we found a valid safest spot; if we find one, return it immediately
            if (!current.equals(start)) {
                return current;  // Found the nearest safest location
            }
    
            // Check all neighbors and add them to the queue
            for (Edge edge : adjList.getOrDefault(current, new ArrayList<>())) {
                Location neighbor = edge.to;
                if (!visited.contains(neighbor)) {
                    pq.add(neighbor); // Add to the queue for further processing
                }
            }
        }
    
        // If no nearest safe spot was found, return null or a default message
        return null;
    }
    
    // Calculate the safety score considering both safety and distance from the start location
    double getSafetyScoreWithDistance(Location location, Location start) {
        double safetyScore = 0;
        double totalDistance = 0;
        List<Edge> edges = adjList.get(location);
    
        if (edges != null && !edges.isEmpty()) {
            for (Edge edge : edges) {
                // SafetyRating affects the score; distance is used as a secondary factor
                safetyScore += edge.safetyRating;
                totalDistance += edge.distance;
            }
            // Combine the safety and distance factors
            // You can adjust the logic here to give more weight to safety or distance as needed
            safetyScore = safetyScore / edges.size() + totalDistance / edges.size();
        }
    
        return safetyScore;
    }
    
    

    List<Location> findSafestPath(Location start, Location end) {
        Map<Location, Double> dist = new HashMap<>();
        Map<Location, Location> prev = new HashMap<>();
        Set<Location> visited = new HashSet<>();

        // Initialize distances to infinity
        for (Location loc : adjList.keySet()) {
            dist.put(loc, Double.MAX_VALUE);
        }
        dist.put(start, 0.0);

        // PriorityQueue based on distance (danger score considering safety and distance)
        PriorityQueue<Location> pq = new PriorityQueue<>(Comparator.comparingDouble(dist::get));
        pq.add(start);

        while (!pq.isEmpty()) {
            Location current = pq.poll();
            if (!visited.add(current))
                continue;

            List<Edge> edges = adjList.getOrDefault(current, new ArrayList<>());
            for (Edge edge : edges) {
                // We consider both safety rating and distance in the danger score calculation
                double dangerScore = 1.0 - edge.safetyRating + edge.distance; // Higher safetyRating = safer path
                double newDist = dist.get(current) + dangerScore;

                if (newDist < dist.get(edge.to)) {
                    dist.put(edge.to, newDist);
                    prev.put(edge.to, current);
                    pq.add(edge.to);
                }
            }
        }

        // Rebuild the path
        List<Location> path = new ArrayList<>();
        for (Location at = end; at != null; at = prev.get(at)) {
            path.add(at);
        }
        Collections.reverse(path); // Reverse to get the path from start to end
        return path;
    }

    // Get safety score for a location considering both safety rating and distance
    double getSafetyScore(Location location) {
        double totalSafety = 0;
        double totalDistance = 0;
        List<Edge> edges = adjList.get(location);
        if (edges == null || edges.isEmpty()) {
            return 0;
        }
        for (Edge edge : edges) {
            totalSafety += edge.safetyRating;
            totalDistance += edge.distance;
        }
        // Consider both safety and distance when calculating the safety score
        return totalSafety / edges.size() + totalDistance / edges.size();
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

