package router;

import java.util.ArrayList;

/**
 * Node represents either a router or a network.
 * @author Derek S. Prijatelj
 */
public class Node{
    
    public int flag;
    public int interf;// unique
    public String ip; // IP address
    public ArrayList <Edge> neighbors;
    public Node prev;

    // HashMap of neighbors to this router <interf, cost>
    // may want <Node.ipAddress, cost>
    //public HashMap <Integer, Integer> neighbors = new HashMap<>();
    
    public boolean inQueue = false;
    public int posPQ;


    public Node(){}
    public Node(int flag, int interf, String ip){
        this.flag = flag;
        this.interf = interf;
        this.ip = ip;
        neighbors = new ArrayList<Edge>();
    }
    
    public void addNeighbor(int cost, Node neighbor){
        neighbors.add(new Edge(neighbor, cost));
    }

    /* Edges are Pairs of cost and destination Node
    public static class Edge implements Comparable <Edge>{
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
    //*/

    //* equal if have same ip address, cuz those should be unique.
    @Override
    public boolean equals(Object obj){
        if (obj instanceof Node && ip.equals(((Node)obj).ip)){
            return true;
        }
        return false;
    }

    @Override
    public int hashCode(){
        return ip.hashCode();   
    }
    //*/
    
}
