import java.io.IOException;
import java.lang.ArrayIndexOutOfBoundsException;
import java.lang.Math;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.animation.AnimationTimer;
import javafx.beans.binding.Bindings;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;

/**
 * Client side program for Terminus Game. Sets up game for client, reads input,
 * updates and recieves updates server on game state via TCP connection.
 * 
 * @author Derek S. Prijatelj
 */
public class Client extends Application{
    final float WIDTH = 600, HEIGHT = 600;
    private static Game game = new Game(10, 30, 3, 'A'); // client-side game
    private static Character p1In = '0', p2In = '0';
    private static String gameRender;
    
    private static ByteBuffer buf = ByteBuffer.allocate(4);
    private static SocketChannel client;
    
    @Override
    public void start(Stage stage) throws Exception{
        VBox body = new VBox();
        body.setAlignment(Pos.CENTER);
        body.styleProperty().bind(Bindings.concat("-fx-font-size: 18px;"
            + "-fx-font-family: Monospace;"
            + "-fx-background-color: #000000;"
            + "-fx-alignment: CENTER;"
        ));

        // TODO Figure out how to get this the game String correctly & allow
        //  For proper updates via server's input.
        /*
        Text gameText = new Text(game.render(p1In, p2In));
        gameText.setFill(Color.WHITE);
        body.getChildren().addAll(gameText);
        resetInput();
        */ 
        new AnimationTimer(){
            public void handle(long currentTime){
                body.getChildren().clear();
                try{
                    gameRender = game.render(p1In, p2In);
                } catch(ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                    terminate();
                }
                
                if (gameRender.endsWith("Wins!")){
                    stop();
                    System.out.println(gameRender.substring(
                        gameRender.lastIndexOf("\n") + 1));
                }
                
                Text gameText = new Text(gameRender);
                gameText.setFill(Color.WHITE);
                body.getChildren().addAll(gameText);
                resetInput();
            }
        }.start();

        StackPane root = new StackPane();
        root.setMinWidth(WIDTH);
        root.setMinHeight(HEIGHT);
        
        root.setPrefWidth(WIDTH);
        root.setPrefHeight(HEIGHT);
        
        root.setMaxWidth(WIDTH);
        root.setMaxHeight(HEIGHT);

        root.getChildren().addAll(body);
        
        stage.setTitle("Terminus");
        Scene scene = new Scene(root);
        inputKeys(scene);
        stage.setScene(scene);
        stage.show();
    }

    private void resetInput(){
        p1In = '0';
        p2In = '0';
    }

    /**
     * Passes the client input to server & readies to update client board with
     * possible input from server. Server controls the pace of the game.
     *
     * @param in character relating to the user's input
     */
    private void input(char in){
        try{
            // write to server
            buf.clear();
            buf.putChar(in);
            buf.flip();
            while(buf.hasRemaining())
                client.write(buf);
            
            // read from server.
            buf.clear();
            client.read(buf);

            p1In = buf.getChar();
            p2In = buf.getChar();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Listens to keyboard input and handles it
     * @param scene being listened to
     */
    private void inputKeys(Scene scene){
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            public void handle(KeyEvent key){
                if (key.getCode() == KeyCode.ESCAPE){
                    key.consume();
                    terminate();
                } else if (key.getCode() == KeyCode.A) {
                    input('a');
                } else if (key.getCode() == KeyCode.S) {
                    input('s');
                } else if (key.getCode() == KeyCode.D) {
                    input('d');
                } else if (key.getCode() == KeyCode.X) {
                    input('x');
                }
                /*
                else if (key.getCode() == KeyCode.W) {
                    game.addPlayer('V', false);
                }
                */
            }
        });
    }

    private static void terminate(){
        Platform.exit();
    }

    public static void main(String args[]){
        if (args.length != 2){
            System.err.println("Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        int arr[] = new int[4];
        int bread;

        try{
            // Connect to server first
            client = SocketChannel.open();
            client.configureBlocking(true);
            //client.configureBlocking(false);

        System.out.println("before connect");

            client.connect(new InetSocketAddress(hostName, port));
            //client.finishConnect();
        System.out.println("after connect");

            if (client.isConnected()){
        System.out.println("is connect");
                // get game settings data & create game
                bread = client.read(buf);
        
        System.out.println("done reading setup. Bytes read: " + bread);
                
                buf.getInt(arr[0]);
                buf.getInt(arr[1]);
                buf.getInt(arr[2]);
                buf.getInt(arr[3]);
                
                System.out.println(arr[0]);
                System.out.println(arr[1]);
                System.out.println(arr[2]);
                System.out.println(arr[3]);

                if(arr[3] == 1){
        System.out.println("is play 1");
                    // client is player 1
                    game = new Game(arr[0], arr[1], arr[2], 'A');
                } else if (arr[3] == 2){
        System.out.println("is play 2");
                    // client is player 2
                    game = new Game(arr[0], arr[1], arr[2], 'A', 'V');
                } else {
                    System.err.println("Error: Invalid player id: " + arr[3]);
                    System.exit(1);
                }

                System.out.println("Made it to Application");

                //client.configureBlocking(false); // is this a problem?

                Application.launch(Client.class, args);
            }
            client.close();
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
