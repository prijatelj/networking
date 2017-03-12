package router;

import java.util.HashSet;
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
    public static void dijkstra(Node src){
        HashSet<Node> visited = new HashSet<>();
        PriorityQueue<Node> pq = new PriorityQueue<>();

        // Traverse graph, mark their priority as infinte & set prev = NULL
        initialize(src, pq, visited);
        
        src.priority = 0;
        pq.add(src); // Necessary?
        
        int alt;

        while(pq.peek() != null){
            Node u = pq.poll();

            for (Edge e : u.neighbors){
                alt = u.priority + e.cost;

                if (alt < e.to.priority){
                    e.to.priority = alt;
                    e.to.prev = u;
                    pq.remove(e.to); // decrease priority
                    pq.add(e.to);
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
            
            for (Edge e : src.neighbors){
                initialize(e.to, pq, visited);
            }
        }
    }
}
