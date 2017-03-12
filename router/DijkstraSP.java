package router;

import java.util.HashSet;
import java.util.PriorityQueue; // This will need replaced w/ dijkstra opt. PQ

/**
 * Implements dijkstra's shortest path algorithm for nodes that contain the prev
 * field
 *
 * @param source the root of the algorithm and the resulting shortest path tree
 * @author Derek S. Prijatelj
 */
public class DijkstraSP{
    
    /**
     * Performs dijkstra's shortest path algorithm on the given source node.
     * Assumes all nodes contain a set of their neighbors
     */
    public static void dijkstra(Node source){
        HashSet<Node> visited = new HashSet<>();

        // Traverse graph, mark their priority as infinte & set prev = NULL
        initialize(source, visited);
        
        
    }

    /**
     * Traverses graph depth first, setting all unvisited nodes priority to 
     * infinite and prev to Null
     */
    private static void initialize(Node source, HashSet<Node> visited){
        if (!visited.contains(source)){
            source.priority = Integer.MAX_VALUE;
            source.prev = null;

            visited.add(source);
            
            for (Edge e : source.neighbors){
                initialize(e.to, visited);
            }
        }
    }
}
