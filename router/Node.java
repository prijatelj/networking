package router;

import java.util.ArrayList;

public class Node{
    
    public static int flag;
    public static int interf;
    public static int[] ip = new int [4]; // IP address
    public ArrayList <Edge> neighbors;
    
    public boolean inQueue = false;
    public int posPQ;


    public Node(){}
    public Node(int flag, int interf, int[] ip){
        this.flag = flag;
        this.interf = interf;
        this.ip = ip;
        neighbors = new ArrayList<Edge>();
    }

    public void addNeighbor(int cost, Node neighbor){
        neighbors.add(new Edge(cost, neighbor));
    }
    public void addNeighbor(Edge e){
        neighbors.add(e);
    }

    // Edges are Pairs of cost and destination Node
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

        public Edge(int cost, Node to){
            this.cost = cost;
            this.to = to;
        }
    }
}
