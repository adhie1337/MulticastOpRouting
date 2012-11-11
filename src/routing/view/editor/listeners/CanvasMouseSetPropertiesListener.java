package routing.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import routing.control.entities.Edge;
import routing.control.entities.Node;
import routing.control.entities.Graph;

import routing.view.editor.DocumentEditor;
import routing.view.editor.EdgePropertiesDialog;
import routing.view.editor.NodePropertiesDialog;
import routing.RoutingDemo;

/**
 * An event listener class that is responsible for the "property editing" of the
 * nodes and edges.
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
				&& _editor.getDocument().graph != null ? _editor.getDocument().graph
				: null;

		if (net != null) {
			Node selected = null;
			Edge selectedEdge = null;

			Iterator<Edge> edgeIt = net.getEdgeList().iterator();

			while (edgeIt.hasNext()) {
				Edge actEdge = edgeIt.next();

				if (isEdgeAtPoint(actEdge, e.getPoint())) {
					selectedEdge = actEdge;
				}
			}

			System.out.println("");

			Iterator<Node> nodeIt = net.getNodeList().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (isNodeAtPoint(actNode, e.getPoint())) {
					selected = actNode;
				}
			}

			if (selected != null) {
				if (nodeProperties == null) {
					nodeProperties = new NodePropertiesDialog(RoutingDemo
							.getApplication().getMainFrame());
				}

				nodeProperties.setNode((Node) selected);
				nodeProperties.showDialog();
			} else if (selectedEdge != null) {
				if (edgeProperties == null) {
					edgeProperties = new EdgePropertiesDialog(RoutingDemo
							.getApplication().getMainFrame());
				}

				edgeProperties.setEdge(selectedEdge);
				edgeProperties.showDialog();
			}
		}
	}
}
