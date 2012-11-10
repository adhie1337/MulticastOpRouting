package routing.view;

import routing.view.editor.DocumentEditor;
import javax.swing.event.ChangeEvent;
import javax.swing.ActionMap;
import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.FrameView;
import routing.control.DocumentController;
import routing.control.Document;
import routing.RoutingDemo;

/**
 * The main window of the application. Can handle multiple editors.
 * @author PIAPAAI.ELTE
 */
public class MainFrame extends FrameView implements ChangeListener {

    private AboutDialog aboutDialog;

    public JTabbedPane tabbedPane;

    private DocumentEditor.EditorMode _currentMode = DocumentEditor.EditorMode.Selection;

    /**
     * Constructor.
     * @param app The application that is currently running.
     */
    public MainFrame(Application app) {
        super(app);

        initFrame();
    }

    /**
     * opens a new editor page and sets it up for the given Petri net.
     * Also shows the editor in the view.
     * @param document the document that is being set to the new editor instance
     * @return the instance of the newly created editor.
     */
    public DocumentEditor getNewPage(Document document)
    {
        DocumentEditor panel = new DocumentEditor(document);
        panel.setEditorMode(_currentMode);
        tabbedPane.add(panel);
        int i = tabbedPane.indexOfComponent(panel);
        tabbedPane.setTitleAt(i, document.documentName);
        tabbedPane.setSelectedIndex(i);

        checkActionsState();

        return panel;
    }

    /**
     * Gets the editor that is being shown.
     */
    public DocumentEditor getCurrentEditor()
    {
        if(tabbedPane.getSelectedComponent() instanceof DocumentEditor) {
            return (DocumentEditor)tabbedPane.getSelectedComponent();
        }
        return null;
    }

    /**
     * Selects the editor that is editing the document given as the parameter.
     * @param document
     * @return the document instance when found, null otherwise.
     */
    public DocumentEditor setCurrentPage(Document document)
    {
        if(tabbedPane.getSelectedComponent() instanceof DocumentEditor
                && ((DocumentEditor)tabbedPane.getSelectedComponent()).getDocument().equals(document)) {
            return (DocumentEditor)tabbedPane.getSelectedComponent();
        }

        for(int i = 0; i < tabbedPane.getTabCount(); ++i)
        {
            DocumentEditor editor = (DocumentEditor)tabbedPane.getComponentAt(i);
            editor.setEditorMode(_currentMode);

            if(editor != null && editor.getDocument().equals(document))
            {
                tabbedPane.setSelectedIndex(i);
                checkActionsState();

                return editor;
            }
        }

        checkActionsState();

        return null;
    }

    /**
     * Sets the currently selected editors behavior.
     * @param value the mode to set to.
     * @see DocumentEditor.EditorMode
     */
    public void setCurrentMode(DocumentEditor.EditorMode value)
    {
        if(_currentMode != value)
        {
            _currentMode = value;

            for(int i = 0; tabbedPane != null && i < tabbedPane.getTabCount(); ++i)
            {
                DocumentEditor editor = (DocumentEditor)tabbedPane.getComponentAt(i);
                editor.setEditorMode(_currentMode);
            }
        }
    }

    /**
     * returns the current "Editor mode". The setting that the editor behaves by.
     */
    public DocumentEditor.EditorMode getCurrentMode()
    {
        return _currentMode;
    }

    /**
     * Removes an editor from the currently opened editors.
     * @param document the document that is edited by the editor. This identifies the editor.
     */
    public void removePage(Document document)
    {
        for(int i = 0; i < tabbedPane.getTabCount(); ++i)
        {
            DocumentEditor editor = (DocumentEditor)tabbedPane.getComponentAt(i);

            if(editor.getDocument() == document)
            {
                tabbedPane.remove(editor);
                break;
            }
        }

        checkActionsState();
    }

    /**
     * Returns the document that is currently edited in the front.
     */
    public Document getCurrentPage() {
        return ((DocumentEditor)tabbedPane.getSelectedComponent()).getDocument();
    }

    /**
     * Updates the current editor tab label to the name of the Petri net document instance.
     */
    public void updateCurrentLabel()
    {
        Document document = ((DocumentEditor)tabbedPane.getSelectedComponent()).getDocument();
        tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(), document.documentName);
    }

    /**
     * Returns the toolbar instance.
     */
    private Toolbar getToolbar()
    {
        return (Toolbar)getToolBar();
    }

    /**
     * Changes the current document to the one inside the current file.
     * @param e
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        if(tabbedPane.getTabCount() > 1)
        {
            DocumentController.getInstance().changeCurrentFileAction();
        }
    }

    private void initFrame() {
        setMenuBar(new PetrinetMenuBar(this));

        tabbedPane = new JTabbedPane(JTabbedPane.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        tabbedPane.addChangeListener(this);

        setComponent(tabbedPane);

        setToolBar(new Toolbar(this));
    }

    private void checkActionsState() {

        ApplicationContext c = Application.getInstance(RoutingDemo.class).getContext();
        ActionMap fileActionMap = c.getActionMap(DocumentController.getInstance());

        if(tabbedPane.getTabCount() == 1)
        {
            DocumentEditor editor = (DocumentEditor)tabbedPane.getComponentAt(0);

            if(editor.getDocument() == null
                    || editor.getDocument().filePath == null
                    || editor.getDocument().filePath.equals(""))
            {
                fileActionMap.get("closeFileAction").setEnabled(false);
                fileActionMap.get("reloadFileAction").setEnabled(false);
            }
            else
            {
                fileActionMap.get("closeFileAction").setEnabled(true);
                fileActionMap.get("reloadFileAction").setEnabled(true);
            }
        }
        else if(tabbedPane.getTabCount() == 0)
        {
            fileActionMap.get("closeFileAction").setEnabled(false);
            fileActionMap.get("reloadFileAction").setEnabled(false);
        }
        else
        {
            fileActionMap.get("closeFileAction").setEnabled(true);
            fileActionMap.get("reloadFileAction").setEnabled(true);
        }
    }

    /**
     * An action to show the about dialog.
     */
    @Action
    public void showAboutDialogAction()
    {
        if (aboutDialog == null) {
            JFrame mainFrame = RoutingDemo.getApplication().getMainFrame();
            aboutDialog = new AboutDialog(mainFrame);
            aboutDialog.setLocationRelativeTo(mainFrame);
        }

        RoutingDemo.getApplication().show(aboutDialog);
    }

}
