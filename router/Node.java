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
    public int priority;


    public Node(){}
    public Node(int flag, int interf, String ip){
        this.flag = flag;
        this.interf = interf;
        this.ip = ip;
        neighbors = new ArrayList<Edge>();
    }
    public Node(int flag, int interf, String ip, Node n, int cost){
        this.Node(flag, interf, ip);
        neighbors.add(new Edge(n, cost));
    }
    
    public void addNeighbor(int cost, Node neighbor){
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
    
}
