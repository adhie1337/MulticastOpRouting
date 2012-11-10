package routing.control.entities;

import routing.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Node extends Entity implements Cloneable {

    public Node(Graph net) {
        this._net = net;
    }


    @Override
    public boolean equals(Object other) {
        return (other instanceof Node)
                && super.equals(other);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        //hash = 59 * hash + (new Integer(this.weight).hashCode());
        return hash;
    }

    @Override
    protected Object clone()
    {
        Node retVal = new Node(_net);

        retVal.label = label;
        retVal.selected = selected;
        retVal.sign = sign;
        retVal.x = x;
        retVal.y = y;
        //retVal.weight = weight;

        return retVal;
    }


}
