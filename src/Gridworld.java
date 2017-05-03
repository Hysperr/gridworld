import java.util.Arrays;
import java.util.Random;

public class Gridworld {
    private Square[][] board;
    private Location mylocation;
    private Location mygoal;
    private float gammaDF = 0.9f;
    private float lambda = 0.000005f;
    private float alpha = 0.005f;



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
        board[mygoal.getX()][mygoal.getY()].setRewardval(1);
    }

    public float getGammaDF() { return  gammaDF; }

    public void setGammaDF(float gammaDF) { this.gammaDF = gammaDF; }

    public void setLambda(float lambda) { this.lambda = lambda; }

    public void setAlpha(float alpha) { this.alpha = alpha; }

    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if ((i == mylocation.getX()) && (j == mylocation.getY()))
                    System.out.print("O ");
                else if ((i == mygoal.getX()) && (j == mygoal.getY())) {
                    System.out.print("X ");
                }
                else {

                    System.out.print(board[i][j].getDirType());
                }
            }
            System.out.println();
        }
        System.out.println("MY LOCA (" + mylocation.getX() + "," + mylocation.getY() + ")");
        System.out.println("MY GOAL (" + mygoal.getX() + "," + mygoal.getY() + ")");
        System.out.println();
    }

    public void printBoardArrows() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if ((i == mylocation.getX()) && (j == mylocation.getY()))
                    System.out.print("O ");
                else if ((i == mygoal.getX()) && (j == mygoal.getY())) {
                    System.out.print("X ");
                }
                else {
                    switch (board[i][j].getDirType()) {
                        case UP:
                            System.out.print("U ");
                            break;
                        case DOWN:
                            System.out.print("D ");
                            break;
                        case LEFT:
                            System.out.print("L ");
                            break;
                        case RIGHT:
                            System.out.print("R ");
                            break;
                        default:
                            throw new IllegalArgumentException("You shouldn't see this, ever");
                    }
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

    private void stochasticRestart() {
        // can't use this code in startMyLocation
        // because goal's x,y isn't initialized yet so
        // would result nullpointer exception
        Random r = new Random();
        int row = r.nextInt(board.length);
        int col = r.nextInt(board[0].length);
        while (row == mygoal.getX() && col == mygoal.getY()) {
            row = r.nextInt(board.length);
            col = r.nextInt(board[0].length);
        }
        mylocation.setX(row);
        mylocation.setY(col);
    }

    private boolean onTarget() {
        if (mylocation.getX() == mygoal.getX() && mylocation.getY() == mygoal.getY()) {
//            System.out.println("Found at location (" + mylocation.getX() + "," + mylocation.getY() + ")");
            return true;
        }
        return false;
    }

    private Square.DirType explore() {
        Random r = new Random();
        int move = r.nextInt(Square.DirType.values().length);
        return Square.DirType.values()[move];   // next move
    }

    private Square.DirType exploit() {
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
                throw new IllegalArgumentException("You shouldn't be seeing this, ever. Dir was " +
                        "" + nextMove + " from mylocation (" + mylocation.getX() + "," +
                        "" + mylocation.getY() + ")");
        }

        return nextMove;
    }

    public void printMyLocation() {
        System.out.println("My location (" + mylocation.getX() + "," + mylocation.getY() + ")");
    }
    public void printGoalLocation() {
        System.out.println("Goal location (" + mygoal.getX() + "," + mygoal.getY() + ")");
    }

    public Square get_qsaP(Square.DirType dirType) {
        switch (dirType) {
            case UP:
                return board[mylocation.getX() - 1][mylocation.getY()];
            case DOWN:
                return board[mylocation.getX() + 1][mylocation.getY()];
            case LEFT:
                return board[mylocation.getX()][mylocation.getY() - 1];
            case RIGHT:
                return board[mylocation.getX()][mylocation.getY() + 1];
            default:
                throw new IllegalArgumentException("You shouldn't be seeing this, ever. Actual Param dir was " +
                        "" + dirType + " from mylocation (" + mylocation.getX() + "," +
                        "" + mylocation.getY() + ")");
        }
    }

    private float maxOfSquare(Square s) {

        float max = -100f;
        int idx;

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

        return max;
        // we don't know where it leaves out but that's fine. algorithm takes care of messy details.
        // However can make it return the next move...see optional function below
    }
    private Square.DirType nextMoveBasedonMaxofSquare(Square s) {
        // this is basically exploit, except can be any square here.
        // exploit is always our current location

        float max = -100f;
        int idx = -1;

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

        return Square.DirType.values()[idx];
    }

    private void resetEligibility() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                for (int k = 0; k < 4; k++) {
                    board[i][j].getEleg()[k] = 0;
                }
            }
        }
    }

    public void takeAction() {
        Random r = new Random();
        float tmp = (r.nextInt(100) + 1) / 100f;
        Square.DirType nextMove_qsa = (tmp < gammaDF) ? explore() : exploit();

        // Q(s,a) and next move for Q(s,a) is above
//        printMyLocation();
        Square qsa = board[mylocation.getX()][mylocation.getY()];
        float max_qsa = maxOfSquare(qsa);

        int reward;
        float max_qsaP;
        Square qsaP;

        try {
            switch (nextMove_qsa) {
                case UP:
                    qsaP = get_qsaP(Square.DirType.UP);
                    max_qsaP = maxOfSquare(qsaP);
                    reward = qsaP.getRewardval();
                    /* Another way to do it.
                    Square.DirType nextMove_qsaP = nextMoveBasedonMaxofSquare(get_qsaP(Square.DirType.UP));
                    max_qsaP = qsaP.getWeights()[dirType.ordinal()];
                    */
                    break;
                case DOWN:
                    qsaP = get_qsaP(Square.DirType.DOWN);
                    max_qsaP = maxOfSquare(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                case LEFT:
                    qsaP = get_qsaP(Square.DirType.LEFT);
                    max_qsaP = maxOfSquare(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                case RIGHT:
                    qsaP = get_qsaP(Square.DirType.RIGHT);
                    max_qsaP = maxOfSquare(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                default:
                    throw new IllegalArgumentException("You shouldn't be seeing this, ever");
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            max_qsaP = 0;
            reward = -1;
        }
        // we now have Q(s'a')

        // delta = r  + gamma * Q(s'a') - Q(s,a)
        float delta = reward + gammaDF * max_qsaP - max_qsa;
        // e(s,a) <- e(s,a) + 1
        qsa.getEleg()[nextMove_qsa.ordinal()] += 1;
        // for all s,a:
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                for (int k = 0; k < 4; k++) {
                    // Q(s,a) <- Q(s,a) + alpha * delta * e(s,a)
                    board[i][j].getWeights()[k] += (alpha * delta * board[i][j].getEleg()[k]);
                    // e(s,a) <- gamma * lambda * e(s,a)
                    board[i][j].getEleg()[k] = gammaDF * lambda * board[i][j].getEleg()[k];
                }
            }
        }

        // s <- s' ; a <- a'
//        System.out.println("Moving in Direction " + nextMove_qsa);
        switch (nextMove_qsa) {
            case UP:
                mylocation.setX(mylocation.getX() - 1);
                break;
            case DOWN:
                mylocation.setX(mylocation.getX() + 1);
                break;
            case LEFT:
                mylocation.setY(mylocation.getY() - 1);
                break;
            case RIGHT:
                mylocation.setY(mylocation.getY() + 1);
                break;
            default:
                throw new IllegalArgumentException("You shouldn't see this. ever");
        }

        if (mylocation.getX() < 0
                || mylocation.getX() >= board.length
                || mylocation.getY() < 0
                || mylocation.getY() >= board[0].length)
                {
//            System.out.println("You ventured out of bounds");
//            System.out.println("Location reset");
            stochasticRestart();
            resetEligibility();
            gammaDF -= lambda;
            // end of episode
        }

        // agent moved, check if found goal
        if (onTarget()) {
//            System.out.println("Location reset");
            stochasticRestart();
            resetEligibility();
            gammaDF -= lambda;
            // end of episode
        }

    }


    public void applyArrows() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                for (int k = 0; k < 4; k++) {
                    Square.DirType dirType = nextMoveBasedonMaxofSquare(board[i][j]);
                    switch (dirType) {
                        case UP:
                            board[i][j].setDirType(Square.DirType.UP);
                            break;
                        case DOWN:
                            board[i][j].setDirType(Square.DirType.DOWN);
                            break;
                        case LEFT:
                            board[i][j].setDirType(Square.DirType.LEFT);
                            break;
                        case RIGHT:
                            board[i][j].setDirType(Square.DirType.RIGHT);
                            break;
                        default:
                            throw new IllegalArgumentException("You should not see this, ever");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        Gridworld g = new Gridworld(20, 20);

        g.applyArrows();
        g.printBoardArrows();
        System.out.println();
        long x = 0;
        while (g.getGammaDF() > .05) {
            g.takeAction();
            x++;
        }
        g.applyArrows();
        g.printBoardArrows();
        System.out.println("Episodes: " + x);
    }
}

