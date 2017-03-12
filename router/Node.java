package router;

import java.util.ArrayList;

/**
 * Node represents either a router or a network.
 * @author Derek S. Prijatelj
 */
public class Node implements Comparable<Node>{
    
    public int flag;
    public int interf;// unique
    public String ip; // IP address
    public ArrayList <Edge> neighbors;
    public Node prev;

    // HashMap of neighbors to this router <interf, cost>
    // may want <Node.ipAddress, cost>
    //public HashMap <Integer, Integer> neighbors = new HashMap<>();
    
    public boolean inQueue = false;
    public int priority = Integer.MAX_VALUE;


    public Node(){}
    public Node(int flag, int interf, String ip){
        this.flag = flag;
        this.interf = interf;
        this.ip = ip;
        neighbors = new ArrayList<Edge>();
    }
    public Node(int flag, int interf, String ip, Node neighbor, int cost){
        this(flag, interf, ip);
        neighbors.add(new Edge(neighbor, cost));
    }
    
    public void addNeighbor(Node neighbor, int cost){
        neighbors.add(new Edge(neighbor, cost));
    }

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
    
    @Override
    public int compareTo(Node o){
        return Integer.compare(this.priority, o.priority);
    }
}
