package routing.control;

import java.util.Collection;
import java.util.LinkedList;
import org.jdesktop.application.Action;
import routing.control.entities.Node;
import routing.control.entities.Graph;
import routing.util.GraphUtil;
import routing.view.MainFrame;
import routing.view.Toolbar;
import routing.view.editor.Canvas;
import routing.view.editor.DocumentEditor;
import routing.RoutingDemo;

/**
 *
 * @author PIAPAAI.ELTE
 */
public class EditorController {


    /**
     * The singleton reference to the only instance of the controller class.
     */
    private static EditorController _instance;

    /**
     * Gets the singleton reference to the only instance of the controller class.
     */
    public static EditorController getInstance(){

        if(_instance == null)
            _instance = new EditorController();

        return _instance;
    }

    /**
     * Constructor
     */
    private EditorController() {}



    /**
     * Sets the current editor state to "Selection".
     */
    @Action
    public void setSelectionEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnSelection.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnSelection.setSelected(true);
        }
        mainFrame.setCurrentMode(DocumentEditor.EditorMode.Selection);
    }

    /**
     * Sets the current editor state to "add nodes".
     */
    @Action
    public void setAddNodeEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnAddNode.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnAddNode.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.AddNode);
    }

    /**
     * Sets the current editor state to "add edges".
     */
    @Action
    public void setAddEdgeEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnAddEdge.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnAddEdge.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.AddEdge);
    }

    /**
     * Sets the current editor state to "set properties".
     */
    @Action
    public void setSetPropertiesEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnSetProperties.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnSetProperties.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.SetProperties);
    }

    /**
     * Sets the current editor state to "simulation".
     */
    @Action
    public void simulationEditorStateAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        if(!((Toolbar)mainFrame.getToolBar()).btnSimulate.isSelected())
        {
            ((Toolbar)mainFrame.getToolBar()).btnSimulate.setSelected(true);
        }

        mainFrame.setCurrentMode(DocumentEditor.EditorMode.Simulation);
    }

    /**
     * Copies the selection of the currently selected editor to the clipboard.
     */
    @Action
    public void copyAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        GraphUtil.toClipBoard(mainFrame.getCurrentPage().net.getSelection());
    }

    /**
     * Cuts the selection of the currently selected editor to the clipboard.
     */
    @Action
    public void cutAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        GraphUtil.toClipBoard(mainFrame.getCurrentPage().net.getSelection(true));
        mainFrame.getCurrentEditor().repaint();
    }

    /**
     * Pastes the contents of the clipboard to the currently selected editor.
     */
    @Action
    public void pasteAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        Graph net = GraphUtil.fromClipboard();
        mainFrame.getCurrentPage().net.addAll(net);
        Collection<Node> selected = new LinkedList<Node>();
        selected.addAll(net.getNodeList());
        mainFrame.getCurrentEditor().setSelection(selected);
        mainFrame.getCurrentEditor().repaint();
    }

    /**
     * Duplicates the selection.
     */
    @Action
    public void duplicateAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        Graph net = mainFrame.getCurrentPage().net.getSelection(false);
        net.translate(Canvas.TRANSITION_WIDTH, Canvas.TRANSITION_WIDTH);
        mainFrame.getCurrentPage().net.addAll(net);
        Collection<Node> selected = new LinkedList<Node>();
        selected.addAll(net.getNodeList());
        mainFrame.getCurrentEditor().setSelection(selected);
        mainFrame.getCurrentEditor().repaint();
    }

    /**
     * Deletes all items in the selection.
     */
    @Action
    public void deleteAction()
    {
        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
        mainFrame.getCurrentPage().net.getSelection(true);
        mainFrame.getCurrentEditor().repaint();
        mainFrame.sessionPanel.checkActionsState();
    }


}
