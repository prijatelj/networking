package router;

import java.util.Scanner;
import java.util.HashSet;

public class LinkStateRouter{

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

    public static void main(String[] args){
        // set up first node as this router
        Node router = new Node(0, new int[]{127,0,0,1});
        
        // Initialize
        Scanner sc = new Scanner(System.in);
        String line = sc.nextLine();
        String[] parts;
        int flag, interf, cost;
        int[] ipAddress1 = new int[4];
        int[] ipAddress2 = new int[4];
        HashSet<Integer> usedInterf = new HashSet <>();

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
            
            ipAddress1[0] = Integer.parseInt(parts[0]);
            ipAddress1[1] = Integer.parseInt(parts[1]);
            ipAddress1[2] = Integer.parseInt(parts[2]);
            ipAddress1[3] = Integer.parseInt(parts[3]);

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
            } else if (ipAddress1[0] < 0 && ipAddress1[0] > 255
                    && ipAddress1[1] < 0 && ipAddress1[1] > 255
                    && ipAddress1[2] < 0 && ipAddress1[2] > 255
                    && ipAddress1[3] < 0 && ipAddress1[3] > 255
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

            
            router.addNeighbor(cost, new Node(flag, interf, ipAddress1));
        }

        // Router Simulation: Receiving Packets & Info
        while (sc.hasNextLine()) {
            line = sc.nextLine();
        
        }
    }
}
