import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;

// The Swing Environment for key listeners & maximum portability:

/**
 * Two player versus Spaceship Shooter in console. In the game you play as a
 * ship at the bottom of an 80 col by 80 row board and shoot at the opponent
 * on the opposite side of the board, as well as dodge their missiles.
 * 
 * This class defines the game.
 * 
 * @author Derek S. Prijatelj
 */
public class Game{
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
            y += (direction) ? -1 : 1;
        }

        protected Missile missileAhead(ArrayList<Missile> ms){
            for (Missile m : ms){
                if ((m.x == x && m.y == y-1
                    && direction && direction != m.direction)
                    || (m.x == x && m.y == y+1
                    && !direction && direction != m.direction))
                    return m;
            }
            return null;
        }
    }

    protected class Player extends Sprite{
        private static final int MAX_AMMO = 5;
        private static final long RELOAD_TIME = (long)15000000000L; // 30 secs
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
            if (ammoTimer.size() < MAX_AMMO && missiles.size() < MAX_AMMO*2){
                ammoTimer.add(System.nanoTime());

                if (id == 1)
                    return new Missile('^', x, y, 1, true);
                else
                    return new Missile('v', x, y, 1, false);
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
            else if (in == 'd' && x < newBoard[0].length-1)
                x++;
            else if (in == 's'){
                return shoot();
            } else if (in == 'x'){
                // Character forfeits, self-destruct
                sprite = 'X';
            }
            return null;
        }
    }

    
    //private int[][] board;
    protected char[][] newBoard, currentBoard;
    private Player p1, p2;
    private int hp;
    private ArrayList <Missile> missiles = new ArrayList<>();

    public Game(int row, int col, int hp){
        createBoard(row, col);
        this.hp = hp;
    }

    public Game(int row, int col, int hp, char p1Sprite){
        this(row, col, hp);
        this.p1 = new Player(p1Sprite, col/2, row-1, hp, 1);
    }

    public Game(int row, int col, int hp, char p1Sprite, char p2Sprite){
        this(row, col, hp);
        this.p1 = new Player(p1Sprite, col/2, row-1, hp, 1);
        this.p2 = new Player(p2Sprite, col/2, 0, hp, 2);
    }

    private void createBoard(int row, int col){
        newBoard = new char[row][col];
        for (int i = 0; i < row; i++)
            newBoard[i] = String.format("%1$" + col + "s"," ").toCharArray();

        currentBoard = new char[row][col];
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
        
        delete = new HashSet<>();
        for (Missile m : missiles){
            m.update();
            if (m.y < 0 || m.y >= newBoard.length)
                delete.add(m);
        }
        missiles.removeAll(delete);

        for (int i = 0; i < missiles.size(); i++){
            for (int j = 0; j < missiles.size(); j++){
                if (i != j && missiles.get(i).isHit(missiles.get(j))){
                    missiles.get(i).sprite = '%';
                    break;
                }
            }
            if (p1.isHit(missiles.get(i)) || p2.isHit(missiles.get(i))){
                missiles.get(i).sprite = '%';
            }
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
        for (int i = 0; i < currentBoard.length; i++)
            currentBoard[i] = Arrays.copyOf(newBoard[i], newBoard[i].length);
        
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

        score += "Ammo:  " + p1.ammoCheck()
            + String.format("%1$" + (newBoard[0].length-7) + "s", p2.ammoCheck()
            + "  :Ammo\n");
        
        score += ("p1(x,y) = (" + p1.x + "," + p1.y + ")")
            + ("  p2(x,y) = (" + p2.x + "," + p2.y + ")")
            + "   board dim: " + newBoard.length + "," + newBoard[0].length + "\n";
        return score;// + line();
    }

    /**
     * Returns String representation of current board & game state
     */
    private boolean render(char p1Input, char p2Input){
        boolean gameOver = updateSprites(p1Input, p2Input);
        setSprites();

        String screen = line();

        for (int i = 0; i < currentBoard.length; i++){
            for (int j = 0; j < currentBoard[i].length; j++)
                screen += currentBoard[i][j];
            screen += "\n";
        }
        
        screen += scoreBoard();
        
        if (gameOver){
            if (p1.sprite == 'X' && p2.sprite == 'X'){
                screen += "Game Over: Tie Game!\n";
            } else if (p1.sprite == 'X'){
                screen += "Game Over: Player " + p2.id + " Wins!\n";
            } else if (p2.sprite == 'X'){
                screen += "Game Over: Player " + p1.id + " Wins!\n";
            }
        }

        //*  used for in terminal testing.
        output(screen);
        return !gameOver; 
        //*/
        
        //return screen; // Used for implementation with swing
    }

    //reset back to top of screen and output new screen
    // Old code used for gameplay in actual terminal. Superceded by Swing
    protected void output(String screen){
        //*
        for (int i = 0; i < newBoard.length+7; i++){
            System.out.print("\033[1A" +
                String.format("%" + newBoard[0].length + "s", "") + "\r");
        }
        //*/

        // may only work in linux if it even works...
        // \033[1A : reset up previous line
        // \033[2k : apparently erases line... i doubt it.
        /*
        System.out.print("\033[" + (newBoard.length+7) + "A" + screen +
            String.format("%" + newBoard[0].length + "s", "") + "\r");
        */
        System.out.print(screen
            + String.format("%" + newBoard[0].length + "s", "") + "\r");
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
        //Game game = new Game(40, 80, 3, 'A', '1');
        Game game = new Game(20, 40, 3, 'A');
        
        Scanner sc = new Scanner(System.in);
        String in; 
        
        game.standby(true);
        sc.nextLine();

        game.addPlayer('V', false);
        
        game.render('o','o');
        do {
            in = sc.nextLine();
        } while (game.render(in.charAt(0), in.charAt(1)));
    }
 }
