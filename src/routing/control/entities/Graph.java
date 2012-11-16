package routing.control.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import routing.util.CompareUtil;

/**
 * 
 * @author PIAPAAI.ELTE
 */
public class Graph {

	public String name;

	private Map<Integer, Node> nodes;

	private List<Edge> edgeList;

	private HashMap<Integer, HashMap<Integer, Double>> weightMap;

	public Node addNode(Node value) {
		
		if (value.id == 0 || nodes.containsKey(value.id)
				&& !nodes.get(value.id).equals(value)) {
			Iterator<Integer> it = nodes.keySet().iterator();
			int i = 0;

			while (it.hasNext()) {
				i = Math.max(i, it.next());
			}

			value.id = ++i;
		} else if (nodes.containsKey(value.id)
				&& nodes.get(value.id).equals(value)) {
			return nodes.get(value.id);
		}

		value._net = this;
		nodes.put(value.id, value);
		return value;
	}
	
	public Node getNode(int id) {
		return nodes.get(id);
	}
	
	public Set<Integer> getAdjacentNodeIds(int nodeId) {
		if(weightMap.containsKey(nodeId)) {
			return weightMap.get(nodeId).keySet();
		}
		
		return new HashSet<Integer>();
	}

	public Collection<Node> getNodeList() {
		return nodes.values();
	}

	public List<Integer> getNodeIds() {
		List<Integer> retVal = new LinkedList<Integer>(nodes.keySet());
		Collections.sort(retVal);
		return retVal;
	}
	
	public void removeNode(int id) {
		if(nodes.containsKey(id)) {
			Map<Integer, Double> edges = weightMap.get(id);
			
			if(edges != null) {
				Vector<Integer> ids = new Vector<Integer>();
				
				for(Entry<Integer, Double> ent : edges.entrySet()) {
					ids.add(ent.getKey());
				}
				
				for(Integer id2 : ids) {
					setWeight(id, id2, 0);
				}
			}
			
			weightMap.remove(id);
			nodes.remove(id);
		}
	}
	
	public HashMap<Integer, HashMap<Integer, Double>> copyWeightMap() {
		Graph g = new Graph();
		g.addAll(this);
		return g.weightMap;
	}

	public void addAll(Graph net) {
		HashMap<Integer, Integer> idMap = new HashMap<Integer, Integer>();

		Iterator<Node> nodeIt = net.getNodeList().iterator();

		while (nodeIt.hasNext()) {
			Node p = nodeIt.next();
			idMap.put(p.id, addNode((Node)p.clone()).id);
		}

		for(Node n1 : net.getNodeList()){
			for(Node n2 : net.getNodeList()){
				double w;
				if(n1.id != n2.id && (w = net.getWeight(n1.id, n2.id)) > 0.0) {
					setWeight(idMap.get(n1.id), idMap.get(n2.id), w);
				}
			}			
		}
	}

	public List<Edge> getEdgeList() {
		return edgeList;
	}
	
	public double getWeight(int n1, int n2) {
		
		if(weightMap.containsKey(n1)) {
			Map<Integer, Double> innerMap = weightMap.get(n1);
			
			if(innerMap.containsKey(n2)) {
				return innerMap.get(n2);
			}
		}
		
		return 0;
	}
	
	public void setWeight(int n1, int n2, double weight) {
		Map<Integer, Double> map = null;
		
		if(!weightMap.containsKey(n1)) {
			weightMap.put(n1, new HashMap<Integer, Double>());
		}
		
		map = weightMap.get(n1);
		
		if(weight > 0.0) {
			map.put(n2, weight);
		}
		else {
			map.remove(n2);
		}
		
		if(!weightMap.containsKey(n2)) {
			weightMap.put(n2, new HashMap<Integer, Double>());
		}
		
		map = weightMap.get(n2);
		
		if(weight > 0.0) {
			map.put(n1, weight);
		}
		else {
			map.remove(n1);
		}
		
		int idx = -1;
		
		for(int i = 0; i < edgeList.size(); ++i) {
			Edge act = edgeList.get(i);
			
			if(act.from.id == n1 && act.to.id == n2
				|| act.from.id == n2 && act.to.id == n1) {
				idx = i;
				break;
			}
		}
		
		if(idx >= 0) {
			if(weight > 0.0) {
				edgeList.get(idx).weight = weight;
			}
			else {
				edgeList.remove(idx);
			}
		}
		else if(weight > 0.0) {
			Edge e = new Edge(this);
			e.from = nodes.get(n1);
			e.to = nodes.get(n2);
			e.weight = weight;
			edgeList.add(e);
		}
	}

	public Graph getSelection() {
		return getSelection(false);
	}

	public Graph getSelection(Boolean delete) {
		Graph retVal = new Graph();

		Iterator<Node> nodeIt = getNodeList().iterator();

		while (nodeIt.hasNext()) {
			Node next = nodeIt.next();

			if (next.selected) {
				retVal.addNode((Node) next.clone());
			}
		}
		
		for(Node n1 : retVal.getNodeList()){
			for(Node n2 : retVal.getNodeList()){
				double w;
				if(n1.id != n2.id && (w = getWeight(n1.id, n2.id)) > 0.0) {
					retVal.setWeight(n1.id, n2.id, w);
				}
			}			
		}

		if (delete) {
			nodeIt = retVal.getNodeList().iterator();

			while (nodeIt.hasNext()) {
				removeNode(nodeIt.next().id);
			}
		}

		return retVal;
	}

	public void translate(double translateX, double translateY) {

		Iterator<Node> nodeIt = getNodeList().iterator();

		while (nodeIt.hasNext()) {
			Node e = nodeIt.next();
			e.x += translateX;
			e.y += translateY;
		}
	}

	public Graph() {
		nodes = new HashMap<Integer, Node>();
		edgeList = new ArrayList<Edge>();
		weightMap = new HashMap<Integer, HashMap<Integer, Double>>();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof Graph)) {
			return false;
		}

		return CompareUtil.compare(name, ((Graph) other).name)
				&& CompareUtil.compare(nodes, ((Graph) other).nodes)
				&& CompareUtil.compare(weightMap, ((Graph) other).weightMap);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
		hash = 67 * hash + (this.nodes != null ? this.nodes.hashCode() : 0);
		hash = 67 * hash + (this.weightMap != null ? this.weightMap.hashCode() : 0);
		return hash;
	}

}
