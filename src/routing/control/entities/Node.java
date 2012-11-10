package routing.control.entities;

import routing.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Node {

    protected Graph _net;

    public double x;
    public double y;

    public int id;
    public String label;

    public Boolean selected;

    public static int nextNodeId = 1;

    public Node() {
        selected = false;

        id = nextNodeId;
        ++nextNodeId;
    }

    public Node(Graph g) {
        selected = false;

        id = nextNodeId;
        ++nextNodeId;
        
        _net = g;
    }

    @Override
    public boolean equals(Object other) {
        return (other instanceof Node)
                && CompareUtil.compare(((Node)other).x, x)
                && CompareUtil.compare(((Node)other).y, y)
                && CompareUtil.compare(((Node)other).id, id)
                && CompareUtil.compare(((Node)other).label, label);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47 * hash + (new Double(this.x).hashCode());
        hash = 47 * hash + (new Double(this.y).hashCode());
        hash = 47 * hash + (new Integer(this.id).hashCode());
        hash = 47 * hash + (this.label != null ? this.label.hashCode() : 0);
        return hash;
    }

    @Override
    protected Object clone()
    {
        Node retVal = new Node(_net);

        retVal.label = label;
        retVal.selected = selected;
        retVal.id = id;
        retVal.x = x;
        retVal.y = y;

        return retVal;
    }

}
