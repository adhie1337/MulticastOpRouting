package routing.view.simulation;

import java.awt.BorderLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jdesktop.application.Application;
import org.jdesktop.application.ApplicationContext;
import org.jdesktop.application.ResourceMap;

import routing.RoutingDemo;
import routing.control.simulation.entities.NodeData;
import routing.control.simulation.entities.Packet;

public class DataPanel extends JPanel {

	private ResourceMap rm;

	private JPanel nodePanel;
	private JPanel packetPanel;

	private JLabel nodeIdValue;
	private JLabel sessionIdValue;
	private JLabel batchNumberValue;
	private JLabel creditsValue;
	private JLabel forwardersValue;
	private JLabel reachableDestValue;

	public DataPanel() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		rm = c.getResourceMap(DataPanel.class);

		setLayout(new BorderLayout());

		initNodePanel();
		initPacketPanel();

		add(nodePanel, BorderLayout.NORTH);
		add(packetPanel, BorderLayout.SOUTH);
	}

	private void initNodePanel() {
		nodePanel = new JPanel();
		nodePanel.setBorder(BorderFactory.createTitledBorder(rm
				.getString("DataPanel.NodePanel.Title")));

		JLabel nodeIdLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.NodeId.Label"));
		nodeIdValue = new JLabel("-");

		JLabel sessionIdLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.SessionId.Label"));
		sessionIdValue = new JLabel("-");

		JLabel batchNumberLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.BatchNumber.Label"));
		batchNumberValue = new JLabel("-");

		JLabel creditsLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.Credits.Label"));
		creditsValue = new JLabel("-");

		JLabel forwardersLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.Forwarders.Label"));
		forwardersValue = new JLabel("-");

		JLabel reachableDestLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.ReachableDest.Label"));
		reachableDestValue = new JLabel("-");

		GroupLayout layout = new GroupLayout(nodePanel);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		nodePanel.setLayout(layout);
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(nodeIdLabel)
										.addComponent(nodeIdValue))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(sessionIdLabel)
										.addComponent(sessionIdValue))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(batchNumberLabel)
										.addComponent(batchNumberValue))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(creditsLabel)
										.addComponent(creditsValue))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(forwardersLabel)
										.addComponent(forwardersValue))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(reachableDestLabel)
										.addComponent(reachableDestValue))));
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(nodeIdLabel)
										.addComponent(nodeIdValue))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(sessionIdLabel)
										.addComponent(sessionIdValue))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(batchNumberLabel)
										.addComponent(batchNumberValue))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(creditsLabel)
										.addComponent(creditsValue))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(forwardersLabel)
										.addComponent(forwardersValue))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(reachableDestLabel)
										.addComponent(reachableDestValue))));
	}

	private void initPacketPanel() {
		packetPanel = new JPanel();
		packetPanel.setBorder(BorderFactory.createTitledBorder(rm
				.getString("DataPanel.PacketPanel.Title")));

		GroupLayout layout = new GroupLayout(packetPanel);
		packetPanel.setLayout(layout);
	}

	public void setCurrentNodeData(NodeData nodeData) {
		
	}

	public void setCurrentPacket(Packet packet) {

	}
}
