package routing.view;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.ActionMap;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;
import routing.control.DocumentController;
import routing.control.EditorController;
import routing.RoutingDemo;

/**
 * The class of the menu bar. Not that interesting.
 * @author PIAPAAI.ELTE
 */
public class MenuBar extends JMenuBar {

    /**
     * Constructor.
     * @param frame
     */
    public MenuBar(MainFrame frame) {
        ApplicationContext c = Application.getInstance(RoutingDemo.class).getContext();
        ActionMap actionMap = c.getActionMap(MainFrame.class, frame);
        ActionMap fileActionMap = c.getActionMap(DocumentController.getInstance());
        ActionMap editorActionMap = c.getActionMap(EditorController.getInstance());
        ResourceMap rm = c.getResourceMap(MainFrame.class);
        ResourceMap fileRm = c.getResourceMap(DocumentController.class);

        JMenu menu = new JMenu(rm.getString("MainFrame.Menu.File.Text"));
        add(menu);

        JMenuItem menuItem = new JMenuItem(fileActionMap.get("newFileAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.New.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, ActionEvent.CTRL_MASK));
        menuItem.setIcon(fileRm.getIcon("newFileAction.Action.icon_small"));
        menu.add(menuItem);

        menuItem = new JMenuItem(fileActionMap.get("loadFileAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Load.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, ActionEvent.CTRL_MASK));
        menuItem.setIcon(fileRm.getIcon("loadFileAction.Action.icon_small"));
        menu.add(menuItem);

        menuItem = new JMenuItem(fileActionMap.get("reloadFileAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Reload.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, ActionEvent.CTRL_MASK));
        menuItem.setIcon(fileRm.getIcon("reloadFileAction.Action.icon_small"));
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(fileActionMap.get("saveFileAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Save.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK));
        menuItem.setIcon(fileRm.getIcon("saveFileAction.Action.icon_small"));
        menu.add(menuItem);

        menuItem = new JMenuItem(fileActionMap.get("saveFileAsAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.SaveAs.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.CTRL_MASK | ActionEvent.SHIFT_MASK));
        menuItem.setIcon(fileRm.getIcon("saveFileAsAction.Action.icon_small"));
        menu.add(menuItem);

        menuItem = new JMenuItem(fileActionMap.get("closeFileAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Close.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, ActionEvent.CTRL_MASK));
        menuItem.setIcon(fileRm.getIcon("closeFileAction.Action.icon_small"));
        menu.add(menuItem);

        menu.addSeparator();

        menuItem = new JMenuItem(actionMap.get("quit"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Exit.Text"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        menuItem.setIcon(fileRm.getImageIcon("quitAction.Action.icon_small"));
        menu.add(menuItem);

        menu = new JMenu(rm.getString("MainFrame.Menu.Edit.Text"));
        add(menu);

        menuItem = new JMenuItem(editorActionMap.get("copyAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Copy.Text"));
        menuItem.setIcon(fileRm.getImageIcon("copyAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("cutAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Cut.Text"));
        menuItem.setIcon(fileRm.getImageIcon("cutAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("pasteAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Paste.Text"));
        menuItem.setIcon(fileRm.getImageIcon("pasteAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("duplicateAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Duplicate.Text"));
        menuItem.setIcon(fileRm.getImageIcon("dulpicateAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_D, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("deleteAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.Delete.Text"));
        menuItem.setIcon(fileRm.getImageIcon("deleteAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        menu.add(menuItem);

        menu = new JMenu(rm.getString("MainFrame.Menu.Tools.Text"));
        add(menu);

        menuItem = new JMenuItem(editorActionMap.get("setSelectionEditorStateAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.SelectionTool.Text"));
        menuItem.setIcon(fileRm.getImageIcon("selectionAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_1, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("setAddNodeEditorStateAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.AddPointTool.Text"));
        menuItem.setIcon(fileRm.getImageIcon("addPointAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_2, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("setAddEdgeEditorStateAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.AddEdgeTool.Text"));
        menuItem.setIcon(fileRm.getImageIcon("addEdgeAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_3, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("setSetPropertiesEditorStateAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.SetPropertiesTool.Text"));
        menuItem.setIcon(fileRm.getImageIcon("setPropertiesAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_4, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menuItem = new JMenuItem(editorActionMap.get("simulationEditorStateAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.SimulationTool.Text"));
        menuItem.setIcon(fileRm.getImageIcon("simulateAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_5, ActionEvent.CTRL_MASK));
        menu.add(menuItem);

        menu = new JMenu(rm.getString("MainFrame.Menu.Help.Text"));
        add(menu);

        menuItem = new JMenuItem(actionMap.get("showAboutDialogAction"));
        menuItem.setText(rm.getString("MainFrame.MenuItem.About.Text"));
        menuItem.setIcon(fileRm.getImageIcon("showAboutDialogAction.Action.icon_small"));
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F1, 0));

        menu.add(menuItem);
    }
}
