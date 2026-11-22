
public class Board {

    public static final int SIZEX = 10;
    public static final int SIZEY = 10;

    public static final long INVALID_MOVE = ((long) -1 << 32) | (-1 & 0xffffffffL);

    private static final int[] X = {0, 1, 1, 1, 0, -1, -1, -1};
    private static final int[] Y = {1, 1, 0, -1, -1, -1, 0, 1};

    public static final boolean[][][] configs = { //This is in X, Y
        {{false, true, false}, {true, false, true}, {false, true, true}}, //Boat
        {{false, false, false}, {true, true, false}, {true, true, true}}, //Beehive
        {{false, false, true}, {false, true, true}, {true, true, false}}, //Loaf
        {{false, false, true}, {true, false, true}, {false, true, true}}, //Glider
        {{false, false, true}, {true, true, true}, {false, true, false}}, //R-Pentomino
    };
    public static final String[] configNames = {"Boat", "Beehive", "Loaf", "Glider", "R-Pentomino"};

    public static final String[] p1Skins = {
        new String(Character.toChars(0x1F333)), //🌳 Tree
        new String(Character.toChars(0x1F334)), //🌴 Palm
        new String(Character.toChars(0x1F33F)), //🌿 Plant
        new String(Character.toChars(0x1FABB)), //🪻 Flower
        new String(Character.toChars(0x1F344)), //🍄 Mushroom
        new String(Character.toChars(0x1F43E)), //🐾 Wild Animal
        new String(Character.toChars(0x1F426)), //🐦 Birb
        new String(Character.toChars(0x1F99F)), //🦟 Insect
        new String(Character.toChars(0x1F3D8)), //🏘️ Suburb

    };

    public static final String[] p1Names = {"Tree", "Palm", "Plant", "Flower", "Mushroom", "Wild Animal", "Birb", "Insect", "Suburb"};

    public static final String[] p2Skins = {
        new String(Character.toChars(0x1F332)), //🌲 Pine
        new String(Character.toChars(0x1F335)), //🌵 Cactus
        new String(Character.toChars(0x1F33E)), //🌾 Grass
        new String(Character.toChars(0x1F337)), //🌷 Flower
        new String(Character.toChars(0x1F340)), //🍀 Clover
        new String(Character.toChars(0x1F98E)), //🦎 Reptile
        new String(Character.toChars(0x1F41F)), //🐟 Fih
        new String(Character.toChars(0x1F9A0)), //🦠 Virus
        new String(Character.toChars(0x1F3D9)), //🏙️ City

    };

    public static final String[] p2Names = {"Pine", "Cactus", "Grass", "Flower", "Clover", "Reptile", "Fih", "Virus", "City"};
    
    public static final String invalid = new String(Character.toChars(0x274C)); //❌ X
    //7 0 1
    //6 X 2
    //5 4 3

    protected static enum Cell {
        DEAD, 
        REDawakening,
        RED,
        REDdying,
        BLUEawakening,
        BLUE,
        BLUEdying,
    }
    private Cell[][][] history;
    private Cell[][] board;
    private short generation;
    private final short maxGenerations = 60;

    public Board(){
        generation = 0;
        history = new Cell[maxGenerations + 1][SIZEX][SIZEY];
        board = history[generation];
        
        for (int x = 0; x < SIZEX; x ++){
            for (int y = 0; y < SIZEY; y ++){
                board[x][y] = Cell.DEAD;
            }
        }
        int p1 = (int) (Math.random() * configs.length);
        for (int x = 0; x < configs[p1].length; x ++) {
            for (int y = 0; y < configs[p1][x].length; y ++) {
                if (configs[p1][x][y]) {
                    board[x][y] = Cell.RED;
                }
            }
        }

        int p2 = (int) (Math.random() * configs.length);
        for (int x = 0; x < configs[p2].length; x ++) {
            for (int y = 0; y < configs[p2][x].length; y ++) {
                if (configs[p2][x][y]) {
                    board[SIZEX - x - 1][SIZEY - y - 1] = Cell.BLUE;
                }
            }
        }
    }
    public short getGeneration() {
        return generation;
    }

    public void computeNextHalfRound(){
        if (generation >= maxGenerations) {
            return;
        }
        generation ++;
        Cell[][] newBoard = history[generation];
        
        for (int x = 0; x < board.length; x ++){
            for (int y = 0; y < board[x].length; y ++){
                Cell cell = board[x][y];
                long neighbors = getNeighborCount(x, y);
                int tot = (int) (neighbors >> 32) + (int) neighbors;

                switch (cell) {
                    case RED:
                        if ((int) (neighbors >> 32) < 2 || tot < 2 || tot > 3) {
                            newBoard[x][y] = Cell.REDdying;
                        } else {
                            newBoard[x][y] = Cell.RED;
                        }
                        break;

                    case BLUE:
                        if ((int) neighbors < 2 || tot < 2 || tot > 3) {
                            newBoard[x][y] = Cell.BLUEdying;
                        } else {
                            newBoard[x][y] = Cell.BLUE;
                        }
                        break;

                    case DEAD:
                        if ((int) (neighbors >> 32) == 3 && tot == 3) {
                            newBoard[x][y] = Cell.REDawakening;
                        } else if ((int) neighbors == 3 && tot == 3) {
                            newBoard[x][y] = Cell.BLUEawakening;
                        } else {
                            newBoard[x][y] = Cell.DEAD;
                        }
                        break;
                    default:
                        break;
                }

            }
        }
        board = newBoard;
    }

    public void updateCells(){
        
        for (int x = 0; x < board.length; x ++){
            for (int y = 0; y < board[x].length; y ++){
                Cell cell = board[x][y];
                switch (cell) {
                    case REDawakening:
                        board[x][y] = Cell.RED;
                        break;
                    case REDdying:
                        board[x][y] = Cell.DEAD;
                        break;
                    case BLUEawakening:
                        board[x][y] = Cell.BLUE;
                        break;
                    case BLUEdying:
                        board[x][y] = Cell.DEAD;
                        break;
                    default:
                        break;
                }
            }
        }
    }

    public void undo(){
        if (generation <= 0) {
            return;
        }
        generation --;
        board = history[generation];
    }
    
    public int[] countPopulations() { //Format: red, blue
        int[] pops = {0, 0};
        for (int x = 0; x < board.length; x ++){
            for (int y = 0; y < board[x].length; y ++){
                Cell cell = board[x][y];
                switch (cell) {
                    case RED:
                        pops[0] ++;
                        break;
                    case BLUE:
                        pops[1] ++;
                        break;
                    default:
                        break;
                }
            }
        }
        return pops;
    }

    private long getNeighborCount(int x, int y){ //int[0] = red, int[1] = blue
        int red = 0;
        int blue = 0;
        for (int i = 0; i < X.length && i < Y.length; i ++){
            try {
                Cell cell = board[x + X[i]][y + Y[i]];
                
                switch (cell) {
                    case RED: case REDdying:
                        red ++;
                        break;
                    case BLUE: case BLUEdying:
                        blue ++;
                        break;
                    default:
                        break;
                }
            } catch (Exception ignored){}
        }
        return ((long) red << 32) | (blue & 0xffffffffL);
    }

    public Cell[][] getBoard() {
        return board;
    }

    public void printBoard(){
        for (int y = 0; y < SIZEY; y ++){
            for (int x = 0; x < SIZEX; x ++){
                switch (board[x][y]) {
                    case DEAD:
                        System.out.print(".");// White square emoji
                        break;
                    case REDawakening: case RED: case REDdying:
                        System.out.print("R");// Red circle emoji
                        break;
                    case BLUEawakening: case BLUE: case BLUEdying:
                        System.out.print("B");// Blue circle emoji
                        break;
                }
            }
            System.out.println();
        }
    }

    public boolean isValidSeed(boolean red, int x, int y){
        if (x < 0 || x >= SIZEX || y < 0 || y >= SIZEY) return false;

        if (board[x][y] != Cell.DEAD) return false;

        long neighborCount = getNeighborCount(x, y);
        if (red && (int) (neighborCount >> 32) <= 0) return false;
        if (!red && (int) neighborCount <= 0) return false;

        return true;
    }

    public boolean seed(boolean red, int x, int y){
        if (!isValidSeed(red, x, y)) {
            return false;
        }

        board[x][y] = red ? Cell.RED : Cell.BLUE;
        return true;
    }
    public boolean unseed(boolean red, int x, int y){
        if (x < 0 || x >= SIZEX || y < 0 || y >= SIZEY) return false;

        if (board[x][y] == Cell.DEAD) return false;

        if (red && (board[x][y] == Cell.BLUE || board[x][y] == Cell.BLUEawakening || board[x][y] == Cell.BLUEdying)) return false;
        if (!red && (board[x][y] == Cell.RED || board[x][y] == Cell.REDawakening || board[x][y] == Cell.REDdying)) return false;

        board[x][y] = Cell.DEAD;
        return true;
    }

    public long[] getMoves(boolean red) {
        long[] moves = new long[SIZEX * SIZEY + 1];
        moves[0] = INVALID_MOVE;
        int moveCount = 1;
        for (int y = 0; y < SIZEY; y ++){
            for (int x = 0; x < SIZEX; x ++){
                if (isValidSeed(red, x, y)) {
                    moves[moveCount] = ((long) x << 32) | (y & 0xffffffffL);
                    moveCount ++;
                }
            }
        }
        long[] res = new long[moveCount];
        while (moveCount > 0) {
            moveCount --;
            res[moveCount] = moves[moveCount];
        }
        return res;
    }

    public int[] getBestMoveAndScore(boolean amIRed, int recursionDepth) {//Format: x, y, score
        System.out.println("Recursion depth: " + recursionDepth);
        long[] myMoves = getMoves(amIRed);
        long[] opponentMoves = getMoves(!amIRed);

        if (myMoves.length == 0) {
            throw new IllegalArgumentException("myMoves length == 0!");
        }
        if (opponentMoves.length == 0) {
            throw new IllegalArgumentException("opponentMoves length == 0!");
        }

        long bestMove = INVALID_MOVE;
        int bestScore = Integer.MIN_VALUE;

        for (int myIndex = 0; myIndex < myMoves.length; myIndex ++) {
            int least = Integer.MAX_VALUE;

            for (int oppIndex = 0; oppIndex < opponentMoves.length; oppIndex ++) {
                long my = myMoves[myIndex];
                long opp = opponentMoves[oppIndex];

                if (my != INVALID_MOVE && my != opp) {
                    seed(amIRed, (int) (my >> 32), (int) my);
                }
                if (opp != INVALID_MOVE && opp != my) {
                    seed(!amIRed, (int) (opp >> 32), (int) opp);
                }

                computeNextHalfRound();
                updateCells();

                int score;

                if (recursionDepth <= 1 || generation >= maxGenerations) {
                    int[] pops = countPopulations();
                    int m = pops[amIRed ? 0 : 1];
                    int o = pops[amIRed ? 1 : 0];
                    score = m - o;
                } else {
                    score = getBestMoveAndScore(amIRed, recursionDepth - 1)[2];
                }

                if (score < least) {
                    least = score;
                }
                undo(); //Undo moves
                if (my != INVALID_MOVE && my != opp){
                    unseed(amIRed, (int) (my >> 32), (int) my);
                }
                if (opp != INVALID_MOVE && opp != my){
                    unseed(!amIRed, (int) (opp >> 32), (int) opp);
                }
            }

            if (least >= bestScore) {
                bestScore = least;
                bestMove = myMoves[myIndex];
            }
        }
        return new int[]{(int) (bestMove >> 32), (int) bestMove, bestScore};
    }

}
