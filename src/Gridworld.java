import java.util.Arrays;
import java.util.Random;

public class Gridworld {
    private Square[][] board;
    private Location mylocation;
    private Location mygoal;
    private float gammaDF = 0.9f;
    private float lambda = 0.000005f;



    public Gridworld(int rowSize, int colSize) {
        int counter = 0;
        Random r = new Random();
        board = new Square[rowSize][colSize];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                board[i][j] = new Square();
                board[i][j].setRewardval(0);
                board[i][j].setId(++counter);
                board[i][j].setDirType(Square.DirType.values()[r.nextInt(Square.DirType.values().length)]);
                for (int k = 0; k < 4; k++) {
                    board[i][j].getWeights()[k] = r.nextFloat() * 0.01f;
                    board[i][j].getEleg()[k] = 0;
                }
            }
        }
        mylocation = new Location(); startMyLocation();
        mygoal = new Location(); startMyGoal();
    }

    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if ((i == mylocation.getX()) && (j == mylocation.getY()))
                    System.out.print("O ");
                else if ((i == mygoal.getX()) && (j == mygoal.getY())) {
                    System.out.print("X ");
                }
                else {
                    System.out.print("- ");
                }
            }
            System.out.println();
        }
        System.out.println("MY LOCA (" + mylocation.getX() + "," + mylocation.getY() + ")");
        System.out.println("MY GOAL (" + mygoal.getX() + "," + mygoal.getY() + ")");
        System.out.println();
    }

    public void printSquare(int x, int y) {
        Square s = board[x][y];
        System.out.println("Location: (" + x + "," + y + ")" + "\n" +
                "Square ID: " + s.getId() + "\nDir: " + s.getDirType() + "\n" +
                "Reward val " + s.getRewardval() + "\nWeights " + Arrays.toString(s.getWeights()) + "\n" +
                "Elegibility val " + Arrays.toString(s.getEleg()));
    }

    private void startMyLocation() {
        Random r = new Random();
        int row = r.nextInt(board.length);
        int col = r.nextInt(board[0].length);
        mylocation.setX(row);
        mylocation.setY(col);
    }

    private void startMyGoal() {
        Random r = new Random();
        int row = r.nextInt(board.length);
        int col = r.nextInt(board[0].length);
        while (row == mylocation.getX() && col == mylocation.getY()) {
            row = r.nextInt(board.length);
            col = r.nextInt(board[0].length);
        }
        mygoal.setX(row);
        mygoal.setY(col);
    }

    public boolean isfound() {
        if (mylocation.getX() == mygoal.getX() && mylocation.getY() == mygoal.getY()) {
            System.out.println("Found at location (" + mylocation.getX() + "," + mylocation.getY() + ")");
            return true;
        }
        return false;
    }

    public Square.DirType explore() {
        Random r = new Random();
        int move = r.nextInt(Square.DirType.values().length);
        return Square.DirType.values()[move];
    }

    public Square.DirType exploit() {
        float max = -100f;
        int idx = 0;

        Square s = board[mylocation.getX()][mylocation.getY()];

        float upVal = s.getWeights()[0];
        float downVal = s.getWeights()[1];
        float leftVal = s.getWeights()[2];
        float rightVal = s.getWeights()[3];

        float[] farray = {upVal, downVal, leftVal, rightVal};
        for (int i = 0; i < farray.length; i++) {
            if (farray[i] > max) {
                max = farray[i];
                idx = i;
            }
        }

        Square.DirType nextMove = null;
        switch (idx) {
            case 0:
                nextMove = Square.DirType.UP;
                break;
            case 1:
                nextMove = Square.DirType.DOWN;
                break;
            case 2:
                nextMove = Square.DirType.LEFT;
                break;
            case 3:
                nextMove = Square.DirType.RIGHT;
                break;
            default:
               throw new IllegalArgumentException("You shouldn't be seeing this, ever");
        }

        return nextMove;

    }




    public static void main(String[] args) {
        Gridworld g = new Gridworld(3, 3);
        g.printBoard();
        g.printSquare(2, 2);
    }

}


