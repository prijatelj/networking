/**
 * Two player TicTacToe Game code. Rules and saved state here.
 * @author Derek S. Prijatelj
 */

public class TicTacToeGame{
    private static final int TOP_LEFT = 1;
    private static final int TOP = 2;
    private static final int TOP_RIGHT = 3;
    private static final int LEFT = 4;
    private static final int RIGHT = 6;
    private static final int BOT_LEFT = 7;
    private static final int BOT = 8;
    private static final int BOT_RIGHT = 9;

    public static int turnCount = 0;
    public static char result = '#'; // '#' = ongoing, '0' = tie, or pID

    public static char[][] board = new char[][]{
        {'1', '2', '3'},
        {'4', '5', '6'},
        {'7', '8', '9'}
    };
    
    public static char[] players = new char[]{'X', 'O'};

    public static String print(){
        String str = new String();
        
        for(int i = 0; i < board.length; i++){
            str += board[i][0] + " | " + board[i][1] + " | " + board[i][2]
                + "\n";
            if (i < board.length-1){
                str += "--+---+--\n";
            } else {
                str.concat(System.getProperty("line.separator"));
            }
        }
        return str;
    }

    /**
     * for generalization of game creation for variable number of players
     * and board size.
     * @param int playerCount, int boardSize
     * @return true if turn ended game, false otherwise.
     */
    // could be fun, but not right now.
    public static void init(){
        board =  new char[][]{
            {'1', '2', '3'},
            {'4', '5', '6'},
            {'7', '8', '9'}
        };
    }

    public static boolean turnHandler(int pID, char boardID)
            throws PlayerDNE, CoordinatesDNE, LocationTaken {
        switch(boardID){
            case '1':
                return turn(pID, 0, 0);
            case '2':
                return turn(pID, 0, 1);
            case '3':
                return turn(pID, 0, 2);
            case '4':
                return turn(pID, 1, 0);
            case '5':
                return turn(pID, 1, 1);
            case '6':
                return turn(pID, 1, 2);
            case '7':
                return turn(pID, 2, 0);
            case '8':
                return turn(pID, 2, 1);
            case '9':
                return turn(pID, 2, 2);
            default:
                // Not an available coordinate, throw error.
                return false;
        }
    }

    /**
     * Updates the board based on players play.
     *
     * @param pID player Identification number
     * @param row row of placing token
     * @param col col of placing token
     * 
     * TODO Throw Proper Custom Errors.
     */
    public static boolean turn(int pID, int row, int col)
            throws PlayerDNE, CoordinatesDNE, LocationTaken{


        if (0 <= pID && pID <= players.length){
            if (0 <= row && row < board.length
                    && 0 <= col && col <= board[row].length) {
                if (0 <= board[row][col] && board[row][col] <= '9'){
                    

                    board[row][col] = players[pID];
                    turnCount++;
                    return isGameOver(row, col);
                } else {
                    throw new LocationTaken();
                }
            } else {
                throw new CoordinatesDNE();
            }
        } else {
            throw new PlayerDNE();
        }
    }

    /**
     * Checks if the game is over, either by Victory or Draw.
     */
    public static boolean isGameOver(int row, int col){
        if (isVictory(row, col)){ // win

            System.out.println("isVictory is true.");

            result = board[row][col];
            return true;
        } else if (turnCount >= board.length * board.length){
            result = '0';
            return true; // draw;
        } else {
            return false; // game continues
        }
    }

    /**
     * Checks all angles of the board in a straight line if there exists a line
     * from one edge of the board to another of entirely one player's token,
     * based on the existing token in the residing at the given coordinates.
     * 
     * @param row row to start checking on game board
     * @param col column to start checking on game board
     */
     public static boolean isVictory(int row, int col){
        char token = board[row][col];
        if (0 <= token && token <= '9'){
            return false;
        }
    
        /* FIXME
         * I killed the generalized method because it was inferior. So I forced
         * the diagonals to only accept when both row and col are at extremes.
         * It could be a fun exercise to make an appropriate generalized method
         * of tictactoe that allows multi dimensional board and variable
         * players.
         */
        
        boolean horizontal = isVictoryPath(LEFT, token, row, col)
            && isVictoryPath(RIGHT, token, row, col);

        boolean vertical = isVictoryPath(TOP, token, row, col)
            && isVictoryPath(BOT, token, row, col);
        
        boolean diagonalPos = isVictoryPath(TOP_RIGHT, token, row, col)
            && isVictoryPath(BOT_LEFT, token, row, col);
        
        boolean diagonalNeg = isVictoryPath(TOP_LEFT, token, row, col)
            && isVictoryPath(BOT_RIGHT, token, row, col);

        return horizontal || vertical || diagonalPos || diagonalNeg;
     }

     /**
      * Checks if there exists a straight line from one edge of the board to the
      * opposite edge of one type of token. Can only check a 2 dimensional board
      * 
      * TODO generalize to multi-dimensional game board.
      */
     private static boolean isVictoryPath(int direction, char token, int row,
            int col){
        switch (direction){
            case LEFT:
                if (col == 0){
                    return true;
                } else if (col > 0 && board[row][col-1] == token){
                    return isVictoryPath(LEFT, token, row, col-1);
                } else {
                    return false;
                }
                
            case RIGHT:
                if (col == board[row].length-1){
                    return true;
                } else if (col < board[row].length-1
                        && board[row][col+1] == token){
                    return isVictoryPath(RIGHT, token, row, col+1);
                } else {
                    return false;
                }
                
            case BOT_LEFT:
                if (row == board.length-1 && col == 0){
                    return true;
                } else if (row < board.length-1 && col > 0
                        && board[row+1][col-1] == token){
                    return isVictoryPath(BOT_LEFT, token, row+1, col-1);
                } else {
                    return false;
                }
                
            case TOP_RIGHT:
                if (row == 0 && col == board[row].length-1){
                    return true;
                } else if (row > 0 && col < board[row].length-1
                        && board[row-1][col+1] == token){
                    return isVictoryPath(TOP_RIGHT, token, row-1, col+1);
                } else {
                    return false;
                }
                
            case BOT:
                if (row == board.length-1){
                    return true;
                } else if (row < board.length-1 && board[row+1][col] == token){
                    return isVictoryPath(BOT, token, row+1, col);
                } else {
                    return false;
                }
                
            case TOP:
                if (row == 0){
                    return true;
                } else if (row > 0 && board[row-1][col] == token){
                    return isVictoryPath(TOP, token, row-1, col);
                } else {
                    return false;
                }
                
            case TOP_LEFT:
                if (row == 0 && col == 0){
                    return true;
                } else if (row > 0 && col > 0 && board[row-1][col-1] == token){
                    return isVictoryPath(TOP_LEFT, token, row-1, col-1);
                } else {
                    return false;
                }
                
            case BOT_RIGHT:
                if (row == board.length-1 && col == board[row].length-1){
                    return true;
                } else if (row < board.length-1 && col < board[row].length-1
                        && board[row+1][col+1] == token){
                    return isVictoryPath(BOT_RIGHT, token, row+1, col+1);
                } else {
                    return false;
                }
                
            default:
                return false; // Not a Direction, TODO throw error to inform.
        }
     }
}
