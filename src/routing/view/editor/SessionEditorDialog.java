package routing.view.editor;

import java.awt.Frame;

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
import routing.control.ErrorController;
import routing.control.entities.Node;
import routing.control.entities.Session;

public class SessionEditorDialog extends JDialog {
	
	public static enum EditorState {
		SelectSourceNode,
		SelectDestinationNodes
	}

	private JButton okButton;
	private JButton cancelButton;

	private JLabel idLabel;
	private JTextField idField;

	private JLabel nameLabel;
	private JTextField nameField;

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
		}
	}

	public SessionEditorDialog(Frame owner) {
		super(owner);

		initializeView();
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
		statusLabel = new JLabel(
				rm.getString("SessionEditorDialog.JLabel.status.Text_source"));
		
		idField = new JTextField();
		idField.setSize(10, idField.getSize().height);
		idField.setEnabled(false);

		nameField = new JTextField();
		nameField.setSize(10, idField.getSize().height);

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
						.addComponent(statusLabel, GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(okButton)
										.addComponent(cancelButton))));

		getRootPane().setDefaultButton(okButton);
	}

	@Action
	public void closeDialogAction() {
		dispose();
	}

	@Action
	public void commitChangesAction() {
		
		if(currentState == EditorState.SelectSourceNode) {
			setState(EditorState.SelectDestinationNodes);
		} else if(currentState == EditorState.SelectDestinationNodes) {
			_session.name = nameField.getText();
	
			try {
				_session.id = Integer.parseInt(idField.getText());
			} catch (Exception e) {
				ErrorController.showError("Invalid id format!", "Error");
			}
	
			dispose();
	
			RoutingDemo.getApplication().getMainFrame().repaint();
		}
	}


}
