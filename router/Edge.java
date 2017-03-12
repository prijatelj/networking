package router;

import java.util.ArrayList;

/**
 * Edges are Pairs of cost and destination Node
 * @author Derek S. Prijatelj
 */
public class Edge implements Comparable<Edge>{
    int cost = Integer.MAX_VALUE;
    Node to;

    @Override
    public int compareTo(Edge o){
        return Integer.compare(this.cost, o.cost);
    }

    public Edge(Node to, int cost){
        this.to = to;
        this.cost = cost;
    }
}
