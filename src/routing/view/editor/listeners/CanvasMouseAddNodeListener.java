package routing.view.editor.listeners;

import java.awt.event.MouseEvent;

import routing.RoutingDemo;
import routing.control.entities.Node;
import routing.control.entities.Graph;

import routing.view.MainFrame;
import routing.view.editor.DocumentEditor;

/**
 * A mouse listener of the canvas that handles edition of nodes.
 * @author PIAPAAI.ELTE
 */
public class CanvasMouseAddNodeListener extends CanvasMouseListener {


    public CanvasMouseAddNodeListener(DocumentEditor editor)
    {
        super(editor);
    }



    /**
     * Mouse click handler method.
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {

        handleDragAndDropEnd(e);
    }

    /**
     * Mouse press handler method.
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {

        if(handleDragAndDropStart(e))
            return;

        Graph net = _editor.getDocument() != null && _editor.getDocument().graph != null ? _editor.getDocument().graph : null;

        if(net != null && _editor.canvas.nodeToAdd == null)
        {
            Node newNode = new Node(net);
            newNode.x = (double)e.getX() / _editor.canvas.getZoom();
            newNode.y = (double)e.getY() / _editor.canvas.getZoom();
            _editor.canvas.nodeToAdd = newNode;

            if(e.isControlDown() || e.isShiftDown())
                _editor.addToSelection(_editor.canvas.nodeToAdd);
            else if(!e.isAltDown())
                _editor.setSelection(_editor.canvas.nodeToAdd);

            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse drag handler method
     * @param e
     */
    @Override
    public void mouseDragged(MouseEvent e) {

        if(handleDragAndDrop(e))
            return;

        if(_editor.canvas.nodeToAdd != null)
        {
            _editor.canvas.nodeToAdd.x = (double)e.getX() / _editor.canvas.getZoom();
            _editor.canvas.nodeToAdd.y = (double)e.getY() / _editor.canvas.getZoom();
            _editor.canvas.calculateMinSize();
            _editor.canvas.repaint();
        }
    }

    /**
     * Mouse release handler method.
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {

        if(_editor.canvas.nodeToAdd == null && handleDragAndDropEnd(e))
            return;

        Graph net = _editor.getDocument() != null && _editor.getDocument().graph != null ? _editor.getDocument().graph : null;

        if(net != null && _editor.canvas.nodeToAdd != null)
        {
            _editor.canvas.nodeToAdd.x = (double)e.getX() / _editor.canvas.getZoom();
            _editor.canvas.nodeToAdd.y = (double)e.getY() / _editor.canvas.getZoom();

            net.addNode(_editor.canvas.nodeToAdd);
            
            MainFrame mainFrame = RoutingDemo.getMF();
            mainFrame.sessionPanel.checkActionsState();

            _editor.canvas.nodeToAdd = null;
            _editor.canvas.calculateMinSize();
        }

        super.mouseReleased(e);
    }


}
