import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ClosedChannelException;
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
    final float WIDTH = 400, HEIGHT = 475;
    private static Game game; // client-side game
    private static Character p1In = '0', p2In = '0', in = '0';
    private static String gameRender;
    private static final int frameDiv = 15; // divides framerate by this amount
    private static int frameCount = 0;
    
    private static ByteBuffer buf = ByteBuffer.allocate(16);
    private static SocketChannel client;
    private static long bread = 0;
    private static boolean gameOver = false, forfeit = false;
    
    @Override
    public void start(Stage stage) throws Exception{
        if (frameCount % frameDiv == 0){
            frameCount = 0;
        }
        frameCount++;

        VBox body = new VBox();
        body.setAlignment(Pos.CENTER);
        body.styleProperty().bind(Bindings.concat("-fx-font-size: 18px;"
            + "-fx-font-family: Monospace;"
            + "-fx-background-color: #000000;"
            + "-fx-alignment: CENTER;"
        ));

        new AnimationTimer(){
            public void handle(long currentTime){
                body.getChildren().clear();
                input(in);
                try{
                    gameRender = game.render(p1In, p2In);
                } catch(ArrayIndexOutOfBoundsException e){
                    e.printStackTrace();
                    terminate();
                }
                
                if (gameRender.endsWith("Win!")
                        || gameRender.endsWith("Wins!")){
                    stop();
                    System.out.println(gameRender.substring(
                        gameRender.lastIndexOf("\n") + 1));
                    gameOver = true;
                    try{
                        client.close();
                    } catch (IOException e){
                        e.printStackTrace();
                        terminate();
                    }
                    if (forfeit) {
                        terminate();
                    }
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
        if (in != '0') {
            in = '0';
        }
        if (p1In != '0' || p2In != '0'){
            p1In = '0';
            p2In = '0';
        }
    }

    /**
     * Passes the client input to server & readies to update client board with
     * possible input from server. Server controls the pace of the game.
     *
     * @param in character relating to the user's input
     */
    private void input(char in){
        try{
            if (frameCount == 1){
                // write to server
                buf.clear();
                buf.putChar(in);
                buf.flip();
                while(buf.hasRemaining())
                    client.write(buf);
                
                // read from server.
                buf.clear();
                buf.putChar('0');
                buf.putChar('0');
                buf.clear();
                bread = client.read(buf);

                //System.out.println("bytes =" + bread);

                if (game.p2 == null){
                    if(buf.getChar(0) == 'p' && buf.getChar(2) == '2'){
                        game.addPlayer('V', false);
                    }
                } else {
                    p1In = buf.getChar(0);
                    p2In = buf.getChar(2);
                }
            }
        } catch (IOException e){
            e.printStackTrace();
            terminate();
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
                    in = 'x';
                    if (gameOver){
                        terminate();
                    } else {
                        forfeit = true;
                    }
                } else if (key.getCode() == KeyCode.A) {
                    in = 'a';
                } else if (key.getCode() == KeyCode.S) {
                    in = 's';
                } else if (key.getCode() == KeyCode.D) {
                    in = 'd';
                } else if (key.getCode() == KeyCode.X) {
                    in = 'x';
                }
                /*
                else if (key.getCode() == KeyCode.W) {
                    game.addPlayer('V', false);
                }
                */
                key.consume();
            }
        });
    }

    private static void terminate(){
        try{
            if (gameOver){
                // write to server
                buf.clear();
                buf.putChar('x');
                buf.flip();
                while(buf.hasRemaining())
                    client.write(buf);
            }
            client.close();
        } catch(ClosedChannelException e){
            // ensuring its closed is all.
        } catch (IOException e){
            e.printStackTrace();
        }
        Platform.exit();
    }

    public static void main(String args[]){
        if (args.length != 2){
            System.err.println("Usage: java Client <host name> <port number>");
            System.exit(1);
        }

        String hostName = args[0];
        int port = Integer.parseInt(args[1]);
        int boardw, boardh, hp, id;

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
                
                boardh = buf.getInt(0);
                boardw = buf.getInt(4);
                hp = buf.getInt(8);
                id = buf.getInt(12);
                
                System.out.println(boardh);
                System.out.println(boardw);
                System.out.println(hp);
                System.out.println(id);

                if(id == 1){
        System.out.println("is play 1");
                    // client is player 1
                    game = new Game(boardh, boardw, hp, 'A');
                } else if (id == 2){
        System.out.println("is play 2");
                    // client is player 2
                    game = new Game(boardh, boardw, hp, 'A', 'V');
                } else {
                    System.err.println("Error: Invalid player id: " + id);
                    System.exit(1);
                }

                System.out.println("Made it to Application");

                client.configureBlocking(false); // is this a problem?
                
                buf = ByteBuffer.allocate(4);

                Application.launch(Client.class, args);
            }
            client.close();
        } catch(ConnectException e){
            System.err.println("Cannot connect to host '" + hostName 
                + "' at port " + port);
            System.exit(1);
        } catch(Exception e){
            e.printStackTrace();
            System.exit(1);
        }
    }
}
