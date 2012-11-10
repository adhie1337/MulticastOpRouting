package routing.control.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import routing.util.CompareUtil;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class Graph {


    public String name;

    public HashMap<String, Node> nodes;

    public List<Edge> edges;



    public Node addNode(Node value)
    {
        if(value.sign == null
                || value.sign.equals("")
                || nodes.containsKey(value.sign)
                    && !nodes.get(value.sign).equals(value))
        {
            Iterator<String> it = nodes.keySet().iterator();
            int i = 0;

            while(it.hasNext()) {
                String act = it.next();

                if(act.matches("^p[0-9]+$")) {
                    try {
                        i = Math.max(i, Integer.parseInt(act.substring(1)));
                    }
                    catch(NumberFormatException e)
                    {
                        // never happens
                        //throw new PNException("InvalidNameFormat", "Error");
                    }
                }
            }

            ++i;
            value.sign = "p" + i;
        }
        else if(nodes.containsKey(value.sign)
                    && nodes.get(value.sign).equals(value)) {
            return nodes.get(value.sign);
        }

        value._net = this;
        nodes.put(value.sign, value);
        return value;
    }

    public void addAll(Graph net) {
        HashMap<String, String> signMap = new HashMap<String, String>();

        Iterator<Node> nodeIt = net.nodes.values().iterator();

        while(nodeIt.hasNext()) {
            Node p = nodeIt.next();
            signMap.put(p.sign, addNode(p).sign);
        }

        Iterator<Edge> edgeIt = net.edges.iterator();


        while(edgeIt.hasNext()) {
            Edge e = edgeIt.next();

            if(e.from instanceof Node)
            {
                if(signMap.containsKey(e.from.sign)) {
                    e.from = nodes.get(signMap.get(e.from.sign));
                }
            }
            else
            {
                if(signMap.containsKey(e.to.sign)) {
                    e.to = nodes.get(signMap.get(e.to.sign));
                }
            }

            edges.add(e);
        }
    }

    public Graph getSelection() {
        return getSelection(false);
    }

    public Graph getSelection(Boolean delete) {
        Graph retVal = new Graph();

		Iterator<Node> nodeIt = nodes.values().iterator();

        while(nodeIt.hasNext()) {
            Node next = nodeIt.next();

            if(next.selected) {
                retVal.addNode((Node)next.clone());
            }
        }

        Iterator<Edge> edgeIt = edges.iterator();

        while(edgeIt.hasNext()) {
            Edge next = edgeIt.next();

            if(next.isSelected()) {
                Edge e = new Edge(retVal);

                if(next.from instanceof Node)
                {
                    e.from = retVal.nodes.get(next.from.sign);
                    e.to = retVal.nodes.get(next.to.sign);
                }

                if(e.from != null && e.to != null)
                    retVal.edges.add(e);
            }
        }

        if(delete) {
            for(int i = 0; i < edges.size(); ++i) {
                if(edges.get(i).isSelected()) {
                    edges.remove(i);
                    --i;
                }
            }

            nodeIt = retVal.nodes.values().iterator();

            while(nodeIt.hasNext()) {
                Node next = nodeIt.next();
                nodes.remove(next.sign);
            }
        }

        return retVal;
    }

    public void translate(double translateX, double translateY) {

        Iterator<Node> nodeIt = nodes.values().iterator();

        while (nodeIt.hasNext()) {
            Entity e = nodeIt.next();
            e.x += translateX;
            e.y += translateY;
        }
    }



    public Graph()
    {
        nodes = new HashMap<String, Node>();
        edges = new ArrayList<Edge>();
    }



    @Override
    public boolean equals(Object other) {
        if(!(other instanceof Graph)) {
            return false;
        }

        return CompareUtil.compare(name, ((Graph)other).name)
                && CompareUtil.compare(nodes, ((Graph)other).nodes)
                && CompareUtil.compare(edges, ((Graph)other).edges);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
        hash = 67 * hash + (this.nodes != null ? this.nodes.hashCode() : 0);
        hash = 67 * hash + (this.edges != null ? this.edges.hashCode() : 0);
        return hash;
    }


}
