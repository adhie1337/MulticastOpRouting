package routing.view.editor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.text.NumberFormat;
import java.util.Iterator;
import java.util.Locale;

import routing.control.EditorController;
import routing.control.entities.Edge;
import routing.control.entities.Graph;
import routing.control.entities.Session;

import javax.swing.JPanel;
import routing.control.entities.Node;

/**
 * The canvas class. Handles the drawing of the graph being currently edited.
 * 
 * @author PIAPAAI.ELTE
 */
public class Canvas extends JPanel {

	private DocumentEditor _editor;
	private Graph _graph;

	/**
	 * Graph setter.
	 * 
	 * @param value
	 */
	public void setGraph(Graph value) {
		_graph = value;

		calculateMinSize();

		repaint();
	}

	/**
	 * Graph getter.
	 */
	public Graph getGraph() {
		return _graph;
	}

	private Double _zoom = 1.0;

	/**
	 * Zoom setter
	 * 
	 * @param z
	 */
	public void setZoom(double z) {
		_zoom = Math.max(z, 0.01);
		calculateMinSize();
		repaint();
	}

	/**
	 * Zoom getter
	 */
	public double getZoom() {
		return _zoom;
	}

	public static final Integer NODE_RADIUS = 20;
	public static final Integer TRANSITION_WIDTH = 32;
	private final int ARR_SIZE = 8;

	public Node nodeToAdd = null;

	public Node edgeToAddStart;
	public Node edgeToAddFinish;
	public java.awt.Point edgeToAddEnd;

	/**
	 * Constructor.
	 * 
	 * @param editor
	 *            the editor to draw the graph by.
	 */
	public Canvas(DocumentEditor editor) {
		_editor = editor;

		/*
		 * JPopupMenu menu = new JPopupMenu(); menu.add(new JMenuItem("lol"));
		 * 
		 * setComponentPopupMenu(menu);
		 */
	}

	@Override
	public void paint(Graphics g) {
		g.clearRect(0, 0, getWidth(), getHeight());

		if (_graph == null) {
			return;
		}

		((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		((Graphics2D) g).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));

		Iterator<Node> nodeIt = _graph.getNodeList().iterator();

		while (nodeIt.hasNext()) {
			drawNode(nodeIt.next(), g.create());
		}

		Iterator<Edge> edgeIt = _graph.getEdgeList().iterator();

		((Graphics2D) g).setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));

		while (edgeIt.hasNext()) {
			drawEdge(edgeIt.next(), g.create());
		}

		((Graphics2D) g).setStroke(new BasicStroke(2, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_BEVEL));

		if (_editor.isBeingSelected()
				&& _editor.getEditorMode() == DocumentEditor.EditorMode.Selection) {
			java.awt.Point a = new java.awt.Point(Math.min(
					_editor.selectionBegin.x, _editor.selectionEnd.x),
					Math.min(_editor.selectionBegin.y, _editor.selectionEnd.y));
			Dimension b = new Dimension(Math.max(_editor.selectionBegin.x,
					_editor.selectionEnd.x) - a.x, Math.max(
					_editor.selectionBegin.y, _editor.selectionEnd.y) - a.y);
			g.setColor(new Color((float) 0.0, (float) 0.0, (float) 1.0,
					(float) 0.1));
			g.fillRect(a.x, a.y, b.width, b.height);
			g.setColor(new Color((float) 0.0, (float) 0.0, (float) 1.0,
					(float) 0.9));
			g.drawRect(a.x, a.y, b.width, b.height);
		} else if (_editor.getEditorMode() == DocumentEditor.EditorMode.AddNode
				&& nodeToAdd != null) {
			g.setColor(new Color((float) 0.0, (float) 0.0, (float) 1.0,
					(float) 0.1));

			drawNode(nodeToAdd, g.create());
		} else if (_editor.getEditorMode() == DocumentEditor.EditorMode.AddEdge
				&& edgeToAddStart != null) {
			Color c = new Color((float) 1.0, (float) 0.0, (float) 0.0,
					(float) 1.0);

			if (edgeToAddFinish != null) {
				c = new Color((float) 0.0, (float) 0.0, (float) 1.0,
						(float) 1.0);

				Edge edgeToDraw = new Edge(_graph);
				edgeToDraw.from = edgeToAddStart;
				edgeToDraw.to = edgeToAddFinish;

				drawEdge(edgeToDraw, g, c, false);
			} else if (edgeToAddEnd != null) {
				java.awt.Point start = null;

				start = getCorrectedPointFromNode(edgeToAddStart.x,
						edgeToAddStart.y, edgeToAddEnd.x, edgeToAddEnd.y,
						NODE_RADIUS - 1);

				g.setColor(c);

				if (false) {
					drawArrow((Graphics2D) g, start.x, start.y, edgeToAddEnd.x,
							edgeToAddEnd.y);
				} else {
					g.drawLine(start.x, start.y, edgeToAddEnd.x, edgeToAddEnd.y);
				}
			}
		}
	}

	private void drawNode(Node p, Graphics g) {
		java.awt.Point pos = getCorrectedPosition(p);
		g.setColor(getColorForEntity(p, false));
		g.fillOval((int) ((pos.x - NODE_RADIUS) * _zoom),
				(int) ((pos.y - NODE_RADIUS) * _zoom),
				(int) (2 * NODE_RADIUS * _zoom),
				(int) (2 * NODE_RADIUS * _zoom));
		g.setColor(getColorForEntity(p, true));
		g.drawOval((int) ((pos.x - NODE_RADIUS) * _zoom),
				(int) ((pos.y - NODE_RADIUS) * _zoom),
				(int) (2 * NODE_RADIUS * _zoom),
				(int) (2 * NODE_RADIUS * _zoom));

		if (p.label != null && !p.label.trim().equals("")) {
			Graphics g2 = g.create();
			g2.setFont(g2.getFont().deriveFont(
					(float) (NODE_RADIUS * 2 / 3 * _zoom)));

			FontMetrics m = g2.getFontMetrics();

			g2.drawString(p.label, (int) (pos.x * _zoom - m
					.stringWidth(p.label) / 2), (int) ((pos.y - NODE_RADIUS)
					* _zoom - g2.getFont().getSize2D() / 2));
		}

		/*
		 * if(p.weight > 0) { String lbl =
		 * NumberFormat.getNumberInstance(Locale.US).format(p.weight); Graphics
		 * g2 = g.create();
		 * g2.setFont(g2.getFont().deriveFont((float)(NODE_RADIUS * 2 / 3 *
		 * _zoom)));
		 * 
		 * FontMetrics m = g2.getFontMetrics();
		 * 
		 * g2.drawString(lbl, (int)(pos.x * _zoom - m.stringWidth(lbl) / 2),
		 * (int)(pos.y * _zoom + g2.getFont().getSize2D() / 2)); }
		 */
	}

	private void drawEdge(Edge e, Graphics g) {
		drawEdge(e, g, null, false);
	}

	private void drawEdge(Edge e, Graphics g, Color c, boolean drawArrow) {

		if (!shouldBeDrawn(e))
			return;

		if (c == null) {
			g.setColor(getColorForEntity(e, false));
		} else {
			g.setColor(c);
		}

		java.awt.Point fromPos = getCorrectedPosition(e.from);
		java.awt.Point toPos = getCorrectedPosition(e.to);
		int fromX = 0;
		int fromY = 0;
		int toX = 0;
		int toY = 0;

		java.awt.Point fromPoint = getCorrectedPointFromNode(fromPos.x,
				fromPos.y, toPos.x, toPos.y, NODE_RADIUS - 1);
		fromX = fromPoint.x;
		fromY = fromPoint.y;

		java.awt.Point toPoint = getCorrectedPointFromNode(toPos.x, toPos.y,
				fromPos.x, fromPos.y, NODE_RADIUS + 1);
		toX = toPoint.x;
		toY = toPoint.y;

		if (drawArrow) {
			drawArrow((Graphics2D) g.create(), fromX, fromY, toX, toY);
		} else {
			g.drawLine(fromX, fromY, toX, toY);
		}

		Graphics g2 = g.create();
		g2.setFont(g2.getFont().deriveFont(
				(float) (TRANSITION_WIDTH / 2 * _zoom)));
		String weightString = NumberFormat.getInstance(Locale.US).format(
				e.weight);

		float ratio = (float) Math.abs(fromX - toX)
				/ ((float) Math.abs(fromY - toY) + (float) 0.1);
		float flip = fromX < toX && fromY < toY || fromX > toX && fromY > toY ? (float) -1.0
				: (float) 1.0;

		g2.drawString(weightString, (int) ((fromX + toX) / 2 - TRANSITION_WIDTH
				/ 4 * _zoom + (1 / ratio > 1.5 ? 1 : 1 / ratio)
				* TRANSITION_WIDTH / 3 * _zoom), (int) ((fromY + toY) / 2
				+ flip * TRANSITION_WIDTH / 4 * _zoom + (ratio > 1.5 ? 1
				: ratio) * TRANSITION_WIDTH / 3 * _zoom));
	}

	void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
		double dx = x2 - x1, dy = y2 - y1;
		double angle = Math.atan2(dy, dx);
		int len = (int) Math.sqrt(dx * dx + dy * dy);
		g.translate(x1, y1);
		g.rotate(angle);

		// Draw horizontal arrow starting in (0, 0)
		g.drawLine(0, 0, (int) len, 0);
		g.fillPolygon(new int[] { len, (int) (len - ARR_SIZE * _zoom),
				(int) (len - ARR_SIZE * _zoom), len }, new int[] { 0,
				(int) (-ARR_SIZE * _zoom), (int) (ARR_SIZE * _zoom), 0 }, 4);
	}

	private java.awt.Point getCorrectedPointFromNode(double a, double b,
			double c, double d, double radius) {
		int fromX;
		int fromY;

		double sgn = 1.0;

		if (Math.abs(b - d) > 0.5) {
			sgn = Math.signum(c - a);
			fromX = (int) (_zoom * (a + radius
					/ Math.sqrt(1 + Math.pow(b - d, 2) / Math.pow(a - c, 2))
					* sgn));
		} else {
			fromX = (int) (_zoom * (a + Math.signum(c - a) * radius));
		}
		if (Math.abs(a - c) > 0.5) {
			sgn = Math.signum(d - b);
			fromY = (int) (_zoom * (b + radius
					/ Math.sqrt(1 + Math.pow(a - c, 2) / Math.pow(b - d, 2))
					* sgn));
		} else {
			fromY = (int) (_zoom * (b - Math.signum(b - d) * radius));
		}

		return new java.awt.Point(fromX, fromY);
	}

	/**
	 * Recalculates the minimum size required by the nodes of the graph and the
	 * zoom value. Also sets the minimum size so the scroll bars can be set.
	 */
	public void calculateMinSize() {
		Dimension minSize = new Dimension();

		Iterator<Node> nodeIt = _graph.getNodeList().iterator();

		double translateX = 0.0;
		double translateY = 0.0;

		while (nodeIt.hasNext()) {
			Node e = nodeIt.next();

			if (e.x - NODE_RADIUS - 5 < translateX) {
				translateX = e.x - NODE_RADIUS - 5;
			}
			if (e.y - NODE_RADIUS - 5 < translateY) {
				translateY = e.y - NODE_RADIUS - 5;
			}

			minSize.width = (int) Math.max(minSize.width,
					(e.x + NODE_RADIUS + 5) * _zoom);
			minSize.height = (int) Math.max(minSize.height,
					(e.y + NODE_RADIUS + 5) * _zoom);
		}

		if (translateX < 0.0) {
			translateX = -translateX;
		}
		if (translateY < 0.0) {
			translateY = -translateY;
		}

		_graph.translate(translateX, translateY);

		minSize.width += translateX;
		minSize.height += translateY;

		setPreferredSize(minSize);
		getParent().revalidate();
	}

	private java.awt.Point getCorrectedPosition(Node e) {
		java.awt.Point p = new java.awt.Point((int) e.x, (int) e.y);

		if (e.selected && _editor.isBeingDraggedAndDropped()) {
			p.x += _editor.dragAndDropDimension.width;
			p.y += _editor.dragAndDropDimension.height;
		}

		return p;
	}

	/**
	 * Gets the color of the entity given as the first parameter.
	 * 
	 * @param value
	 *            The entity value.
	 * @param lineColor
	 *            A boolean that means that we are asking the color to fill the
	 *            entity with, or not.
	 * @return The color.
	 */
	public Color getColorForEntity(Object value, Boolean lineColor) {
		Session s = EditorController.getCurrentSession();

		if (value instanceof Edge) {
			if (_editor.getEditorMode() == DocumentEditor.EditorMode.Simulation) {
				return Color.BLACK;
			} else
				return ((Edge) value).isSelected() ? Color.BLUE : Color.BLACK;
		} else if (value instanceof Node) {
			if (_editor.getEditorMode() == DocumentEditor.EditorMode.Simulation) {
				return Color.BLACK;
			} else if (s != null && s.sourceId == ((Node) value).id
					&& !lineColor) {
				return new Color(187, 215, 242);
			} else if (s != null
					&& s.destinationIds.contains(((Node) value).id)
					&& !lineColor) {
				return new Color(253, 176, 176);
			} else {
				if (!lineColor)
					return Color.WHITE;
				return ((Node) value).selected ? Color.BLUE : Color.BLACK;
			}
		}

		return Color.WHITE;
	}

	private boolean shouldBeDrawn(Edge e) {
		double z = getZoom();
		java.awt.Point from = getCorrectedPosition(e.from);
		java.awt.Point to = getCorrectedPosition(e.to);
		if (Math.sqrt(Math.pow(from.x * z - to.x * z, 2)
				+ Math.pow(from.y * z - to.y * z, 2)) < Math.max(
				TRANSITION_WIDTH, NODE_RADIUS * 2) * z)
			return false;

		return true;
	}

}
