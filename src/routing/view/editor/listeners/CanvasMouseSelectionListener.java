package routing.view.editor.listeners;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import routing.control.entities.Node;
import routing.control.entities.Graph;

import routing.view.editor.DocumentEditor;

/**
 * An event listener class, that makes the canvas edit its own selection.
 * 
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseSelectionListener extends CanvasMouseListener {

	/**
	 * Constructor.
	 * 
	 * @param editor
	 */
	public CanvasMouseSelectionListener(DocumentEditor editor) {
		super(editor);
	}

	/**
	 * Mouse click handler.
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

			Iterator<Node> nodeIt = net.getNodeList().iterator();

			while (nodeIt.hasNext()) {
				Node actNode = nodeIt.next();

				if (selected != null) {
					break;
				} else if (isNodeAtPoint(actNode, e.getPoint())) {
					selected = actNode;
				}
			}

			if (selected != null) {
				if (e.isShiftDown()) {
					_editor.addToSelection(selected);
				} else if (e.isAltDown()) {
					_editor.removeFromSelection(selected);
				} else if (e.isControlDown()) {
					_editor.addToOrRemoveFromSelection(selected);
				} else {
					_editor.setSelection(selected);
				}
			} else if (!e.isShiftDown()) {
				_editor.setSelection(new LinkedList<Node>());
			}
		}
	}

	/**
	 * Mouse press handler.
	 * 
	 * @param e
	 */
	@Override
	public void mousePressed(MouseEvent e) {

		if (handleDragAndDropStart(e))
			return;

		_editor.selectionBegin = e.getPoint();
		_editor.selectionEnd = e.getPoint();
		_editor.canvas.repaint();
	}

	/**
	 * Mouse release handler.
	 * 
	 * @param e
	 */
	@Override
	public void mouseReleased(MouseEvent e) {

		if (handleDragAndDropEnd(e) || _editor.selectionBegin == null)
			return;

		_editor.selectionEnd = e.getPoint();

		java.awt.Point a = new java.awt.Point(Math.min(
				_editor.selectionBegin.x, _editor.selectionEnd.x), Math.min(
				_editor.selectionBegin.y, _editor.selectionEnd.y));
		Dimension b = new Dimension(Math.max(_editor.selectionBegin.x,
				_editor.selectionEnd.x) - a.x, Math.max(
				_editor.selectionBegin.y, _editor.selectionEnd.y) - a.y);

		List<Node> newSelection = new LinkedList<Node>();

		Iterator<Node> nodeIt = _editor.getDocument().graph.getNodeList()
				.iterator();

		while (nodeIt.hasNext()) {
			Node actNode = nodeIt.next();

			if (isNodeInRect(actNode, a, b)) {
				newSelection.add(actNode);
			}
		}

		if (e.isShiftDown()) {
			_editor.addToSelection(newSelection);
		} else if (e.isAltDown()) {
			_editor.removeFromSelection(newSelection);
		} else if (e.isControlDown()) {
			_editor.addToOrRemoveFromSelection(newSelection);
		} else {
			_editor.setSelection(newSelection);
		}

		_editor.selectionBegin = null;
		_editor.selectionEnd = null;

		super.mouseReleased(e);
	}

	/**
	 * Mouse drag handler.
	 * 
	 * @param e
	 */
	@Override
	public void mouseDragged(MouseEvent e) {

		if (handleDragAndDrop(e))
			return;

		if (_editor.isBeingSelected()) {
			_editor.selectionEnd = e.getPoint();
			_editor.canvas.repaint();
		}
	}

}
