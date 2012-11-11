package routing.view;

import java.awt.BorderLayout;

import routing.view.editor.DocumentEditor;
import routing.view.editor.DocumentEditor.EditorMode;
import routing.view.editor.SessionEditorDialog;

import javax.swing.event.ChangeEvent;
import javax.swing.ActionMap;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeListener;
import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.FrameView;
import routing.control.DocumentController;
import routing.control.Document;
import routing.control.EditorController;
import routing.control.SimulationController;
import routing.RoutingDemo;

/**
 * The main window of the application. Can handle multiple editors.
 * 
 * @author PIAPAAI.ELTE
 */
public class MainFrame extends FrameView implements ChangeListener {

	private AboutDialog aboutDialog;

	public JTabbedPane tabbedPane;

	public SessionPanel sessionPanel;

	private DocumentEditor.EditorMode _currentMode = DocumentEditor.EditorMode.Selection;

	/**
	 * Constructor.
	 * 
	 * @param app
	 *            The application that is currently running.
	 */
	public MainFrame(Application app) {
		super(app);

		initFrame();
	}

	/**
	 * Opens a new editor page and sets it up for the given graph. Also shows
	 * the editor in the view.
	 * 
	 * @param document
	 *            the document that is being set to the new editor instance
	 * @return the instance of the newly created editor.
	 */
	public DocumentEditor getNewPage(Document document) {
		DocumentEditor panel = new DocumentEditor(document);
		panel.setEditorMode(_currentMode);
		tabbedPane.add(panel);
		int i = tabbedPane.indexOfComponent(panel);
		tabbedPane.setTitleAt(i, document.documentName);
		tabbedPane.setSelectedIndex(i);
		sessionPanel.setDocument(document);

		checkActionsState();

		return panel;
	}

	/**
	 * Gets the editor that is being shown.
	 */
	public DocumentEditor getCurrentEditor() {
		if (tabbedPane.getSelectedComponent() instanceof DocumentEditor) {
			return (DocumentEditor) tabbedPane.getSelectedComponent();
		}
		return null;
	}

	/**
	 * Selects the editor that is editing the document given as the parameter.
	 * 
	 * @param document
	 * @return the document instance when found, null otherwise.
	 */
	public DocumentEditor setCurrentPage(Document document) {
		if (tabbedPane.getSelectedComponent() instanceof DocumentEditor
				&& ((DocumentEditor) tabbedPane.getSelectedComponent())
						.getDocument().equals(document)) {
			return (DocumentEditor) tabbedPane.getSelectedComponent();
		}

		for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
			DocumentEditor editor = (DocumentEditor) tabbedPane
					.getComponentAt(i);
			editor.setEditorMode(_currentMode);

			if (editor != null && editor.getDocument().equals(document)) {
				tabbedPane.setSelectedIndex(i);
				sessionPanel.setDocument(document);
				checkActionsState();

				return editor;
			}
		}

		checkActionsState();

		return null;
	}

	/**
	 * Sets the currently selected editors behavior.
	 * 
	 * @param value
	 *            the mode to set to.
	 * @see DocumentEditor.EditorMode
	 */
	public void setCurrentMode(DocumentEditor.EditorMode value) {
		if (_currentMode != value) {
			_currentMode = value;

			for (int i = 0; tabbedPane != null && i < tabbedPane.getTabCount(); ++i) {
				DocumentEditor editor = (DocumentEditor) tabbedPane
						.getComponentAt(i);
				editor.setEditorMode(_currentMode);
			}
		}
	}

	/**
	 * returns the current "Editor mode". The setting that the editor behaves
	 * by.
	 */
	public DocumentEditor.EditorMode getCurrentMode() {
		return _currentMode;
	}

	/**
	 * Removes an editor from the currently opened editors.
	 * 
	 * @param document
	 *            the document that is edited by the editor. This identifies the
	 *            editor.
	 */
	public void removePage(Document document) {
		for (int i = 0; i < tabbedPane.getTabCount(); ++i) {
			DocumentEditor editor = (DocumentEditor) tabbedPane
					.getComponentAt(i);

			if (editor.getDocument() == document) {
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
		return ((DocumentEditor) tabbedPane.getSelectedComponent())
				.getDocument();
	}

	/**
	 * Updates the current editor tab label to the name of the document
	 * instance.
	 */
	public void updateCurrentLabel() {
		Document document = ((DocumentEditor) tabbedPane.getSelectedComponent())
				.getDocument();
		tabbedPane.setTitleAt(tabbedPane.getSelectedIndex(),
				document.documentName);
	}

	/**
	 * Returns the toolbar instance.
	 */
	private Toolbar getToolbar() {
		return (Toolbar) getToolBar();
	}

	/**
	 * Changes the current document to the one inside the current file.
	 * 
	 * @param e
	 */
	@Override
	public void stateChanged(ChangeEvent e) {
		if (tabbedPane.getTabCount() > 1) {
			DocumentController.getInstance().changeCurrentFileAction();
		}

		sessionPanel.setDocument(getCurrentPage());
	}

	private void initFrame() {
		setMenuBar(new MenuBar(this));

		tabbedPane = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.addChangeListener(this);

		sessionPanel = new SessionPanel();

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(tabbedPane, BorderLayout.CENTER);
		mainPanel.add(sessionPanel, BorderLayout.EAST);
		setComponent(mainPanel);

		setToolBar(new Toolbar(this));
	}

	public void checkActionsState() {

		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ActionMap fileActionMap = c.getActionMap(DocumentController
				.getInstance());
		ActionMap editorActionMap = c.getActionMap(EditorController
				.getInstance());
		ActionMap simuationActionMap = c.getActionMap(SimulationController
				.getInstance());

		if (tabbedPane.getTabCount() == 1) {
			DocumentEditor editor = (DocumentEditor) tabbedPane
					.getComponentAt(0);

			if (editor.getDocument() == null
					|| editor.getDocument().filePath == null
					|| editor.getDocument().filePath.equals("")) {
				fileActionMap.get("closeFileAction").setEnabled(false);
				fileActionMap.get("reloadFileAction").setEnabled(false);
			} else {
				fileActionMap.get("closeFileAction").setEnabled(
						!SessionEditorDialog.isShown());
				fileActionMap.get("reloadFileAction").setEnabled(
						!SessionEditorDialog.isShown());
			}
		} else if (tabbedPane.getTabCount() == 0) {
			fileActionMap.get("closeFileAction").setEnabled(false);
			fileActionMap.get("reloadFileAction").setEnabled(false);
		} else {
			fileActionMap.get("closeFileAction").setEnabled(
					!SessionEditorDialog.isShown());
			fileActionMap.get("reloadFileAction").setEnabled(
					!SessionEditorDialog.isShown());
		}

		fileActionMap.get("loadFileAction").setEnabled(
				!SessionEditorDialog.isShown());
		fileActionMap.get("newFileAction").setEnabled(
				!SessionEditorDialog.isShown());

		boolean enableEditing = getCurrentPage().sessions.size() == 0
				&& !SessionEditorDialog.isShown();
		editorActionMap.get("setSelectionEditorStateAction").setEnabled(
				!SessionEditorDialog.isShown());
		editorActionMap.get("setAddNodeEditorStateAction").setEnabled(
				enableEditing);
		editorActionMap.get("setAddEdgeEditorStateAction").setEnabled(
				enableEditing);
		editorActionMap.get("setSetPropertiesEditorStateAction").setEnabled(
				!SessionEditorDialog.isShown());
		editorActionMap.get("cutAction").setEnabled(enableEditing);
		editorActionMap.get("pasteAction").setEnabled(enableEditing);
		editorActionMap.get("duplicateAction").setEnabled(enableEditing);
		editorActionMap.get("deleteAction").setEnabled(enableEditing);
		simuationActionMap.get("showSimulationDialogAction").setEnabled(
				getCurrentPage().sessions.size() > 0
						&& !SessionEditorDialog.isShown());

		if (!enableEditing
				&& getCurrentEditor().getEditorMode() == EditorMode.AddNode
				&& getCurrentEditor().getEditorMode() == EditorMode.AddEdge) {
			getCurrentEditor().setEditorMode(EditorMode.Selection);
		}

		tabbedPane.setEnabled(!SessionEditorDialog.isShown());
		sessionPanel.setEnabled(!SessionEditorDialog.isShown());
	}

	/**
	 * An action to show the about dialog.
	 */
	@Action
	public void showAboutDialogAction() {
		if (aboutDialog == null) {
			JFrame mainFrame = RoutingDemo.getApplication().getMainFrame();
			aboutDialog = new AboutDialog(mainFrame);
			aboutDialog.setLocationRelativeTo(mainFrame);
		}

		RoutingDemo.getApplication().show(aboutDialog);
	}

}
