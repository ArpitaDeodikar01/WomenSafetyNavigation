# WomenSafetyNavigation
HI THERE
import java.util.*;

class Edge {
    int destination;
    double safetyRating;
    int streetlights, crowd, cctv; // Store safety attributes

    Edge(int destination, double safetyRating, int streetlights, int crowd, int cctv) {
        this.destination = destination;
        this.safetyRating = safetyRating;
        this.streetlights = streetlights;
        this.crowd = crowd;
        this.cctv = cctv;
    }
}

class Node {
    int vertex;
    double safetyScore;

    Node(int vertex, double safetyScore) {
        this.vertex = vertex;
        this.safetyScore = safetyScore;
    }
}

class Graph {
    int vertices;
    LinkedList<Edge>[] adjList;

    Graph(int vertices) {
        this.vertices = vertices;
        adjList = new LinkedList[vertices];
        for (int i = 0; i < vertices; i++) {
            adjList[i] = new LinkedList<>();
        }
    }

    void addEdge(int source, int destination, int streetlights, int crowd, int cctv) {
        double safetyRating = (streetlights + crowd + cctv) / 9.0; // Normalized safety score
        adjList[source].add(new Edge(destination, safetyRating, streetlights, crowd, cctv));
        adjList[destination].add(new Edge(source, safetyRating, streetlights, crowd, cctv)); // Undirected graph
    }

    void findSafestPath(int start, int end) {
        double[] safetyScore = new double[vertices];
        Arrays.fill(safetyScore, Double.NEGATIVE_INFINITY);
        safetyScore[start] = 1.0;

        PriorityQueue<Node> pq = new PriorityQueue<>((a, b) -> Double.compare(b.safetyScore, a.safetyScore));
        pq.add(new Node(start, 1.0));

        int[] parent = new int[vertices];
        Arrays.fill(parent, -1);

        Map<Integer, Edge> pathEdges = new HashMap<>(); // Stores edges in the safest path

        while (!pq.isEmpty()) {
            Node node = pq.poll();
            int u = node.vertex;
            double score = node.safetyScore;

            if (u == end) break;

            for (Edge edge : adjList[u]) {
                int v = edge.destination;
                double newScore = score * edge.safetyRating;

                if (newScore > safetyScore[v]) {
                    safetyScore[v] = newScore;
                    pq.add(new Node(v, newScore));
                    parent[v] = u;
                    pathEdges.put(v, edge); // Store edge for attribute tracking
                }
            }
        }

        if (safetyScore[end] == Double.NEGATIVE_INFINITY) {
            System.out.println("No safe path available!");
            return;
        }

        printPath(parent, pathEdges, start, end);
        System.out.println("\nTotal Safety Score: " + safetyScore[end]);
    }

    void printPath(int[] parent, Map<Integer, Edge> pathEdges, int start, int current) {
        if (parent[current] == -1) {
            System.out.print("Safest Path: " + current);
            return;
        }
        
        printPath(parent, pathEdges, start, parent[current]);

        Edge edge = pathEdges.get(current);
        System.out.print(" → " + current + " [Streetlights: " + edge.streetlights +
                         ", Crowd: " + edge.crowd + ", CCTV: " + edge.cctv + "]");
    }
}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        // Hardcoded Map with Safety Attributes
        int vertices = 6;
        Graph graph = new Graph(vertices);

        // (Source, Destination, Streetlights, Crowd, CCTV)
        graph.addEdge(0, 1, 3, 3, 3);
        graph.addEdge(0, 2, 2, 2, 2);
        graph.addEdge(1, 2, 1, 3, 3);
        graph.addEdge(1, 3, 2, 3, 2);
        graph.addEdge(2, 4, 3, 1, 3);
        graph.addEdge(3, 4, 2, 3, 3);
        graph.addEdge(4, 5, 3, 3, 3);
        graph.addEdge(3, 5, 1, 2, 2);

        // User Inputs
        System.out.print("Enter start location: ");
        int start = sc.nextInt();
        System.out.print("Enter destination location: ");
        int end = sc.nextInt();

        graph.findSafestPath(start, end);
    }
}
