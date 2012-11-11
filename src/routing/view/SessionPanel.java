package routing.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.ActionMap;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.application.Action;
import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import routing.RoutingDemo;
import routing.control.Document;
import routing.control.EditorController;
import routing.control.entities.Session;
import routing.control.simulation.SimulationUtil;
import routing.view.editor.RenderInfo;

public class SessionPanel extends JPanel implements ListSelectionListener {

	private Document document;

	private JList<Session> sessionList;
	private JButton btnAdd;
	private JButton btnRemove;

	public SessionPanel() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		ActionMap editorMap = c.getActionMap(EditorController.getInstance());
		ResourceMap resources = c.getResourceMap(SessionPanel.class);
		setBorder(BorderFactory.createTitledBorder(resources
				.getString("SessionPanel.Title")));

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

		btnAdd = new JButton(editorMap.get("newSessionAction"));
		btnAdd.setEnabled(false);
		btnAdd.setText(resources.getString("SessionPanel.btnAdd.Text"));
		btnRemove = new JButton(c.getActionMap(this).get("removeSession"));
		btnRemove.setEnabled(false);
		btnRemove.setText(resources.getString("SessionPanel.btnRemove.Text"));

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
		RenderInfo ri = new RenderInfo();
		ri.session = sessionList.getSelectedValue();
		EditorController.setCurrentRenderInfo(ri);
		checkActionsState();
	}

	public void checkActionsState() {
		if (document != null) {
			btnAdd.setEnabled(document.graph.getNodeList().size() >= 2);
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

	public void selectSession(int id) {
		for (int i = 0; i < document.sessions.size(); ++i) {
			if (document.sessions.get(i).id == id) {
				sessionList.setSelectedIndex(i);
				valueChanged(null);
				break;
			}
		}
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);

		btnAdd.setEnabled(enabled);
		btnRemove.setEnabled(enabled);
		sessionList.setEnabled(enabled);

		if (enabled) {
			checkActionsState();
		}
	}

	@Action
	public void removeSession() {
		if (sessionList.getSelectedValue() != null) {
			document.sessions.remove(sessionList.getSelectedValue());
			checkActionsState();
			RoutingDemo.getMF().checkActionsState();
			setDocument(this.document);
		}
	}

}
