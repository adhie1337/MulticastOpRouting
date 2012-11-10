package routing.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import routing.control.entities.Edge;
import routing.control.entities.Entity;
import routing.control.entities.Graph;
import routing.control.entities.Node;
import routing.view.editor.DocumentEditor;
import routing.view.editor.EdgePropertiesDialog;
import routing.view.editor.NodePropertiesDialog;
import routing.RoutingDemo;

/**
 * An event listener class that is responsible for the "propery editing" of the
 * entities and edges.
 * 
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseSetPropertiesListener extends CanvasMouseListener {

	private NodePropertiesDialog nodeProperties;
	private EdgePropertiesDialog edgeProperties;

	/**
	 * Constructor.
	 * 
	 * @param editor
	 */
	public CanvasMouseSetPropertiesListener(DocumentEditor editor) {
		super(editor);
	}

	/**
	 * Mouse click event handler.
	 * 
	 * @param e
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		Graph net = _editor.getDocument() != null
				&& _editor.getDocument().net != null ? _editor.getDocument().net
				: null;

		if (net != null) {
			Entity selected = null;
			Edge selectedEdge = null;

			Iterator<Edge> edgeIt = net.edges.iterator();

			while (edgeIt.hasNext()) {
				Edge actEdge = edgeIt.next();

				if (isEdgeAtPoint(actEdge, e.getPoint())) {
					selectedEdge = actEdge;
				}
			}

			System.out.println("");

			Iterator<Node> nodeIt = net.nodes.values().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (isEntityAtPoint(actNode, e.getPoint())) {
					selected = actNode;
				}
			}

			if (selected != null) {
				if (selected instanceof Node) {
					if (nodeProperties == null) {
						nodeProperties = new NodePropertiesDialog(RoutingDemo
								.getApplication().getMainFrame());
					}

					nodeProperties.setNode((Node) selected);
					RoutingDemo.getApplication().show(nodeProperties);
				}
			} else if (selectedEdge != null) {
				if (edgeProperties == null) {
					edgeProperties = new EdgePropertiesDialog(RoutingDemo
							.getApplication().getMainFrame());
				}

				edgeProperties.setEdge(selectedEdge);
				RoutingDemo.getApplication().show(edgeProperties);
			}
		}
	}
}
