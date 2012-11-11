package routing.control.entities;

import routing.util.CompareUtil;

/**
 * A class representing an "edge" (a connector between nodes and transitions).
 * @author PIAPAAI.ELTE
 */
public class Edge {

    private Graph _net;

    public Node from;
    public Node to;
    public double weight;

    public Boolean isComplete() {
        return from != null && to != null;
    }

    public Boolean isSelected() {
        return isComplete() && (from.selected || to.selected);
    }
    
    public Graph getNet() {
    	return _net;
    }

    public Edge(Graph net) {
        this._net = net;
        this.weight = 0.5;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Edge)
                && CompareUtil.compare(((Edge)other).from, from)
                && CompareUtil.compare(((Edge)other).to, to)
                && CompareUtil.compare(((Edge)other).weight, weight);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + (this.from != null ? this.from.hashCode() : 0);
        hash = 17 * hash + (this.to != null ? this.to.hashCode() : 0);
        hash = 17 * hash + (new Double(this.weight).hashCode());
        return hash;
    }

}
