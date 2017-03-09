import java.io.IOException;

public class CoordinatesDNE extends Exception{
    public CoordinatesDNE () {}

    public CoordinatesDNE (String message){
        super (message);
    }   

    public CoordinatesDNE (Throwable cause){
        super (cause);
    }   

    public CoordinatesDNE (String message, Throwable cause){
        super (message, cause);
    }   
}
