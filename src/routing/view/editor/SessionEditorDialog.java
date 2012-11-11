package routing.view.editor;

import java.awt.Frame;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.ActionMap;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JTextField;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import routing.RoutingDemo;
import routing.control.Document;
import routing.control.DocumentController;
import routing.control.EditorController;
import routing.control.ErrorController;
import routing.control.entities.Node;
import routing.control.entities.Session;
import routing.view.MainFrame;
import routing.view.SessionPanel;
import routing.view.editor.DocumentEditor.EditorMode;

public class SessionEditorDialog extends JDialog implements ComponentListener {
	
	public EditorMode lastMode = EditorMode.Selection;
	
	public static enum EditorState {
		SelectSourceNode,
		SelectDestinationNodes
	}
	
	private static SessionEditorDialog instance;

	public static SessionEditorDialog getInstance() {
		return instance;
	}
	
	private static boolean shown = false;
	
	public static boolean isShown() {
		return shown;
	}
	
	private JButton okButton;
	private JButton cancelButton;

	private JLabel idLabel;
	private JTextField idField;

	private JLabel nameLabel;
	private JTextField nameField;

	private JLabel weightLabel;
	private JTextField weightField;

	private JLabel batchCountLabel;
	private JTextField batchCountField;

	private JLabel statusLabel;

	private Session _session;
	
	private EditorState currentState;
	
	public void setState(EditorState s) {
		currentState = s;
		
		if(statusLabel != null) {
			ApplicationContext c = Application.getInstance(RoutingDemo.class)
					.getContext();
			ResourceMap rm = c.getResourceMap(SessionEditorDialog.class);
			if (s == EditorState.SelectSourceNode) {
				statusLabel.setText(rm.getString("SessionEditorDialog.JLabel.status.Text_source"));
			}else if (s == EditorState.SelectDestinationNodes) {
				statusLabel.setText(rm.getString("SessionEditorDialog.JLabel.status.Text_destinations"));
			}
		}
	}

	public Session getSession() {
		return _session;
	}

	public void setSession(Session s) {
		_session = s;

		if (_session != null) {
			idField.setText(Integer.toString(_session.id));
			nameField.setText(_session.name);
			weightField.setText(Integer.toString(_session.weight));
			batchCountField.setText(Integer.toString(_session.batchCount));
		}
	}

	public SessionEditorDialog(Frame owner) {
		super(owner);

		initializeView();
		
		instance = this;
		addComponentListener(this);
		setModal(false);
	}

	private void initializeView() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ActionMap actionMap = c.getActionMap(SessionEditorDialog.class, this);
		ResourceMap rm = c.getResourceMap(SessionEditorDialog.class);

		setModal(true);
		setResizable(false);
		setTitle(rm.getString("SessionEditorDialog.Title"));

		idLabel = new JLabel(
				rm.getString("SessionEditorDialog.JLabel.id.Text"));
		nameLabel = new JLabel(
				rm.getString("SessionEditorDialog.JLabel.name.Text"));
		weightLabel = new JLabel(
				rm.getString("SessionEditorDialog.JLabel.weight.Text"));
		batchCountLabel = new JLabel(
				rm.getString("SessionEditorDialog.JLabel.batchCount.Text"));
		statusLabel = new JLabel(
				rm.getString("SessionEditorDialog.JLabel.status.Text_source"));
		
		idField = new JTextField();
		idField.setSize(10, idField.getSize().height);
		idField.setEnabled(false);

		nameField = new JTextField();
		nameField.setSize(10, idField.getSize().height);

		weightField = new JTextField();
		weightField.setSize(10, idField.getSize().height);

		batchCountField = new JTextField();
		batchCountField.setSize(10, idField.getSize().height);

		okButton = new JButton(actionMap.get("commitChangesAction"));
		okButton.setText(rm.getString("SessionEditorDialog.JButton.ok.Text"));

		cancelButton = new JButton(actionMap.get("closeDialogAction"));
		cancelButton.setText(rm
				.getString("SessionEditorDialog.JButton.cancel.Text"));

		GroupLayout layout = new GroupLayout(getContentPane());
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		getContentPane().setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(idLabel)
										.addComponent(idField))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(nameLabel)
										.addComponent(nameField))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(weightLabel)
										.addComponent(weightField))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(batchCountLabel)
										.addComponent(batchCountField))
						.addComponent(statusLabel)
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(okButton)
										.addComponent(cancelButton))));
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(idLabel)
										.addComponent(idField, 150, 150, 150))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(nameLabel)
										.addComponent(nameField, 150, 150, 150))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(weightLabel)
										.addComponent(weightField, 150, 150, 150))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(batchCountLabel)
										.addComponent(batchCountField, 150, 150, 150))
						.addComponent(statusLabel, GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(okButton)
										.addComponent(cancelButton))));

		getRootPane().setDefaultButton(okButton);
	}
	
	public void setSourceId(int id) {
		_session.sourceId = id;
	}
	
	public void toggleDestinationId(int id) {
		if(_session.destinationIds.contains(id)) {
			_session.destinationIds.remove((Object)id);
		} else {
			_session.destinationIds.add(id);
		}
	}

	@Action
	public void closeDialogAction() {
		closeDialog();
		SessionPanel p = RoutingDemo.getMF().sessionPanel;
		if(p.getSelectedSession() != null) {
			p.selectSession(p.getSelectedSession().id);
		}
	}

	@Action
	public void commitChangesAction() {
		
		if(currentState == EditorState.SelectSourceNode) {
			if(_session.sourceId > 0) {
				MainFrame mf = RoutingDemo.getMF();
				mf.getCurrentEditor().setEditorMode(EditorMode.SelectDestinationNodes);
				setState(EditorState.SelectDestinationNodes);
			} else {
				ErrorController.showError("Please select a source node!", "Error");
			}
		} else if(currentState == EditorState.SelectDestinationNodes) {
			if(_session.destinationIds.size() > 0) {
				_session.name = nameField.getText();

				String actField = "weight";
				
				try {
					_session.weight = Integer.parseInt(weightField.getText());
					actField = "batch count";
					_session.batchCount = Integer.parseInt(batchCountField.getText());
				} catch (Exception e) {
					ErrorController.showError("Invalid " + actField + " format!", "Error");
					return;
				}
		
				Document d = DocumentController.getInstance().getCurrentDocument();
				d.sessions.add(_session);
				RoutingDemo.getMF().sessionPanel.setDocument(d);
				closeDialog();
				RoutingDemo.getMF().sessionPanel.selectSession(_session.id);
			} else {
				ErrorController.showError("Please select at least one desrtination node!", "Error");
			}
		}
	}
	
	public void showDialog() {
		MainFrame mf = RoutingDemo.getMF();
		lastMode = mf.getCurrentEditor().getEditorMode();
		mf.getCurrentEditor().setEditorMode(EditorMode.SelectSourceNode);
		RoutingDemo.getApplication().show(this);
		shown = true;
		mf.checkActionsState();

		getRootPane().setDefaultButton(okButton);
	}
	
	private void closeDialog() {
		dispose();
		RoutingDemo.getApplication().getMainFrame().repaint();
		MainFrame mf = RoutingDemo.getMF();
		mf.getCurrentEditor().setEditorMode(lastMode);
		shown = false;
		mf.checkActionsState();
	}

	@Override
	public void componentHidden(ComponentEvent arg0) {
		closeDialogAction();
	}

	@Override
	public void componentMoved(ComponentEvent arg0) { }

	@Override
	public void componentResized(ComponentEvent arg0) { }

	@Override
	public void componentShown(ComponentEvent arg0) { }


}
