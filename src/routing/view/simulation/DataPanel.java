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
import routing.control.simulation.entities.AckPacket;
import routing.control.simulation.entities.DataPacket;
import routing.control.simulation.entities.NodeState;
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

	private JLabel packetIdValue;
	private JLabel packetTypeValue;

	private JLabel ackPacketDataIdValue;

	public DataPanel() {
		ApplicationContext c = Application.getInstance(RoutingDemo.class)
				.getContext();
		rm = c.getResourceMap(DataPanel.class);

		setLayout(new BorderLayout());

		initNodePanel();
		initPacketPanel();

		add(nodePanel, BorderLayout.NORTH);
		add(packetPanel, BorderLayout.CENTER);
	}

	private void initNodePanel() {
		nodePanel = new JPanel();
		nodePanel.setBorder(BorderFactory.createTitledBorder(rm
				.getString("DataPanel.NodePanel.Title")));

		JLabel nodeIdLabel = new JLabel(
				rm.getString("DataPanel.NodePanel.NodeId.Label"));
		nodeIdValue = new JLabel(
				rm.getString("DataPanel.NodePanel.NodeId.Value.NoData"));

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

		JLabel packetIdLabel = new JLabel(
				rm.getString("DataPanel.PacketPanel.PacketId.Label"));
		packetIdValue = new JLabel(
				rm.getString("DataPanel.PacketPanel.PacketId.Value.NoData"));

		JLabel packetTypeLabel = new JLabel(
				rm.getString("DataPanel.PacketPanel.PacketType.Label"));
		packetTypeValue = new JLabel("-");

		GroupLayout layout = new GroupLayout(packetPanel);
		packetPanel.setLayout(layout);
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createSequentialGroup()
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(packetIdLabel)
										.addComponent(packetIdValue))
						.addGroup(
								layout.createParallelGroup(
										GroupLayout.Alignment.CENTER)
										.addComponent(packetTypeLabel)
										.addComponent(packetTypeValue))));
		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(packetIdLabel)
										.addComponent(packetIdValue))
						.addGroup(
								layout.createSequentialGroup()
										.addComponent(packetTypeLabel)
										.addComponent(packetTypeValue))));
	}

	public void setCurrentNodeState(NodeState nodeState) {
		if (nodeState != null) {
			NodeState.SessionState sessionData = nodeState.getSessionState();
			nodeIdValue.setText(Integer.toString(nodeState.getNodeId()));
			sessionIdValue.setText(Integer.toString(nodeState.getSessionId()));
			
			if(sessionData != null) {
				batchNumberValue.setText(Integer.toString(sessionData
						.getBatchNumber()));
				creditsValue.setText(Integer.toString(sessionData.getCredits()));
	
				forwardersValue.setText(idsToString(sessionData.getForwarderIds()));
				reachableDestValue.setText(idsToString(sessionData
						.getReachableDestIds()));
			} else {
				batchNumberValue.setText("-");
				creditsValue.setText("-");
				forwardersValue.setText("-");
				reachableDestValue.setText("-");
			}
		} else {
			nodeIdValue.setText(rm
					.getString("DataPanel.NodePanel.NodeId.Value.NoData"));
			sessionIdValue.setText("-");
			batchNumberValue.setText("-");
			creditsValue.setText("-");
			forwardersValue.setText("-");
			reachableDestValue.setText("-");
		}
	}

	public void setCurrentPacket(Packet packet) {
		if (packet != null) {
			// attributes independent from packet type
			packetIdValue.setText(Integer.toString(packet.getId()));

			// attributes based on packet type
			if (packet instanceof DataPacket) {
				packetTypeValue
						.setText(rm
								.getString("DataPanel.PacketPanel.PacketType.Value.Data"));
			} else if (packet instanceof AckPacket) {
				packetTypeValue
						.setText(rm
								.getString("DataPanel.PacketPanel.PacketType.Value.Ack"));
			}
		} else {
			packetIdValue.setText(rm
					.getString("DataPanel.PacketPanel.PacketId.Value.NoData"));
			packetTypeValue.setText("-");
		}
	}

	private String idsToString(Iterable<Integer> ids) {
		StringBuilder sb = new StringBuilder();

		for (Integer id : ids) {
			sb.append(id);
			sb.append(", ");
		}

		String retVal = sb.toString();
		if(retVal.length() > 0) {
			return retVal.substring(0, retVal.length() - 2);
		}
		
		return "-";
	}
}
