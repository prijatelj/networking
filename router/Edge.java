package router;

import java.util.ArrayList;

/**
 * Edges are Pairs of cost and destination Node
 * @author Derek S. Prijatelj
 */
public class Edge implements Comparable <Edge>{
    int cost = Integer.MAX_VALUE;
    Node to;

    @Override
    public int compareTo(Edge o){
        if (this.cost < o.cost){
            return -1;
        } else if (this.cost > o.cost){
            return 1;
        }
        return 0;
    }

    public Edge(Node to, int cost){
        this.to = to;
        this.cost = cost;
    }
}
