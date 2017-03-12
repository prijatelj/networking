package router;

import java.util.Scanner;
import java.util.HashSet;
import java.util.HashMap;

/**
 * Simulates a Link State Router
 * @author Derek S. Prijatelj
 */
public class LinkStateRouter{
    // IP Address Interface Pair
    public HashMap<String, Node> forwardingTable = new HashMap<>();
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
            
            Node newNode = new Node(flag, interf, ip);
            router.addNeighbor(cost, newNode);
            forwardingTable.put(ip, newNode);
        }

        // Inform all Peer routers of other neighbors
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

            // Do something with this input ???? 0 if Advertisement, 1 if datagram
            

        }
    }
    
    /**
     * Inform all peer routers of updated links between neighbors
     */
    public void peerInfrom(){
        for (Edge n : router.neighbors){
            for (Edge m : router.neighbors){
                if (n.to.ip.equals(m.to.ip)){
                    continue;
                }
                advertiseLink(n.to.interf, router.ip, m.to.ip, m.cost);
            }
        }
    }
    /**
     * Inform peer router on interface about updated links between two ips
     */
    public void advertiseLink(int interf, String ip1, String ip2, int cost){
        System.out.println("0," + interf + "," + ip1 + "," + ip2 + "," + cost );
    }

    public static void main(String[] args){
        // set up first node as this router
        LinkStateRouter router = new LinkStateRouter();

        router.initialize();
        // Inform neighboring Peer Routers of all other links
        router.simulation();
    }
}
