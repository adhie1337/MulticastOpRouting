package routing.view.editor;

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
import routing.control.ErrorController;
import routing.control.entities.Node;
import routing.RoutingDemo;

/**
 * A dialog to set the selected node's settings
 * 
 * @author PIAPAAI.ELTE
 */
public class NodePropertiesDialog extends JDialog {

	private JButton okButton;
	private JButton cancelButton;

	private JLabel nameLabel;
	private JTextField nameField;

	private JLabel labelLabel;
	private JTextField labelField;

	private Node _node;

	public Node getNode() {
		return _node;
	}

	public void setNode(Node t) {
		_node = t;

		if (_node != null) {
			nameField.setText(Integer.toString(_node.id));
			labelField.setText(_node.label);
		}
	}

	public NodePropertiesDialog(java.awt.Frame owner) {
		super(owner);

		initializeView();
	}

	private void initializeView() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ActionMap actionMap = c.getActionMap(NodePropertiesDialog.class, this);
		ResourceMap rm = c.getResourceMap(NodePropertiesDialog.class);

		setModal(true);
		setResizable(false);
		setTitle(rm.getString("NodePropertiesDialog.Title"));

		nameLabel = new JLabel(
				rm.getString("NodePropertiesDialog.JLabel.name.Text"));
		labelLabel = new JLabel(
				rm.getString("NodePropertiesDialog.JLabel.label.Text"));

		nameField = new JTextField();
		nameField.setSize(10, nameField.getSize().height);
		nameField.setEnabled(false);

		labelField = new JTextField();
		labelField.setSize(10, nameField.getSize().height);

		okButton = new JButton(actionMap.get("commitChangesAction"));
		okButton.setText(rm.getString("NodePropertiesDialog.JButton.ok.Text"));

		cancelButton = new JButton(actionMap.get("closeDialogAction"));
		cancelButton.setText(rm
				.getString("NodePropertiesDialog.JButton.cancel.Text"));

		GroupLayout layout = new GroupLayout(getContentPane());
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		getContentPane().setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(nameLabel)
										.addComponent(nameField))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(labelLabel)
										.addComponent(labelField))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(okButton)
										.addComponent(cancelButton))));
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(nameLabel)
										.addComponent(nameField, 75, 75, 75))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(labelLabel)
										.addComponent(labelField, 75, 75, 75))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(okButton)
										.addComponent(cancelButton))));
	}

	@Action
	public void closeDialogAction() {
		dispose();
	}

	@Action
	public void commitChangesAction() {
		_node.label = labelField.getText();

		try {
			_node.id = Integer.parseInt(nameField.getText());
		} catch (Exception e) {
			ErrorController.showError("Invalid id format!", "Error");
		}

		dispose();

		RoutingDemo.getApplication().getMainFrame().repaint();
	}
	
	public void showDialog() {
		RoutingDemo.getApplication().show(this);
		getRootPane().setDefaultButton(okButton);
	}

}
