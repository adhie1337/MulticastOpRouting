package routing.view;

import java.awt.TextField;

import javax.swing.ActionMap;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import routing.control.DocumentController;
import routing.control.EditorController;
import routing.control.SimulationController;
import routing.RoutingDemo;

/**
 * The class of the applications toolbar. Not that interesting.
 * @author PIAPAAI.ELTE
 */
public class Toolbar extends JToolBar{

    private JButton btnNew;
    private JButton btnLoad;
    private JButton btnReload;
    private JButton btnSave;
    private JButton btnSaveAs;
    private JButton btnClose;
    private JButton btnExit;

    public JToggleButton btnSelection;
    public JToggleButton btnAddNode;
    public JToggleButton btnAddTransition;
    public JToggleButton btnAddEdge;
    public JToggleButton btnSetProperties;
    public JButton btnSimulate;

    private ButtonGroup btngGroup;

    public Boolean hasDocument;

    public Toolbar(MainFrame ownerFrame) {
        super();

        initEditor();
    }

    private void initEditor() {
        ApplicationContext c = Application.getInstance(RoutingDemo.class).getContext();
        ActionMap appActionMap = c.getActionMap(RoutingDemo.getApplication());
        ActionMap editorActionMap = c.getActionMap(EditorController.getInstance());
        ActionMap simulationActionMap = c.getActionMap(SimulationController.getInstance());
        ResourceMap fileRm = c.getResourceMap(DocumentController.class);
        ActionMap fileActionMap = c.getActionMap(DocumentController.getInstance());

        btnNew = new JButton();
        btnNew.setAction(fileActionMap.get("newFileAction"));
        add(btnNew);

        btnLoad = new JButton();
        btnLoad.setAction(fileActionMap.get("loadFileAction"));
        add(btnLoad);

        btnReload = new JButton();
        btnReload.setAction(fileActionMap.get("reloadFileAction"));
        add(btnReload);

        btnSave = new JButton();
        btnSave.setAction(fileActionMap.get("saveFileAction"));
        add(btnSave);

        btnSaveAs = new JButton();
        btnSaveAs.setAction(fileActionMap.get("saveFileAsAction"));
        add(btnSaveAs);

        btnClose = new JButton();
        btnClose.setAction(fileActionMap.get("closeFileAction"));
        add(btnClose);

        btnExit = new JButton();
        btnExit.setAction(appActionMap.get("quit"));
        btnExit.setText("");
        btnExit.setIcon(fileRm.getImageIcon("quitAction.Action.icon"));
        add(btnExit);

        addSeparator();

        btngGroup = new ButtonGroup();

        btnSelection = new JToggleButton();
        btnSelection.setAction(editorActionMap.get("setSelectionEditorStateAction"));
        btnSelection.setText("");
        btnSelection.setIcon(fileRm.getImageIcon("selectionAction.Action.icon"));
        btnSelection.setSelected(true);
        btngGroup.add(btnSelection);
        add(btnSelection);

        btnAddNode = new JToggleButton();
        btnAddNode.setAction(editorActionMap.get("setAddNodeEditorStateAction"));
        btnAddNode.setText("");
        btnAddNode.setIcon(fileRm.getImageIcon("addPointAction.Action.icon"));
        btngGroup.add(btnAddNode);
        add(btnAddNode);

        btnAddEdge = new JToggleButton();
        btnAddEdge.setAction(editorActionMap.get("setAddEdgeEditorStateAction"));
        btnAddEdge.setText("");
        btngGroup.add(btnAddEdge);
        btnAddEdge.setIcon(fileRm.getImageIcon("addEdgeAction.Action.icon"));
        add(btnAddEdge);

        btnSetProperties = new JToggleButton();
        btnSetProperties.setAction(editorActionMap.get("setSetPropertiesEditorStateAction"));
        btnSetProperties.setText("");
        btngGroup.add(btnSetProperties);
        btnSetProperties.setIcon(fileRm.getImageIcon("setPropertiesAction.Action.icon"));
        add(btnSetProperties);

        btnSimulate = new JButton();
        btnSimulate.setAction(simulationActionMap.get("showSimulationDialogAction"));
        btnSimulate.setText("");
        btngGroup.add(btnSimulate);
        btnSimulate.setIcon(fileRm.getImageIcon("simulateAction.Action.icon"));
        add(btnSimulate);
    }

}
