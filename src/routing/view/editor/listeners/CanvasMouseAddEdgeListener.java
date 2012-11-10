package routing.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;
import routing.control.entities.Edge;
import routing.control.entities.Entity;
import routing.control.entities.Graph;
import routing.control.entities.Node;
import routing.view.editor.DocumentEditor;

/**
 * A mouse listener of the canvas that handles edition of edges.
 * 
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseAddEdgeListener extends CanvasMouseListener {

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor instance whose events it listens to.
	 */
	public CanvasMouseAddEdgeListener(DocumentEditor editor) {
		super(editor);
	}

	/**
	 * Mouse pressed event handler.
	 * 
	 * @param e
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		Graph net = _editor.getDocument() != null
				&& _editor.getDocument().net != null ? _editor.getDocument().net
				: null;

		if (net != null
				&& _editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge) {
			Entity found = null;

			Iterator<Node> nodeIt = net.nodes.values().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (found != null) {
					break;
				} else if (isEntityAtPoint(actNode, e.getPoint())) {
					found = actNode;
				}
			}

			if (found != null) {
				_editor.canvas.edgeToAddStart = found;
			}

			_editor.canvas.repaint();
		}
	}

	/**
	 * Mouse dragged event handler.
	 * 
	 * @param e
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		Graph net = _editor.getDocument() != null
				&& _editor.getDocument().net != null ? _editor.getDocument().net
				: null;

		if (net != null && _editor.canvas.edgeToAddStart != null
				&& _editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge) {
			Entity found = null;

			Iterator<Node> nodeIt = net.nodes.values().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (found != null) {
					break;
				} else if (isEntityAtPoint(actNode, e.getPoint())) {
					found = actNode;
				}
			}

			if (found == null) {
				_editor.canvas.edgeToAddEnd = new java.awt.Point(e.getX(),
						e.getY());
				_editor.canvas.edgeToAddFinish = null;
			} else if (found != null && found != _editor.canvas.edgeToAddStart) {
				_editor.canvas.edgeToAddFinish = found;
			}

			_editor.canvas.repaint();
		}
	}

	/**
	 * Mouse released event handler.
	 * 
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		Graph net = _editor.getDocument() != null
				&& _editor.getDocument().net != null ? _editor.getDocument().net
				: null;

		if (net != null) {
			if (_editor.canvas.edgeToAddStart != null
					&& _editor.canvas.edgeToAddFinish != null){
					//&& !_editor.canvas.edgeToAddStart.getClass().equals(
					//		_editor.canvas.edgeToAddFinish.getClass())) {
				Edge newEdge = new Edge(net);
				newEdge.from = _editor.canvas.edgeToAddStart;
				newEdge.to = _editor.canvas.edgeToAddFinish;
				net.edges.add(newEdge);
			}
		}

		_editor.canvas.edgeToAddStart = null;
		_editor.canvas.edgeToAddFinish = null;
		_editor.canvas.edgeToAddEnd = null;

		super.mouseReleased(e);
	}

}
