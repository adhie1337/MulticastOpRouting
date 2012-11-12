package routing.control.simulation;

import com.google.common.collect.HashMultimap;

import routing.control.entities.Session;
import routing.control.simulation.entities.AckPacket;
import routing.control.simulation.entities.DataPacket;
import routing.control.simulation.entities.InfoPacket;
import routing.control.simulation.entities.NodeState;
import routing.control.simulation.entities.NodeState.SessionState;
import routing.control.simulation.entities.Packet;

public class NodeLogic {
	private NodeState state;

	public NodeState getState() {
		return state;
	}

	public int getNodeId() {
		return state.getNodeId();
	}

	private Simulation simulation;

	public NodeLogic(int id, Simulation simulation) {
		state = new NodeState(id);
		this.simulation = simulation;
	}

	public Packet execute(Packet receivedPacket) {

		if (receivedPacket instanceof InfoPacket) {
			state.transformWithInfoPacket((InfoPacket) receivedPacket);
			return null;
		} else if (receivedPacket instanceof AckPacket) {
			// Received an ack packet
			AckPacket ap = (AckPacket) receivedPacket;
			state.transformWithAckPacket(ap);
			SessionState ss = state.getSessionStateById(ap.getSessionId());

			if (ss.getBatchNumber() == ap.getBatchNumber()
					&& ss.sentPackets.containsKey(ap.dataPacketId)) {

				for (int destId : ap.reachableDestIds) {
					// init the multimap instance when it's the first item
					if (!ss.ackData.containsKey(destId)) {
						HashMultimap<Integer, Integer> newMap = HashMultimap
								.create();
						ss.ackData.put(destId, newMap);
					}

					// put the ack sender to the list of the nodes, who recieved
					// the packet and are forwarders to the given destination
					ss.ackData.get(destId).put(ap.dataPacketId,
							ap.getSourceNodeId());
				}

			}

			return null;
		} else if (receivedPacket instanceof DataPacket) {
			DataPacket dp = (DataPacket) receivedPacket;
			if (dp.getForwarderIds().contains(getNodeId())
					|| dp.getDestinationIds().contains(getNodeId())) {
				state.transformWithDataPacket(dp);
				SessionState ss = state.getSessionState();

				if (ss.getBatchNumber() < dp.getBatchNumber()) {
					return null;
				}

				AckPacket ap = new AckPacket(getNodeId(), dp.getSessionId(),
						dp.getBatchNumber());
				ap.reachableDestIds = ss.getReachableDestIds();
				ap.dataPacketId = dp.getId();

				if (ss.getBatchNumber() > dp.getBatchNumber()) {
					ap.recievedFromBatch = Session.PACKETS_PER_BATCH;
				} else if (ss.getBatchNumber() == dp.getBatchNumber()) {
					ss.receivedDataPackets.add(dp);
					int rfb = ss.receivedDataPackets.size();
					ap.recievedFromBatch = rfb;

					if (rfb == Session.PACKETS_PER_BATCH) {
						ss.receivedDataPackets.clear();
						ss.setBatchNumber(ss.getBatchNumber() + 1);
					}
				}

				return ap;
			}

		} else if (receivedPacket == null) {
			System.out.print("jeah");
		}
		simulation.enque(this);
		SessionState ss = state.getSessionState();
		DataPacket dp = new DataPacket(getNodeId(), state.getSessionId(), 1);// state.getSessionState().getBatchNumber());
		dp.getDestinationIds().addAll(ss.getReachableDestIds());
		dp.getForwarderIds().addAll(ss.getForwarderIds());
		return dp;
	}
}
