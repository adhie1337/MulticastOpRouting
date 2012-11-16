package routing.view.editor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.swing.JPanel;
import javax.swing.JScrollPane;

import routing.control.Document;
import routing.control.entities.Node;
import routing.view.editor.listeners.CanvasMouseAddEdgeListener;
import routing.view.editor.listeners.CanvasMouseAddNodeListener;
import routing.view.editor.listeners.CanvasMouseSelectionListener;
import routing.view.editor.listeners.CanvasMouseSetPropertiesListener;
import routing.view.editor.listeners.SelectSourceNodeListener;
import routing.view.editor.listeners.SetDestinationNodesListener;

/**
 * 
 * @author PIAPAAI.ELTE
 */
public class DocumentEditor extends JPanel {

	/**
	 * The editor mode type.<br/>
	 * Elements:<br/>
	 * Selection - mouse interaction means setting or modifying the selection.<br/>
	 * AddNode - add or move nodes.<br/>
	 * AddTransition - add or move transitions<br/>
	 * AddEdge - add or move edges<br/>
	 * SetProperties - set the properties of the node or edge, that is being
	 * clicked on.<br/>
	 * Simulation - simulation mode
	 */
	public enum EditorMode {
		/**
		 * In selection mode, a click on a node means sets the node to the only
		 * selection. Ctrl+click and Shift+click means add or remove the node
		 * from the selection. Alt+click means remove from selection. When
		 * dragging the mouse on the background we can select multiple nodes in
		 * a rectangular area. Modifier keys also work in rectangular selection
		 * mode.
		 */
		Selection,
		/**
		 * In addnode mode, a click adds a new node in the graph. Dragging
		 * existing nodes will move them and the selection. Selection modifier
		 * keys will also work here.
		 */
		AddNode,
		/**
		 * Drag an edge from the source node to the target node to add it.
		 */
		AddEdge,
		/**
		 * A new editor dialog pops up when clicking an node or edge. the
		 * clicked objects properties can be set in the editor dialog.
		 */
		SetProperties,
		/**
		 * Simulation mode. When a transition is executable, it will turn red.
		 * Clicking it will execute the transition and add or remove the
		 * resources from the source and destination nodes.
		 */
		Simulation, SelectSourceNode, SelectDestinationNodes
	};

	private Document _document;

	public JScrollPane canvasContainer;
	public Canvas canvas;

	private Map<Integer, Node> _selectedNodes;

	private MouseAdapter _currentMouseListener;
	private EditorMode _editorMode;

	private void _setEditorMode(EditorMode value) {
		_editorMode = value;

		if (_currentMouseListener != null) {
			canvas.removeMouseListener(_currentMouseListener);
			canvas.removeMouseMotionListener(_currentMouseListener);
			canvas.removeMouseWheelListener(_currentMouseListener);
		}

		if (canvas != null) {
			switch (value) {
			case Selection:
				_currentMouseListener = new CanvasMouseSelectionListener(this);
				break;
			case AddNode:
				_currentMouseListener = new CanvasMouseAddNodeListener(this);
				break;
			case AddEdge:
				_currentMouseListener = new CanvasMouseAddEdgeListener(this);
				break;
			case SetProperties:
				_currentMouseListener = new CanvasMouseSetPropertiesListener(
						this);
				break;
			case SelectSourceNode:
				_currentMouseListener = new SelectSourceNodeListener(this);
				setSelection(new LinkedList<Node>());
				break;
			case SelectDestinationNodes:
				_currentMouseListener = new SetDestinationNodesListener(this);
				setSelection(new LinkedList<Node>());
				break;
			case Simulation:
				_currentMouseListener = null;
				setSelection(new LinkedList<Node>());
				break;
			}
		}

		canvas.addMouseListener(_currentMouseListener);
		canvas.addMouseMotionListener(_currentMouseListener);
		canvas.addMouseWheelListener(_currentMouseListener);
		canvas.repaint();
	}

	public java.awt.Point selectionBegin;
	public java.awt.Point selectionEnd;

	public java.awt.Point dragAndDropBegin;
	public Dimension dragAndDropDimension;

	/**
	 * Constructor.
	 * 
	 * @param document
	 *            the document to edit.
	 */
	public DocumentEditor(Document document) {

		_selectedNodes = new HashMap<Integer, Node>();

		_document = document;

		initEditor();

		_setEditorMode(EditorMode.Selection);
	}

	/**
	 * Editor mode getter.
	 */
	public EditorMode getEditorMode() {
		return _editorMode;
	}

	/**
	 * Editor mode setter.
	 * 
	 * @param value
	 */
	public void setEditorMode(EditorMode value) {
		_setEditorMode(value);
	}

	/**
	 * Getter to determine whether there is a rectangular selection going on or
	 * not.
	 */
	public Boolean isBeingSelected() {
		return selectionBegin != null && selectionEnd != null;
	}

	/**
	 * Returns true when items on the canvas are being dragged at the moment.
	 */
	public Boolean isBeingDraggedAndDropped() {
		return dragAndDropBegin != null && dragAndDropDimension != null;
	}

	/**
	 * Document setter.
	 * 
	 * @param value
	 */
	public void setDocument(Document value) {
		_document = value;

		if (canvas != null) {
			canvas.setGraph(_document.graph);
		}
	}

	/**
	 * Document getter.
	 * 
	 * @param value
	 */
	public Document getDocument() {
		return _document;
	}

	/**
	 * Selection getter.
	 * 
	 * @param value
	 */
	public List<Node> getSelection() {
		return new LinkedList<Node>(_selectedNodes.values());
	}

	/**
	 * Selection setter.
	 * 
	 * @param selection
	 *            the node that will be the only selected one.
	 */
	public void setSelection(Node selection) {
		Collection<Node> newSelection = new LinkedList<Node>();
		newSelection.add(selection);
		setSelection(newSelection);
	}

	/**
	 * Sets the selection to these nodes. Others will be deselected.
	 * 
	 * @param selection
	 *            the nodes to select.
	 */
	public void setSelection(Collection<Node> selection) {
		Iterator<Node> it = _selectedNodes.values().iterator();

		List<Node> removeAbles = new LinkedList<Node>();

		while (it.hasNext()) {
			Node e = it.next();

			if (selection.contains(e)) {
				selection.remove(e);
			} else {
				removeAbles.add(e);
			}
		}

		it = removeAbles.iterator();

		while (it.hasNext()) {
			Node e = it.next();
			_selectedNodes.remove(e.id);
			e.selected = false;
		}

		it = selection.iterator();

		while (it.hasNext()) {
			Node e = it.next();
			_selectedNodes.put(e.id, e);
			e.selected = true;
		}

		canvas.repaint();
	}

	/**
	 * Add the node to the list of the selected ones.
	 * 
	 * @param selection
	 */
	public void addToSelection(Node selection) {
		_selectedNodes.put(selection.id, selection);
		selection.selected = true;
	}

	/**
	 * Add the nodes to the selected ones.
	 * 
	 * @param selection
	 */
	public void addToSelection(Collection<Node> selection) {
		Iterator<Node> it = selection.iterator();

		while (it.hasNext()) {
			Node e = it.next();
			_selectedNodes.put(e.id, e);
			e.selected = true;
		}

		canvas.repaint();
	}

	/**
	 * Deselects an node.
	 * 
	 * @param selection
	 */
	public void removeFromSelection(Node selection) {
		if (_selectedNodes.containsKey(selection.id)) {
			_selectedNodes.remove(selection.id);
			selection.selected = false;
		}
	}

	/**
	 * Deselects multiple nodes.
	 * 
	 * @param selection
	 */
	public void removeFromSelection(Collection<Node> selection) {
		Iterator<Node> it = selection.iterator();

		while (it.hasNext()) {
			Node e = it.next();
			_selectedNodes.remove(e.id);
			e.selected = false;
		}

		canvas.repaint();
	}

	/**
	 * Adds a node to the selection if it isn't currently selected, otherwise
	 * removes it's selection status.
	 * 
	 * @param selection
	 */
	public void addToOrRemoveFromSelection(Node selection) {
		if (_selectedNodes.containsKey(selection.id))
			removeFromSelection(selection);
		else
			addToSelection(selection);
	}

	/**
	 * Adds multiple nodes to the selection if one isn't currently selected,
	 * otherwise removes it's selection status.
	 * 
	 * @param selection
	 */
	public void addToOrRemoveFromSelection(Collection<Node> selection) {
		Iterator<Node> it = selection.iterator();

		while (it.hasNext()) {
			Node e = it.next();

			if (_selectedNodes.containsKey(e.id)) {
				_selectedNodes.remove(e.id);
				e.selected = false;
			} else {
				_selectedNodes.put(e.id, e);
				e.selected = true;
			}
		}

		canvas.repaint();
	}

	private void initEditor() {
		canvas = new Canvas(this);
		canvasContainer = new JScrollPane(canvas);
		add(canvasContainer);
		setLayout(new BorderLayout(5, 2));

		if (_document != null) {
			canvas.setGraph(_document.graph);
		}

		addComponentListener(new EditorListener());
	}

	class EditorListener implements ComponentListener {
		@Override
		public void componentResized(ComponentEvent e) {
			canvasContainer.setSize(getSize());
		}

		@Override
		public void componentMoved(ComponentEvent e) {
		}

		@Override
		public void componentShown(ComponentEvent e) {
		}

		@Override
		public void componentHidden(ComponentEvent e) {
		}
	}

}
