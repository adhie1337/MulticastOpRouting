package routing.view.editor.listeners;

import java.awt.event.MouseEvent;
import java.util.Iterator;

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
				&& _editor.getDocument().graph != null ? _editor.getDocument().graph
				: null;

		if (net != null
				&& _editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge) {
			Node found = null;

			Iterator<Node> nodeIt = net.getNodeList().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (found != null) {
					break;
				} else if (isNodeAtPoint(actNode, e.getPoint())) {
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
				&& _editor.getDocument().graph != null ? _editor.getDocument().graph
				: null;

		if (net != null && _editor.canvas.edgeToAddStart != null
				&& _editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge) {
			Node found = null;

			Iterator<Node> nodeIt = net.getNodeList().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (!_editor.canvas.edgeToAddStart.equals(actNode) && isNodeAtPoint(actNode, e.getPoint())) {
					found = actNode;
					break;
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
				&& _editor.getDocument().graph != null ? _editor.getDocument().graph
				: null;

		if (net != null && _editor.canvas.edgeToAddStart != null
				&& _editor.canvas.edgeToAddFinish != null) {
			net.setWeight(_editor.canvas.edgeToAddStart.id,
					_editor.canvas.edgeToAddFinish.id, 0.5);
		}

		_editor.canvas.edgeToAddStart = null;
		_editor.canvas.edgeToAddFinish = null;
		_editor.canvas.edgeToAddEnd = null;

		super.mouseReleased(e);
	}
}
