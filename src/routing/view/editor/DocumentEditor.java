package routing.view.editor;

import routing.view.editor.listeners.CanvasMouseAddEdgeListener;
import routing.view.editor.listeners.CanvasMouseAddNodeListener;
import routing.view.editor.listeners.CanvasMouseSelectionListener;
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
import routing.control.entities.Entity;
import routing.view.editor.listeners.CanvasMouseSetPropertiesListener;

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
	 * SetProperties - set the properties of the entity or edge, that is being
	 * clicked on.<br/>
	 * Simulation - simulation mode
	 */
	public enum EditorMode {
		/**
		 * In selection mode, a click on an entity means sets the entity to the
		 * only selection. Ctrl+click and Shift+click means add or remove the
		 * entity from the selection. Alt+click means remove from selection.
		 * When dragging the mouse on the background we can select multiple
		 * entities in a rectangular area. Modifier keys also work in
		 * rectangular selection mode.
		 */
		Selection,
		/**
		 * In addnode mode, a click creates a new node in the Petri net.
		 * Dragging existing nodes will move them and the selection. Selection
		 * modifier keys will also work here.
		 */
		AddNode,
		/**
		 * Drag an edge from the source entity to the target entity to add it.
		 */
		AddEdge,
		/**
		 * A new editor dialog pops up when clicking an entity or edge. the
		 * clicked objects properties can be set in the editor dialog.
		 */
		SetProperties,
		/**
		 * Simulation mode. When a transition is executable, it will turn red.
		 * Clicking it will execute the transition and add or remove the
		 * resources from the source and destination nodes.
		 */
		Simulation
	};

	private Document _document;

	public JScrollPane canvasContainer;
	public Canvas canvas;

	private Map<String, Entity> _selectedEntities;

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

		_selectedEntities = new HashMap<String, Entity>();

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
			canvas.setNet(_document.net);
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
	public List<Entity> getSelection() {
		return new LinkedList<Entity>(_selectedEntities.values());
	}

	/**
	 * Selection setter.
	 * 
	 * @param selection
	 *            the entity that will be the only selected one.
	 */
	public void setSelection(Entity selection) {
		Collection<Entity> newSelection = new LinkedList<Entity>();
		newSelection.add(selection);
		setSelection(newSelection);
	}

	/**
	 * Sets the selecttion to these entities. Others will be deselected.
	 * 
	 * @param selection
	 *            the entities to select.
	 */
	public void setSelection(Collection<Entity> selection) {
		Iterator<Entity> it = _selectedEntities.values().iterator();

		List<Entity> removeAbles = new LinkedList<Entity>();

		while (it.hasNext()) {
			Entity e = it.next();

			if (selection.contains(e)) {
				selection.remove(e);
			} else {
				removeAbles.add(e);
			}
		}

		it = removeAbles.iterator();

		while (it.hasNext()) {
			Entity e = it.next();
			_selectedEntities.remove(e.sign);
			e.selected = false;
		}

		it = selection.iterator();

		while (it.hasNext()) {
			Entity e = it.next();
			_selectedEntities.put(e.sign, e);
			e.selected = true;
		}

		canvas.repaint();
	}

	/**
	 * Add the entity to the list of the selected ones.
	 * 
	 * @param selection
	 */
	public void addToSelection(Entity selection) {
		_selectedEntities.put(selection.sign, selection);
		selection.selected = true;
	}

	/**
	 * Add the entities to the selected ones.
	 * 
	 * @param selection
	 */
	public void addToSelection(Collection<Entity> selection) {
		Iterator<Entity> it = selection.iterator();

		while (it.hasNext()) {
			Entity e = it.next();
			_selectedEntities.put(e.sign, e);
			e.selected = true;
		}

		canvas.repaint();
	}

	/**
	 * Deselect an entity.
	 * 
	 * @param selection
	 */
	public void removeFromSelection(Entity selection) {
		if (_selectedEntities.containsKey(selection.sign)) {
			_selectedEntities.remove(selection.sign);
			selection.selected = false;
		}
	}

	/**
	 * Deselect multiple entities.
	 * 
	 * @param selection
	 */
	public void removeFromSelection(Collection<Entity> selection) {
		Iterator<Entity> it = selection.iterator();

		while (it.hasNext()) {
			Entity e = it.next();
			_selectedEntities.remove(e.sign);
			e.selected = false;
		}

		canvas.repaint();
	}

	/**
	 * Adds an entity to the selection if it isn't currently selected, otherwise
	 * removes it's selection status.
	 * 
	 * @param selection
	 */
	public void addToOrRemoveFromSelection(Entity selection) {
		if (_selectedEntities.containsKey(selection.sign))
			removeFromSelection(selection);
		else
			addToSelection(selection);
	}

	/**
	 * Adds multiple entities to the selection if one isn't currently selected,
	 * otherwise removes it's selection status.
	 * 
	 * @param selection
	 */
	public void addToOrRemoveFromSelection(Collection<Entity> selection) {
		Iterator<Entity> it = selection.iterator();

		while (it.hasNext()) {
			Entity e = it.next();

			if (_selectedEntities.containsKey(e.sign)) {
				_selectedEntities.remove(e.sign);
				e.selected = false;
			} else {
				_selectedEntities.put(e.sign, e);
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
			canvas.setNet(_document.net);
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
