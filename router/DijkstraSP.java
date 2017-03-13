package router;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue; // This will need replaced w/ dijkstra opt. PQ

/**
 * Implements dijkstra's shortest path algorithm for nodes that contain the prev
 * field
 *
 * @author Derek S. Prijatelj
 */
public class DijkstraSP{

    /**
     * Performs dijkstra's shortest path algorithm on the given source node.
     * Assumes all nodes contain a set of their neighbors
     *
     * @param src the root of the algorithm and the resulting shortest path tree
     */
    public static void dijkstra(Node src, HashMap<String, Node> verts){
        HashSet<Node> visited = new HashSet<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Traverse graph, mark their priority as infinte & set prev = NULL
        //initialize(src, pq, visited);
        init(verts, pq);

        src.priority = 0;
        pq.add(src); // Necessary?
        
        int alt;
        int cost;
        Node neighbor, u;

        while(pq.peek() != null){
            u = pq.poll();

            for (Map.Entry<Node, Integer> entry : u.neighbors.entrySet()){
                cost = entry.getValue().intValue();
                neighbor = entry.getKey();

                alt = u.priority + cost;

                if (alt < neighbor.priority){
                    neighbor.priority = alt;
                    neighbor.prev = u;
                    pq.remove(neighbor); // decrease priority
                    pq.add(neighbor);
                }
            }
        }
    }

    /**
     * Traverses graph depth first, setting all unvisited nodes priority to 
     * infinite and prev to Null
     */
    private static void initialize(Node src, PriorityQueue<Node> pq,
            HashSet<Node> visited){
        if (!visited.contains(src)){
            src.priority = Integer.MAX_VALUE;
            src.prev = null;

            visited.add(src);
            pq.add(src);
            
            for (Node n : src.neighbors.keySet()){
                initialize(n, pq, visited);
            }
        }
    }

    private static void init(HashMap<String, Node> verts,
            PriorityQueue<Node> pq){
        for (Node tmp : verts.values()){
            tmp.priority = Integer.MAX_VALUE;
            tmp.prev = null;

            pq.add(tmp);
        }
    }
}
