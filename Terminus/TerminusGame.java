import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

/**
 * Two player versus Spaceship Shooter in console. In the game you play as a
 * ship at the bottom of an 80 col by 80 row board and shoot at the opponent
 * on the opposite side of the board, as well as dodge their missiles.
 * 
 * This class defines the game.
 * 
 * @author Derek S. Prijatelj
 */
 public class TerminusGame{
    private static class Sprite{
        protected int x, y, hp;
        protected char sprite; // serves as unique id
        
        protected boolean isAlive(){
            return hp > 0;
        }

        protected boolean isHit(Missile m){
            if (x == m.x && y == m.y){
                hp -= m.dmg;
                return true;
            }
            return false;
        }
    }

    private static class Missile extends Sprite{
        private boolean direction; // true = up
        private int dmg = 1;
        private int speed = 1;

        public Missile(char sprite, int x, int y, int hp, boolean direction){
            //Sprite(sprite, x, y, hp);
            this.sprite = sprite;
            this.x = x;
            this.y = y;
            this.hp = hp;
            this.direction = direction;
        }

        protected void update(){
            y += (direction) ? 1 : -1;
        }

        protected Missile missileAhead(ArrayList<Missile> ms){
            for (Missile m : ms){
                if ((m.y == y+1 && direction && direction != m.direction)
                    || (m.y == y-1 && !direction && direction != m.direction))
                    return m;
            }
            return null;
        }
    }

    protected class Player extends Sprite{
        private static final int MAX_AMMO = 5;
        private static final long RELOAD_TIME = (long)30000000000L; // 30 secs
        private ArrayList <Long> ammoTimer = new ArrayList<>();
        protected int id;

        public Player(char sprite, int x, int y, int hp, int id){
            //Sprite(sprite, x, y, hp);
            this.sprite = sprite;
            this.x = x;
            this.y = y;
            this.hp = hp;
            this.id = id;
        }

        protected int ammo(){
            return MAX_AMMO - ammoTimer.size();
        }

        private int ammoCheck(){
            Iterator<Long> i = ammoTimer.iterator();
            Long l;

            while(i.hasNext()){
                l = i.next();
                if (System.nanoTime() - l >= RELOAD_TIME)
                    i.remove();// does this still not skip?
            }
            return ammo();
        }

        protected Missile shoot(){
            if (ammoTimer.size() <= MAX_AMMO){
                ammoTimer.add(System.nanoTime());

                if (id == 'A')
                    return new Missile('^', x, y+1, 1, true);
                else
                    return new Missile('v', x, y-1, 1, false);
            }
            return null;
        }

        /**
         * Listens if player moves or shoots, updates per frame
         * l = left, r = right, s = shoot, e = exit
         */
        protected Missile input(char in){
            if (in == 'a' && x > 0)
                x--;
            else if (in == 'd' && x < newBoard.length)
                x++;
            else if (in == ' '){
                return shoot();
            } else if (in == 'e'){
                // Character forfeits, self-destruct
                sprite = 'X';
            }
            return null;
        }
    }

    
    //private int[][] board;
    private char[][] newBoard, currentBoard;
    private Player p1, p2;
    private int hp;
    private ArrayList <Missile> missiles = new ArrayList<>();

    public TerminusGame(int row, int col, int hp){
        createBoard(row, col);
        this.hp = hp;
    }

    public TerminusGame(int row, int col, int hp, char p1Sprite){
        this(row, col, hp);
        this.p1 = new Player(p1Sprite, col/2, row-1, hp, 1);
    }

    public TerminusGame(int row, int col, int hp, char p1Sprite, char p2Sprite){
        this(row, col, hp);
        this.p1 = new Player(p1Sprite, col/2, row-1, hp, 1);
        this.p2 = new Player(p2Sprite, col/2, 0, hp, 2);
    }

    private void createBoard(int row, int col){
        newBoard = new char[row][col];
        for (int i = 0; i < row-1; i++)
            newBoard[i] = String.format("%1$" + col + "s"," ").toCharArray();
    }

    protected void addPlayer(char sprite, boolean isPlayer1){
        if (isPlayer1)
            p1 = new Player(sprite, newBoard[0].length/2, newBoard.length-1, hp, 1);
        else
            p2 = new Player(sprite, newBoard[0].length/2, 0, hp, 2);
    }

    protected boolean updateSprites(char p1Input, char p2Input){
        Missile tmp;
        boolean gameOver = false;
       
        tmp = p1.input(p1Input);
        if (tmp != null)
            missiles.add(tmp);
    
        tmp = p2.input(p2Input);
        if (tmp != null)
            missiles.add(tmp);

        HashSet<Missile> delete = new HashSet<>();

        // remove destroyed missiles
        for (Missile m : missiles){
            if (m.sprite == '%')
                delete.add(m);
        }
        missiles.removeAll(delete);

        // Check if missiles become destroyed
        for (Missile m : missiles){
            tmp = m.missileAhead(missiles);
            if (tmp != null)
                tmp.sprite = '%';
        }
        

        for (Missile m : missiles){
            m.update();
        }
        for (Missile m : missiles){
            if (m.isHit(m))
                m.sprite = '%';
            p1.isHit(m);
            p2.isHit(m);
        }

        if (!p1.isAlive()){
            p1.sprite = 'X';
        }
        if (!p2.isAlive()){
            p2.sprite = 'X';
        }

        if (p1.sprite == 'X' || p2.sprite == 'X')
            gameOver = true;
        
        return gameOver;
    }

    /**
     * Places active sprites on empty board, assumes update of sprites complete.
     */
    private void setSprites(){
        currentBoard = Arrays.copyOf(newBoard, newBoard.length);
        
        currentBoard[p1.y][p1.x] = p1.sprite;
        currentBoard[p2.y][p2.x] = p2.sprite;

        for (Missile m : missiles)
            currentBoard[m.y][m.x] = m.sprite;
    }

    private String scoreBoard(){
        String score = line();

        score += "Player 1"
            +String.format("%1$" + (newBoard[0].length-7) + "s", "Player 2\n");
        
        score += "HP:    " + p1.hp
            + String.format("%1$" + (newBoard[0].length-7) + "s", p2.hp
            + "    :HP\n");

        score += "Ammo:  " + p1.ammo()
            + String.format("%1$" + (newBoard[0].length-7) + "s", p2.ammo()
            + "  :Ammo\n");

        return score + line();
    }

    /**
     * Returns String representation of current board & game state
     */
    private boolean render(char p1Input, char p2Input){
        boolean gameOver = updateSprites(p1Input, p2Input);
        setSprites();

        String screen = line();

        for (int i = 0; i < currentBoard.length; i++)
            screen += String.valueOf(currentBoard[i]) + "\n";
        
        screen += scoreBoard();
        
        if (gameOver){
            if (p1.sprite == 'X' && p2.sprite == 'X'){
                screen += "Game Over: Tie Game!\n";
            } else if (p1.sprite == 'X'){
                screen += "Game Over: Player " + p1.id + " Wins!\n";
            } else if (p2.sprite == 'X'){
                screen += "Game Over: Player " + p2.id + " Wins!\n";
            }
        }

        output(screen);

        return !gameOver;
    }

    protected void output(String screen){
        // System.out.print(renderScreen(p1Input, p2Input));
        // need to consistently overwrite the previous output!!!
        // renderScreen could be only render and bool if gameover. So output
        // @ end of renderScreen after making screen and return gameOver;
        
        //reset back to top of screen
        System.out.printf("%" + newBoard[0].length + "s","\33[1a\r");
        for (int i = 0; i < newBoard.length+6; i++){
            System.out.print("\33[2k\33[1A\33[2k"); // may only work in linux if it even works...
        }

        System.out.print(screen);
    }

    /**
     * Standby Screen, waiting for other player to join
     */
    protected void standby(boolean isPlayer1){
        String screen = line();

        for (int row = 0; row < newBoard.length; row++ ){
            if (row == newBoard.length/2){
                screen += String.format("%1$"+ ((newBoard[0].length/2) - 5)
                    + "s","Waiting for Opponent");
            }
            screen += "\n";
        }
        
        screen += line();
        
        screen += "\nYou are Player " + ((isPlayer1) ? "1" : "2") + "\n\n";

        // output screen
        System.out.print(screen+line());
    }

    private String line(){
        String str = new String();

        for (int col = 0; col < newBoard[0].length; col++){
            str += "-";
        }

        return str + "\n";
    }

    public static void main(String args[]){
        // create board, & 1 player
        TerminusGame game = new TerminusGame(40, 80, 3, 'A', '1');
        
        Scanner sc = new Scanner(System.in);
        String in; 
        
        game.standby(true);
        sc.nextLine();

        game.addPlayer('V', false);
        
        game.render('o','o');
        do {
            in = sc.nextLine();
        } while (game.render(in.charAt(0), in.charAt(1)));

        // add player 2

        // test input and render
    }
 }
