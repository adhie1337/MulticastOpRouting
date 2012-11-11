package routing.view.editor;

import java.awt.Color;
import java.util.List;
import java.util.Map;
import java.util.Set;

import routing.control.entities.Session;

public class RenderInfo {
	
	public Session session;
	public Set<Integer> highlightedNodeIds;
	public List<Edge> directedEdges;
	public Map<Integer, String> nodeInfo;

	public static class Edge {
		public int fromId;
		public int toId;
		
		public Color color = Color.BLACK;
		
		public Edge(int fromId, int toId) {
			this(fromId, toId, Color.BLACK);
		}
		
		public Edge(int fromId, int toId, Color color) {
			this.fromId = fromId;
			this.toId = toId;
			this.color = color;
		}
	}
}
