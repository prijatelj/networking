package router;

import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Simulates a Link State Router
 * @author Derek S. Prijatelj
 */
public class LinkStateRouter{
    // IP Address Interface Pair
    public HashMap<String, Node> fwdTable = new HashMap<>();
    public HashSet<Integer> usedInterf = new HashSet<>();
    public Node router;

    public LinkStateRouter(){
        router = new Node(1, -1,"120.0.0.1");
    }
    public LinkStateRouter(Node router){
        this.router = router;
    }

    public static class NoPathToHost extends Exception{
        public NoPathToHost () {}

        public NoPathToHost (String message){
            super (message);
        }

        public NoPathToHost (Throwable cause){
            super (cause);
        }

        public NoPathToHost (String message, Throwable cause){
            super (message, cause);
        }
    }

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
        dijkstraInit(verts, pq);

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
    private static void dijkstraInit(HashMap<String, Node> verts,
            PriorityQueue<Node> pq){
        for (Node tmp : verts.values()){
            tmp.priority = Integer.MAX_VALUE;
            tmp.prev = null;

            pq.add(tmp);
        }
    }

    public void initialize(){

        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine(), ip;
        String[] parts;
        int flag, interf, cost;
        int[] ipAddress = new int[4];

        while(!line.equals("0,0,0.0.0.0,0") && sc.hasNextLine()){
            // Flag, Interface, IP Address, Cost
            line = sc.nextLine();
            parts = line.split(",");

            if (parts.length != 4) {
                System.err.println(
                    "Error: Incorrect Initialization Input Format: "
                    + "Must be of the format: Flag, Interface, IP Address, Cost"
                    + "\nPlease submit input again."
                    );
                continue;
            }

            // TODO Handle NumberFormatException for all parseInt()
            flag = Integer.parseInt(parts[0]);
            interf = Integer.parseInt(parts[1]);
            cost = Integer.parseInt(parts[3]);
            ip = parts[2];
            parts = parts[2].split(".");

            if (parts.length != 4) {
                System.err.println(
                    "Error: Incorrect Initialization Input Format: "
                    + "IP address must be of the format: #.#.#.# \n"
                    + "where # is an integer value in the range [0,255]"
                    + "\nPlease submit input again."
                    );
                continue;
            }

            ipAddress[0] = Integer.parseInt(parts[0]);
            ipAddress[1] = Integer.parseInt(parts[1]);
            ipAddress[2] = Integer.parseInt(parts[2]);
            ipAddress[3] = Integer.parseInt(parts[3]);


            if (flag != 0 || flag != 1) {
                System.err.println("Error: Incorrect Initialize Flag Value: "
                    + "Flag value in input must be 0 or 1"
                    );
                continue;
            } else if (interf < 0) {
                System.err.println(
                    "Error: Incorrect Initialize Interface Value: "
                    + "Interface must be a unique positive integer"
                    + "\nPlease submit input again."
                    );
                continue;
            } else if ((ipAddress[0] < 0 && ipAddress[0] > 255)
                    || (ipAddress[1] < 0 && ipAddress[1] > 255)
                    || (ipAddress[2] < 0 && ipAddress[2] > 255)
                    || (ipAddress[3] < 0 && ipAddress[3] > 255)
                    ){
                System.err.println(
                    "Error: Incorrect Initialization Input Format: "
                    + "IP address must be of the format: #.#.#.# \n"
                    + "where # is an integer value in the range [0,255]"
                    + "\nPlease submit input again."
                    );
                continue;
            } else if (cost < 0){
                System.err.println("Error: Incorrect Cost Value: "
                    + "Cost must be apositive integer value."
                    + "\nPlease submit input again."
                    );
                continue;
            }

            if (!usedInterf.add(interf)){
                System.err.println("Error: Interface Already in Use: "
                    + "Interface values must be unique."
                    + "\nPlease submit input again."
                    );
                continue;
            }

            Node newNode = new Node(flag, interf, ip, router, cost);
            router.addNeighbor(newNode, cost);
            fwdTable.put(ip, newNode);
        }

        // Inform all Peer routers of other neighbors
        peerInform();
    }

    public void simulation(){

        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine(), ip1, ip2;
        String[] parts, ip1Parts, ip2Parts;
        int flag, interf, cost;
        int[] ipAddress1 = new int[4], ipAddress2 = new int[4];

        // Router Simulation: Receiving Packets & Info
        while (sc.hasNextLine()) {
            // Flag, Interface, IP Address1, IP Address2, Cost
            line = sc.nextLine();
            parts = line.split(",");

            if (parts.length != 5) {
                System.err.println(
                    "Error: Incorrect Initialization Input Format: "
                    + "Must be of the format: Flag, Interface, IP Address, Cost"
                    + "\nPlease submit input again."
                    );
                continue;
            }

            // TODO Handle NumberFormatException for all parseInt()
            flag = Integer.parseInt(parts[0]);
            interf = Integer.parseInt(parts[1]);
            cost = Integer.parseInt(parts[4]);
            ip1 = parts[2];
            ip2 = parts[3];
            ip1Parts = parts[2].split(".");
            ip2Parts = parts[3].split(".");

            if (ip1Parts.length != 4 || ip2Parts.length != 4) {
                System.err.println(
                    "Error: Incorrect Initialization Input Format: "
                    + "IP addresses must be of the format: #.#.#.# \n"
                    + "where # is an integer value in the range [0,255]"
                    + "\nPlease submit input again."
                    );
                continue;
            }

            ipAddress1[0] = Integer.parseInt(ip1Parts[0]);
            ipAddress1[1] = Integer.parseInt(ip1Parts[1]);
            ipAddress1[2] = Integer.parseInt(ip1Parts[2]);
            ipAddress1[3] = Integer.parseInt(ip1Parts[3]);

            ipAddress2[0] = Integer.parseInt(ip1Parts[0]);
            ipAddress2[1] = Integer.parseInt(ip1Parts[1]);
            ipAddress2[2] = Integer.parseInt(ip1Parts[2]);
            ipAddress2[3] = Integer.parseInt(ip1Parts[3]);

            if (flag != 0 || flag != 1) {
                System.err.println("Error: Incorrect Initialize Flag Value: "
                    + "Flag value in input must be 0 or 1"
                    );
                continue;
            } else if (interf < 0) {
                System.err.println(
                    "Error: Incorrect Initialize Interface Value: "
                    + "Interface must be a unique positive integer"
                    + "\nPlease submit input again."
                    );
                continue;
            } else if ((ipAddress1[0] < 0 && ipAddress1[0] > 255)
                    || (ipAddress1[1] < 0 && ipAddress1[1] > 255)
                    || (ipAddress1[2] < 0 && ipAddress1[2] > 255)
                    || (ipAddress1[3] < 0 && ipAddress1[3] > 255)
                    || (ipAddress2[0] < 0 && ipAddress2[0] > 255)
                    || (ipAddress2[1] < 0 && ipAddress2[1] > 255)
                    || (ipAddress2[2] < 0 && ipAddress2[2] > 255)
                    || (ipAddress2[3] < 0 && ipAddress2[3] > 255)
                    ){
                System.err.println(
                    "Error: Incorrect Initialization Input Format: "
                    + "IP address must be of the format: #.#.#.# \n"
                    + "where # is an integer value in the range [0,255]"
                    + "\nPlease submit input again."
                    );
                continue;
            } else if (cost < 0){
                System.err.println("Error: Incorrect Cost Value: "
                    + "Cost must be apositive integer value."
                    + "\nPlease submit input again."
                    );
                continue;
            }

            /*
             * Check if interf exists, otherwise this was recieved from unknown
             * router
             */
            if (!usedInterf.contains(interf)){
                System.err.println("Error: Unknown Interface Used: "
                    + "Interface value entered is not known to router from "
                    + "initialization. Thus an unknown neighbor exists, but "
                    + "unable to add new router as neighbor due to lack of "
                    + "certainty of the intermediate router/network's IP address"
                    + "\nPlease submit input again."
                    );
                continue;
            }

            // Do something with this input ???? 0 if Advert, 1 if datagram
            if (flag == 0){
                handleAdvert(interf, ip1, ip2, cost);
            } else {
                handleDatagram(interf, ip1, ip2);
            }

        }
    }

    /**
     * Inform peer router on interface about updated links between two ips
     */
    public void advertiseLink(int interf, String ip1, String ip2, int cost){
        System.out.println("0," + interf + "," + ip1 + "," + ip2 + "," + cost );
    }

    /**
     * Inform all peer routers of updated links between neighbors; inform about
     * both routers and networks
     */
    public void peerInform(){
        Node m;
        for (Node n : router.neighbors.keySet()){
            if (n.flag == 0){ // only informs peer Routers, not Networks
                continue;
            }
            for (Map.Entry<Node, Integer> entryM : router.neighbors.entrySet()){
                m = entryM.getKey();
                if ((n.ip).equals(m.ip)){
                    continue;
                }
                advertiseLink(n.interf, router.ip, m.ip,
                    entryM.getValue().intValue());
            }
        }
    }

    /**
     * Returns the next router/node in route to destination.
     * Travels towards the root router from destination.
     *
     * @param current the current router in route from root to destination
     */
    public Node nextRouter(Node current){
        if (current.prev.ip == router.ip){
            return current;
        }
        return nextRouter(current.prev);
    }

    /**
     * Handler for advertisement of link update to propagte the network
     */
    public void handleAdvert(int interf, String ip1, String ip2,
            int cost){
        Node node1 = fwdTable.get(ip1), node2 = fwdTable.get(ip2);

        if (node1 == null){ // add node to graph. redo DijkstraSP
            node1 = new Node(-2, -2, ip1, node2, cost);
            node2.addNeighbor(node1, cost);
            fwdTable.put(ip1, node1);
        } else if (node2 == null) {
            // impossible for two unknowns, given propagation of advertisements.
            node2 = new Node(-2, -2, ip1, node1, cost);
            node1.addNeighbor(node2, cost);
            fwdTable.put(ip2, node2);
        } else { // Both nodes known to router & in fwdTable, update cost
            // If link between nodes exists, update cost

            // if no link exists, create link between both

            //node1
        }

        dijkstra(router, fwdTable);

        System.out.println("0," + interf + "," + ip1 + "," + ip2 + "," + cost);
    }

    /**
     * Passes the datagram to the best next router/node in route to destination.
     * If the destination cannot be reached, then prints out error message
     */
    public void handleDatagram(int interf, String ip1, String ip2){
        Node dest = fwdTable.get(ip2);
        if (dest == null){
            System.err.println("No path to host: " + ip2);
        } else {
            Node next = nextRouter(dest);
            System.out.println("1," + next.interf + "," + ip1 + "," + ip2 + ",0");
        }
    }

    /*
     * main method for testing purposes.
     */
    public static void main(String[] args){
        // set up first node as this router
        LinkStateRouter router = new LinkStateRouter();

        router.initialize();
        // Inform neighboring Peer Routers of all other links
        router.simulation();
    }
}
