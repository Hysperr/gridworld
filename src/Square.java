
public class Square {

    public enum DirType { UP, DOWN, LEFT, RIGHT }
    private DirType dirType;
    private float [] weights = new float[4];
    private float [] eleg = new float[4];
    private int rewardval;
    private int id;


    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getRewardval() { return rewardval; }

    public void setRewardval(int rewardval) { this.rewardval = rewardval; }

    public float[] getWeights() { return weights; }

    public float[] getEleg() {
        return eleg;
    }

    public DirType getDirType() { return dirType; }

    public void setDirType(DirType dirType) { this.dirType = dirType; }

}
