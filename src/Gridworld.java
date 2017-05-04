import java.util.Arrays;
import java.util.Random;

public class Gridworld {

    private Square[][] board;
    private Location mylocation;
    private Location mygoal;
    private float gammaDF = 0.9f;
    private float lambda = 0.0000005f;
    private float alpha = 0.005f;


    /**
     * Gridworld constructor.
     * @param rowSize
     * @param colSize
     */
    public Gridworld(int rowSize, int colSize, boolean addObstacles) {
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
        mygoal = new Location(); startMyGoal();
        board[mygoal.getX()][mygoal.getY()].setRewardval(1);
        mylocation = new Location(); startMyLocation();
        if (addObstacles) generateObstacles();
    }

    public float getGammaDF() { return  gammaDF; }

    private void startMyLocation() {
        Random r = new Random();
        int row = r.nextInt(board.length);
        int col = r.nextInt(board[0].length);
        while ((row == mygoal.getX() && col == mygoal.getY()) || board[row][col].isObstacle()) {
            row = r.nextInt(board.length);
            col = r.nextInt(board[0].length);
        }
        mylocation.setX(row);
        mylocation.setY(col);
    }

    private void startMyGoal() {
        Random r = new Random();
        int row = r.nextInt(board.length);
        int col = r.nextInt(board[0].length);
        mygoal.setX(row);
        mygoal.setY(col);
    }

    private boolean onTarget() {
        return mylocation.getX() == mygoal.getX() && mylocation.getY() == mygoal.getY();
    }

    private Square.DirType explore() {
        Random r = new Random();
        int nextMove = r.nextInt(Square.DirType.values().length);
        return Square.DirType.values()[nextMove];
    }

    private Square.DirType exploit(Square s) {
        float upVal = s.getWeights()[0];
        float downVal = s.getWeights()[1];
        float leftVal = s.getWeights()[2];
        float rightVal = s.getWeights()[3];

        float[] farray = {upVal, downVal, leftVal, rightVal};
        float max = -100f; int idx = -1;

        for (int i = 0; i < farray.length; i++) {
            if (farray[i] > max) {
                max = farray[i];
                idx = i;
            }
        }
        return Square.DirType.values()[idx];
    }

    private float max_of_square(Square s) {
        float upVal = s.getWeights()[0];
        float downVal = s.getWeights()[1];
        float leftVal = s.getWeights()[2];
        float rightVal = s.getWeights()[3];

        float[] farr = {upVal, downVal, leftVal, rightVal};

        float max = -100f;
        for (float tmp : farr)
            if (tmp > max)
                max = tmp;

        return max;
    }

    private Square get_qsaP(Square.DirType dirType) {
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
                throw new IllegalArgumentException();
        }
    }

    private void resetEligibility() {
        for (Square[] aBoard : board) {
            for (int j = 0; j < board[0].length; j++) {
                for (int k = 0; k < 4; k++) {
                    aBoard[j].getEleg()[k] = 0;
                }
            }
        }
    }

    public void takeAction() {
        // Gather Q(s,a) and gather 'a'
        Random r = new Random(); float tmp = (r.nextInt(100) + 1) / 100f;
        Square qsa = board[mylocation.getX()][mylocation.getY()];
        float max_qsa = max_of_square(qsa);
        Square.DirType nextMove_qsa = (tmp < gammaDF) ? explore() : exploit(qsa);

        // Gather reward in s' and gather Q(s'a')
        int reward; float max_qsaP; Square qsaP;
        try {
            switch (nextMove_qsa) {
                case UP:
                    qsaP = get_qsaP(Square.DirType.UP);
                    max_qsaP = max_of_square(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                case DOWN:
                    qsaP = get_qsaP(Square.DirType.DOWN);
                    max_qsaP = max_of_square(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                case LEFT:
                    qsaP = get_qsaP(Square.DirType.LEFT);
                    max_qsaP = max_of_square(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                case RIGHT:
                    qsaP = get_qsaP(Square.DirType.RIGHT);
                    max_qsaP = max_of_square(qsaP);
                    reward = qsaP.getRewardval();
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
        catch (ArrayIndexOutOfBoundsException e) {
            // you attempted Q(s'a') out-of-bounds
            reward = -1;
            max_qsaP = 0;
        }

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
                throw new IllegalArgumentException();
        }

        // check end of episode - out of bounds
        if (onObstacle() || mylocation.getX() < 0 || mylocation.getX() >= board.length || mylocation.getY() < 0 || mylocation.getY() >= board[0].length) {
            startMyLocation();
            resetEligibility();
            gammaDF -= lambda;
        }

        // check end of episode - on target goal
        if (onTarget()) {
            startMyLocation();
            resetEligibility();
            gammaDF -= lambda;
        }

    }   // end takeAction()

    private void applyArrow(Square s) { s.setDirType(exploit(s)); }

    public void printBoard() {
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                applyArrow(board[i][j]);
                if (i == mylocation.getX() && j == mylocation.getY())
                    System.out.print("O ");
                else if (i == mygoal.getX() && j == mygoal.getY())
                    System.out.print("X ");
                else if (board[i][j].isObstacle())
                    System.out.print("# ");
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
                            throw new IllegalArgumentException();
                    }
                }
            }
            System.out.println();
        }
        System.out.println("MY LOCA - O (" + mylocation.getX() + "," + mylocation.getY() + ")");
        System.out.println("MY GOAL - X (" + mygoal.getX() + "," + mygoal.getY() + ")");
        System.out.println();
    }

    public void printMyLocation() {
        System.out.println("My location (" + mylocation.getX() + "," + mylocation.getY() + ")");
    }
    public void printGoalLocation() {
        System.out.println("Goal location (" + mygoal.getX() + "," + mygoal.getY() + ")");
    }

    public void printSquare(int x, int y) {
        Square s = board[x][y];
        System.out.println("Location: (" + x + "," + y + ")" + "\n" +
                "Square ID: " + s.getId() + "\nObstacle: " + s.isObstacle() + "\nDir: " + s.getDirType() + "\n" +
                "Reward val " + s.getRewardval() + "\nWeights " + Arrays.toString(s.getWeights()) + "\n" +
                "Eligibility val " + Arrays.toString(s.getEleg()));
    }

    public void generateObstacles() {
        Random r = new Random(); int chance;
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if ((i != mylocation.getX() && j != mylocation.getY()) && (i != mygoal.getX() && j != mygoal.getY())) {
                    chance = r.nextInt(4);  // 1 in 5 chance, [0-4] generated
                    if (chance == 0) {
                        board[i][j].setObstacle(true);
                        board[i][j].setRewardval(-1);
                        Arrays.fill(board[i][j].getWeights(), 0);
                    }
                    else board[i][j].setObstacle(false);
                }
            }
        }
    }

    private boolean onObstacle() {
        try {
            return board[mylocation.getX()][mylocation.getY()].isObstacle();
        }
        catch (ArrayIndexOutOfBoundsException e) {
            return true;
        }
    }

    public static void main(String[] args) {
        Gridworld g = new Gridworld(10, 10, false);

        g.printBoard();
        System.out.println();
        long x = 0;
        while (g.getGammaDF() > .05) {
            g.takeAction();
            x++;
        }
        g.printBoard();
        System.out.println("Episodes: " + x);

        g.printSquare(0, 1);
    }
}
