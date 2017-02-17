import java.io.IOException;

public class PlayerDNE extends Exception{
    public PlayerDNE () {}

    public PlayerDNE (String message){
        super (message);
    }   

    public PlayerDNE (Throwable cause){
        super (cause);
    }   

    public PlayerDNE (String message, Throwable cause){
        super (message, cause);
    }   
}
