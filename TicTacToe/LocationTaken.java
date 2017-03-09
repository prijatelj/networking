import java.io.IOException;

public class LocationTaken extends Exception{
    public LocationTaken () {}

    public LocationTaken (String message){
        super (message);
    }   

    public LocationTaken (Throwable cause){
        super (cause);
    }   

    public LocationTaken (String message, Throwable cause){
        super (message, cause);
    }   
}
