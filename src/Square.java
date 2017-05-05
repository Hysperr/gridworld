
public class Square {

    public enum DirType { UP, DOWN, LEFT, RIGHT }
    private DirType dirType;
    private float [] weights = new float[4];
    private float [] eleg = new float[4];
    private boolean isObstacle;
    private int rewardval;
    private int id;


    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getRewardval() { return rewardval; }

    public boolean isObstacle() { return isObstacle; }

    public void setObstacle(boolean obstacle) { isObstacle = obstacle; }

    public void setRewardval(int rewardval) { this.rewardval = rewardval; }

    public float[] getWeights() { return weights; }

    public float[] getEleg() { return eleg; }

    public DirType getDirType() { return dirType; }

    public void setDirType(DirType dirType) { this.dirType = dirType; }

}
