package routing.control.entities;

import routing.util.CompareUtil;

/**
 * A class representing an "edge" (a connector between nodes and transitions).
 * @author PIAPAAI.ELTE
 */
public class Edge {

    private Graph _net;

    public Entity from;
    public double fromAngle;
    public double fromWeight;

    public Entity to;
    public double toAngle;
    public double toWeight;

    public double weight;

    public Boolean isComplete() {
        return from != null && to != null;
    }

    public Boolean isSelected() {
        return isComplete() && (from.selected || to.selected);
    }

    public Edge(Graph net) {
        this._net = net;

        fromAngle = -1.0;
        fromWeight = -1.0;
        toAngle = -1.0;
        toWeight = -1.0;

        this.weight = 1.0;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Edge)
                && CompareUtil.compare(((Edge)other).from, from)
                && CompareUtil.compare(((Edge)other).fromAngle, fromAngle)
                && CompareUtil.compare(((Edge)other).fromWeight, fromWeight)
                && CompareUtil.compare(((Edge)other).to, to)
                && CompareUtil.compare(((Edge)other).toAngle, toAngle)
                && CompareUtil.compare(((Edge)other).toWeight, toWeight)
                && CompareUtil.compare(((Edge)other).weight, weight);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.from != null ? this.from.hashCode() : 0);
        hash = 17 * hash + (new Double(this.fromAngle).hashCode());
        hash = 17 * hash + (new Double(this.fromWeight).hashCode());
        hash = 17 * hash + (this.to != null ? this.to.hashCode() : 0);
        hash = 17 * hash + (new Double(this.toAngle).hashCode());
        hash = 17 * hash + (new Double(this.toWeight).hashCode());
        hash = 17 * hash + (new Double(this.weight).hashCode());
        return hash;
    }

}
