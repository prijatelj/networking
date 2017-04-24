import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Math;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.channels.spi.SelectorProvider;

/**
 * Dedicated server that hosts the Terminus game between two clients. 
 *
 * @author Derek S. Prijatelj
 */
public class Server{
    public static void main(String[] args){
        if (args.length != 1){
            System.err.println("Usage: java Server <port number>");
            System.exit(1);
        }

        final int port = Integer.parseInt(args[0]);
        // Cannot be > 255, limited by byte size
        final int boardWidth = 30, boardHeight = 15, hp = 3;
        long bytes1 = 0, bytes2 = 0;
       
        try{
            final ServerSocketChannel serv = ServerSocketChannel.open();
            serv.socket().bind(new InetSocketAddress(port));
            serv.configureBlocking(true);
            
            // establish game settings
            Game game = new Game(boardHeight, boardWidth, hp);

        System.out.println("Game board made,");

            // Need to handle if player 1 leaves while in queue.
            SocketChannel player1 = serv.accept();
            ByteBuffer buf1 = ByteBuffer.allocate(16);
        
        System.out.println("Player 1 connected, bufs made");

            buf1.putInt(boardHeight);
            buf1.putInt(boardWidth);
            buf1.putInt(hp);
            buf1.putInt(1);

            buf1.flip();
        
        System.out.println("Bufs 1 filled");
            
            while(buf1.hasRemaining())
                bytes1 = player1.write(buf1);
        
        System.out.println("player1 written to. Bytes = " + bytes1);

            buf1.clear();
            //player1.read(buf1); // may block and may be problem.
            
            game.addPlayer('A', true);
            
            // Get 2nd Player
            SocketChannel player2 = serv.accept();
            ByteBuffer buf2 = ByteBuffer.allocate(16);

        System.out.println("Player 2 connected, bufs made");

            buf2.putInt(boardHeight);
            buf2.putInt(boardWidth);
            buf2.putInt(hp);
            buf2.putInt(2);

            buf2.flip();

        System.out.println("Bufs 2 filled");

            while(buf2.hasRemaining()) 
                bytes2 = player2.write(buf2);

        System.out.println("player2 written to. Bytes = " + bytes2);

            buf2.clear();
            //player2.read(buf2); // may block and may be problem.

            game.addPlayer('V', false);
            
            buf1 = ByteBuffer.allocate(4);
            buf2 = ByteBuffer.allocate(4);
            
            // inform player1 of player2's joining
            buf1.putChar('p');
            buf1.putChar('2');
            buf1.flip();
            while(buf1.hasRemaining()) 
                player1.write(buf1);
            buf1.clear();

            // begin game loop
            //long turnTime = 250000000L, lastTime = System.nanoTime();
            long turnTime = 20000000L, lastTime = System.nanoTime();
            boolean inPlay = true;
            String gameRender = "";
            Character p1In, p2In;
            player1.configureBlocking(false);
            player2.configureBlocking(false);
            

        System.out.println("Game begin:");
            
            bytes1 = 0;
            bytes2 = 0;

            Thread.sleep(1000); // Wait for the clients to create thier games

            while (inPlay){
                bytes1 += player1.read(buf1);
                buf1.position(0);

                bytes2 += player2.read(buf2);
                buf2.position(0);

                // accept input from both players if any (non-blocking)
                if (System.nanoTime() - lastTime > turnTime){
        //System.out.println("bytes1 = " + bytes1 + ", bytes2 = " + bytes2);
                    
                    if (bytes1 <= 0){ // No activity, therefore lost connection
                        p1In = 'x';
                    } else {
                        bytes1 = 0;
                        p1In = buf1.getChar(0);
                    }
                    if (bytes2 <= 0 ){ // No activity, therefore lost connection
                        p2In = 'x';
                    } else {
                        bytes2 = 0;
                        p2In = buf2.getChar(0);
                    }

                    buf1.clear();
                    buf2.clear();

                    buf1.putChar(p1In.charValue());
                    buf1.putChar(p2In.charValue());
                    
                    buf2.putChar(p2In.charValue());
                    buf2.putChar(p1In.charValue());

                    buf1.flip();
                    buf2.flip();

                    while(buf1.hasRemaining())
                        player1.write(buf1);
                    while(buf2.hasRemaining())
                        player2.write(buf2);
                    
                    buf1.clear();
                    buf2.clear();
                    gameRender = game.render(p1In, p2In);
                    inPlay = !(gameRender.endsWith("Win!")
                        || gameRender.endsWith("Wins!"));
                    lastTime = System.nanoTime();
                }
            }

            Thread.sleep(1000);

            player1.close();
            player2.close();
            serv.close();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
