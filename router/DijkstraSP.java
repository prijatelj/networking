package router;

import java.io.IOException;

/**
 *
 * @author Derek S. Prijatelj
 */
public class DijkstraSP{

    public static class Pair{
        public Node n1, n2;
        int cost;
        
        public Pair(Node n1, Node n2, int cost){
            this.n1 = n1;
            this.n2 = n2;
            this.cost = cost;
        }
    }

}
