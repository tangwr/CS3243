package TetrisState;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class ProjectedState {
    public static final int COLS = 10;
    public static final int ROWS = 21;
    public static final int N_PIECES = 7;

    public boolean lost = false;
    
    //current turn
    private int turn = 0;
    private int cleared = 0;
    
    //each square in the grid - int means empty - other values mean the turn it was placed
    private int[][] field = new int[ROWS][COLS];
    //top row+1 of each column
    //0 means empty
    private int[] top = new int[COLS];

    
    //number of next piece
    protected int nextPiece;

    
    //all legal moves - first index is piece type - then a list of 2-length arrays
    protected static int[][][] legalMoves = new int[N_PIECES][][];
    
    //indices for legalMoves
    public static final int ORIENT = 0;
    public static final int SLOT = 1;
    
    //possible orientations for a given piece type
    protected static int[] pOrients = {1,2,4,4,4,2,2};
    
    //the next several arrays define the piece vocabulary in detail
    //width of the pieces [piece ID][orientation]
    protected static int[][] pWidth = {
            {2},
            {1,4},
            {2,3,2,3},
            {2,3,2,3},
            {2,3,2,3},
            {3,2},
            {3,2}
    };
    //height of the pieces [piece ID][orientation]
    private static int[][] pHeight = {
            {2},
            {4,1},
            {3,2,3,2},
            {3,2,3,2},
            {3,2,3,2},
            {2,3},
            {2,3}
    };
    private static int[][][] pBottom = {
        {{0,0}},
        {{0},{0,0,0,0}},
        {{0,0},{0,1,1},{2,0},{0,0,0}},
        {{0,0},{0,0,0},{0,2},{1,1,0}},
        {{0,1},{1,0,1},{1,0},{0,0,0}},
        {{0,0,1},{1,0}},
        {{1,0,0},{0,1}}
    };
    private static int[][][] pTop = {
        {{2,2}},
        {{4},{1,1,1,1}},
        {{3,1},{2,2,2},{3,3},{1,1,2}},
        {{1,3},{2,1,1},{3,3},{2,2,2}},
        {{3,2},{2,2,2},{2,3},{1,2,1}},
        {{1,2,2},{3,2}},
        {{2,2,1},{2,3}}
    };
    
    //initialize legalMoves
    {
        //for each piece type
        for(int i = 0; i < N_PIECES; i++) {
            //figure number of legal moves
            int n = 0;
            for(int j = 0; j < pOrients[i]; j++) {
                //number of locations in this orientation
                n += COLS+1-pWidth[i][j];
            }
            //allocate space
            legalMoves[i] = new int[n][2];
            //for each orientation
            n = 0;
            for(int j = 0; j < pOrients[i]; j++) {
                //for each slot
                for(int k = 0; k < COLS+1-pWidth[i][j];k++) {
                    legalMoves[i][n][ORIENT] = j;
                    legalMoves[i][n][SLOT] = k;
                    n++;
                }
            }
        }
    
    } 
    
    public int[][] getField() {
        return field;
    }

    public int[] getTop() {
        return top;
    }

    public static int[] getpOrients() {
        return pOrients;
    }
    
    public static int[][] getpWidth() {
        return pWidth;
    }

    public static int[][] getpHeight() {
        return pHeight;
    }

    public static int[][][] getpBottom() {
        return pBottom;
    }

    public static int[][][] getpTop() {
        return pTop;
    }


    public int getNextPiece() {
        return nextPiece;
    }
    
    public boolean hasLost() {
        return lost;
    }
    
    public int getRowsCleared() {
        return cleared;
    }
    
    public int getTurnNumber() {
        return turn;
    }
    
    public int getMaxHeight() {
        int max = 0;
        for (int i = 0; i < COLS; i++) {
            max = Math.max(max, top[i]);
        }
        return max;
    }
    
    public int getAggregateHeight() {
        int min = 20;
        int aggregateHeight = 0;
        for (int i = 0; i < COLS; i++) {
            min = Math.min(min, top[i]);
            aggregateHeight += top[i];
        }
        aggregateHeight = aggregateHeight - min * COLS;
        return aggregateHeight;
    }
    
    public int getNumberOfHoles() {
        int holes = 0;
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < top[i]; j++) {
                if (field[j][i] == 0)  {
                   holes ++;
                }
            }
        }
        return holes;
    }
    
    public int getNumberOfBlockage() {
        int blockage = 0;
        boolean hole = false;
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < top[i]; j++) {
                if (field[j][i] == 0) {
                    hole = true;
                }
                if (hole && field[j][i] != 0)  {
                    blockage ++;
                }
            }
        }
        return blockage;
    }
    
    public int getBumpiness() {
        int bumpiness = 0;
        for (int i = 1; i < COLS; i++) {
            bumpiness += Math.abs(top[i-1] - top[i]);
        }
        return bumpiness;
    }
    
    public int getWells() {
        int wells = 0;
        int left, right;
        for (int i = 0; i < COLS; i++) {
            for (int j = top[i]; j < ROWS; j++) {
                if (i == 0) {
                    left = 1;
                } else {
                    left = field[j][i - 1];
                }
                if (i == 9) {
                    right = 1;
                } else {
                    right = field[j][i + 1];
                }
                if ((left != 0) && (right != 0)) {
                    wells++;
                }
            }
        }
        return wells;
    }

    public double getTouchingEdges() {
        double edges = 0.0;

        for (int r = 0; r < 21; r++) {
            for (int c = 0; c < 10; c++) {
                if (field[r][c] == this.getTurnNumber()) {
                    if (c == 0) {
                        edges += 2;
                    } else if (field[r][c - 1] != 0 && field[r][c - 1] != this.getTurnNumber()) {
                        edges++;
                    }
                    if (c == 9) {
                        edges += 2;
                    } else if (field[r][c + 1] != 0 && field[r][c + 1] != this.getTurnNumber()) {
                        edges++;
                    }
                }
            }
        }
        return edges;
    }

    public double getLandingHeight() {
        int maxHeight = 0;
        int minHeight = 10;

        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLS; c++) {
                if (field[r][c] == this.getTurnNumber()) {
                    maxHeight = Math.max(maxHeight, r);
                    minHeight = Math.min(minHeight, r);
                }
            }
        }
        return minHeight + maxHeight;
    }
    
    //constructor
    public ProjectedState(TetrisState.State s) {
        field = new int[State.ROWS][State.COLS];
        int[][] actualField = s.getField();

        for (int i = 0; i < State.ROWS; i++) {
            for (int j = 0; j < State.COLS; j++) {
                field[i][j] = actualField[i][j];
            }
        }

        for (int k = 0; k < s.getTop().length; k++) {
            top[k] = s.getTop()[k];
        }
        
        nextPiece = s.getNextPiece();
        turn = s.getTurnNumber();
        cleared = s.getRowsCleared();
    }
    
    

	//random integer, returns 0-6
    private int randomPiece() {
        return (int)(Math.random()*N_PIECES);
    }
    


    
    //gives legal moves for 
    public int[][] legalMoves() {
        return legalMoves[nextPiece];
    }
    
    //make a move based on the move index - its order in the legalMoves list
    public void makeMove(int move) {
        makeMove(legalMoves[nextPiece][move]);
    }
    
    //make a move based on an array of orient and slot
    public void makeMove(int[] move) {
        makeMove(move[ORIENT],move[SLOT]);
    }
    
    //returns false if you lose - true otherwise
    public boolean makeMove(int orient, int slot) {
        turn++;
        //height if the first column makes contact
        int height = top[slot]-pBottom[nextPiece][orient][0];
        //for each column beyond the first in the piece
        for(int c = 1; c < pWidth[nextPiece][orient];c++) {
            height = Math.max(height,top[slot+c]-pBottom[nextPiece][orient][c]);
        }
        
        //check if game ended
        if(height+pHeight[nextPiece][orient] >= ROWS) {
            lost = true;
            return false;
        }

        
        //for each column in the piece - fill in the appropriate blocks
        for(int i = 0; i < pWidth[nextPiece][orient]; i++) {
            
            //from bottom to top of brick
            for(int h = height+pBottom[nextPiece][orient][i]; h < height+pTop[nextPiece][orient][i]; h++) {
                field[h][i+slot] = turn;
            }
        }
        
        //adjust top
        for(int c = 0; c < pWidth[nextPiece][orient]; c++) {
            top[slot+c]=height+pTop[nextPiece][orient][c];
        }
        
        int rowsCleared = 0;
        
        //check for full rows - starting at the top
        for(int r = height+pHeight[nextPiece][orient]-1; r >= height; r--) {
            //check all columns in the row
            boolean full = true;
            for(int c = 0; c < COLS; c++) {
                if(field[r][c] == 0) {
                    full = false;
                    break;
                }
            }
            //if the row was full - remove it and slide above stuff down
            if(full) {
                rowsCleared++;
                cleared++;
                //for each column
                for(int c = 0; c < COLS; c++) {

                    //slide down all bricks
                    for(int i = r; i < top[c]; i++) {
                        field[i][c] = field[i+1][c];
                    }
                    //lower the top
                    top[c]--;
                    while(top[c]>=1 && field[top[c]-1][c]==0)   top[c]--;
                }
            }
        }

        //pick a new piece
        nextPiece = randomPiece();

        return true;
    }
}
