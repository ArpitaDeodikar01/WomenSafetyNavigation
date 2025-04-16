public Location findSafestNearestSpot(Location currLocation) {
    // g(n): Actual distance from start to current node
    Map<Location, Double> gScore = new HashMap<>();
    gScore.put(currLocation, 0.0);

    // f(n): Estimated total cost (g + heuristic)
    PriorityQueue<Location> openSet = new PriorityQueue<>(Comparator.comparingDouble(loc ->
        gScore.getOrDefault(loc, Double.MAX_VALUE) + getHeuristic(loc, currLocation)
    ));

    Set<Location> visited = new HashSet<>();
    openSet.add(currLocation);

    while (!openSet.isEmpty()) {
        Location current = openSet.poll();

        if (visited.contains(current)) continue;
        visited.add(current);

        // Return the first valid spot that is not the current location
        if (!current.equals(currLocation)) {
            return current;
        }

        for (Edge edge : adjList.getOrDefault(current, new ArrayList<>())) {
            Location neighbor = edge.to;
            double tentativeG = gScore.get(current) + edge.distance;

            if (tentativeG < gScore.getOrDefault(neighbor, Double.MAX_VALUE)) {
                gScore.put(neighbor, tentativeG);
                openSet.add(neighbor);
            }
        }
    }

    return null;
}

// Heuristic: Combines safety and distance (as in your original code)
double getHeuristic(Location location, Location start) {
    double safetyScore = 0;
    double totalDistance = 0;
    List<Edge> edges = adjList.get(location);

    if (edges != null && !edges.isEmpty()) {
        for (Edge edge : edges) {
            safetyScore += edge.safetyRating;
            totalDistance += edge.distance;
        }
        return safetyScore / edges.size() + totalDistance / edges.size();
    }

    return Double.MAX_VALUE; // if no data, assume worst
}

