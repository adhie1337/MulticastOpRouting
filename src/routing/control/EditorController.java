package routing.control;

import java.util.Collection;
import java.util.LinkedList;
import org.jdesktop.application.Action;
import routing.control.entities.Node;
import routing.control.entities.Graph;
import routing.control.entities.Session;
import routing.util.GraphUtil;
import routing.view.MainFrame;
import routing.view.Toolbar;
import routing.view.editor.Canvas;
import routing.view.editor.DocumentEditor;
import routing.view.editor.NodePropertiesDialog;
import routing.view.editor.SessionEditorDialog;
import routing.view.editor.SessionEditorDialog.EditorState;
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
        if(DocumentController.getInstance().getCurrentDocument().sessions.size() == 0) {
	        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
	        GraphUtil.toClipBoard(mainFrame.getCurrentPage().net.getSelection(true));
	        mainFrame.getCurrentEditor().repaint();
	    } else {
	    	copyAction();
	    	ErrorController.showError("You can't delete nodes when there are sessions!", "Error");
	    }
    }

    /**
     * Pastes the contents of the clipboard to the currently selected editor.
     */
    @Action
    public void pasteAction()
    {
        if(DocumentController.getInstance().getCurrentDocument().sessions.size() == 0) {
	        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
	        Graph net = GraphUtil.fromClipboard();
	        mainFrame.getCurrentPage().net.addAll(net);
	        Collection<Node> selected = new LinkedList<Node>();
	        selected.addAll(net.getNodeList());
	        mainFrame.getCurrentEditor().setSelection(selected);
	        mainFrame.getCurrentEditor().repaint();
	    } else {
	    	ErrorController.showError("You can't add nodes when there are sessions!", "Error");
	    }
    }

    /**
     * Duplicates the selection.
     */
    @Action
    public void duplicateAction()
    {
        if(DocumentController.getInstance().getCurrentDocument().sessions.size() == 0) {
	        MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
	        Graph net = mainFrame.getCurrentPage().net.getSelection(false);
	        net.translate(Canvas.TRANSITION_WIDTH, Canvas.TRANSITION_WIDTH);
	        mainFrame.getCurrentPage().net.addAll(net);
	        Collection<Node> selected = new LinkedList<Node>();
	        selected.addAll(net.getNodeList());
	        mainFrame.getCurrentEditor().setSelection(selected);
	        mainFrame.getCurrentEditor().repaint();
	    } else {
	    	ErrorController.showError("You can't add nodes when there are sessions!", "Error");
	    }
    }

    /**
     * Deletes all items in the selection.
     */
    @Action
    public void deleteAction()
    {
        if(DocumentController.getInstance().getCurrentDocument().sessions.size() == 0) {
            MainFrame mainFrame = (MainFrame)RoutingDemo.getApplication().getMainView();
	        mainFrame.getCurrentPage().net.getSelection(true);
	        mainFrame.getCurrentEditor().repaint();
        	mainFrame.sessionPanel.checkActionsState();
        } else {
        	ErrorController.showError("You can't delete nodes when there are sessions!", "Error");
        }
    }

    private SessionEditorDialog sessionEditor;
    
    @Action
    public void showSessionEditorAction() {
		if (sessionEditor == null) {
			sessionEditor = new SessionEditorDialog(RoutingDemo
					.getApplication().getMainFrame());
		}
		
		sessionEditor.setState(EditorState.SelectSourceNode);

		Session s = new Session();
		s.id = 42;
		s.name = "session 1";
		s.sourceId = 1;
		s.destinationIds.add(2);
		s.destinationIds.add(3);
		s.weight = 1;
		sessionEditor.setSession(s);
		RoutingDemo.getApplication().show(sessionEditor);
    }
}
