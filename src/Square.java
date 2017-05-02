
public class Square {
    public enum DirType { UP, DOWN, LEFT, RIGHT }
    private DirType dirType;
    private float [] weights = new float[4];
    private float [] eleg = new float[4];
    private int rewardval;
    private int id;


    public float[] getWeights() {
        return weights;
    }

    public void setWeights(float[] weights) {
        this.weights = weights;
    }

    public float[] getEleg() {
        return eleg;
    }

    public void setEleg(float[] eleg) {
        this.eleg = eleg;
    }

    public int getRewardval() {
        return rewardval;
    }

    public void setRewardval(int rewardval) {
        this.rewardval = rewardval;
    }

    public DirType getDirType() {
        return dirType;
    }

    public void setDirType(DirType dirType) {
        this.dirType = dirType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


}
