package routing.view;

import java.awt.BorderLayout;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import routing.control.Document;
import routing.control.entities.Session;

public class SessionPanel extends JPanel implements ListSelectionListener {

	private Document document;

	private JList<Session> sessionList;
	private JButton btnAdd;
	private JButton btnRemove;

	public SessionPanel() {
		setBorder(BorderFactory.createTitledBorder("Sessions"));

		JPanel unitGroup = new JPanel();
		setLayout(new BorderLayout());
		add(unitGroup, BorderLayout.CENTER);

		sessionList = new JList<Session>();
		sessionList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		sessionList.addListSelectionListener(this);
		JPanel buttonPanel = new JPanel();

		unitGroup.setLayout(new BorderLayout());
		unitGroup.add(new JScrollPane(sessionList), BorderLayout.CENTER);
		unitGroup.add(buttonPanel, BorderLayout.SOUTH);

		btnAdd = new JButton("Add");
		btnAdd.setEnabled(false);
		btnRemove = new JButton("Remove");
		btnRemove.setEnabled(false);

		buttonPanel.add(btnAdd);
		buttonPanel.add(btnRemove);
	}

	public void setDocument(Document document) {
		this.document = document;

		if (this.document != null) {
			sessionList.setListData(document.sessions);
		} else {
			sessionList.setListData(new Vector<Session>());
		}

		checkActionsState();
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		checkActionsState();
	}

	public void checkActionsState() {
		if (document != null) {
			btnAdd.setEnabled(document.net.getNodeList().size() >= 2);
			btnRemove.setEnabled(sessionList.getSelectedIndex() >= 0
					&& sessionList.getSelectedIndex() < document.sessions
							.size());
		} else {
			btnAdd.setEnabled(false);
			btnRemove.setEnabled(false);
		}
	}

	public Session getSelectedSession() {
		return sessionList.getSelectedValue();
	}
}
