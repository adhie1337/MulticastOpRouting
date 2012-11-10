package routing.control.entities;

import routing.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Entity {

    protected Graph _net;

    public double x;
    public double y;

    public String sign;
    public String label;

    public Boolean selected;

    public static int nextNodeId = 1;

    public Entity() {
        selected = false;

        if(getClass().equals(Node.class))
        {
            sign = "n" + nextNodeId;
            ++nextNodeId;
        }
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Entity)
                && CompareUtil.compare(((Entity)other).x, x)
                && CompareUtil.compare(((Entity)other).y, y)
                && CompareUtil.compare(((Entity)other).sign, sign)
                && CompareUtil.compare(((Entity)other).label, label);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (new Double(this.x).hashCode());
        hash = 47 * hash + (new Double(this.y).hashCode());
        hash = 47 * hash + (this.sign != null ? this.sign.hashCode() : 0);
        hash = 47 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }

}
