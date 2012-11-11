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
		
		if(receivedPacket instanceof InfoPacket) {
			state.transformWithInfoPacket((InfoPacket)receivedPacket);
			return null;
		} else if(receivedPacket instanceof AckPacket) {
			AckPacket ap = (AckPacket)receivedPacket;
			state.transformWithAckPacket(ap);
			SessionState ss = state.getSessionState();
			if(ss.getBatchNumber() == ap.getBatchNumber()) {
				
				for(int destId : ap.reachableDestIds) {
					if(!ss.ackData.containsKey(destId)) {
						HashMultimap<Integer, Integer> newMap = HashMultimap.create();
						ss.ackData.put(destId, newMap);
					}
				}
				
			}
			
			return null;
		} else if(receivedPacket instanceof DataPacket) {
			DataPacket dp = (DataPacket)receivedPacket;
			if(dp.getForwarderIds().contains(getNodeId())) {
				state.transformWithDataPacket(dp);
				SessionState ss = state.getSessionState();
			} else if(dp.getDestinationIds().contains(getNodeId())) {
				// Destination receiver
				state.transformWithDataPacket(dp);
				SessionState ss = state.getSessionState();
				
				if(ss.getBatchNumber() < dp.getBatchNumber()) {
					return null;
				}
				
				AckPacket ap = new AckPacket(getNodeId(), dp.getSessionId(), dp.getBatchNumber());
				ap.reachableDestIds = ss.getReachableDestIds();
				ap.dataPacketId = dp.getId();
				
				if(ss.getBatchNumber() > dp.getBatchNumber()) {
					ap.recievedFromBatch = Session.PACKETS_PER_BATCH;
				} else if(ss.getBatchNumber() == dp.getBatchNumber()) {
					ss.receivedDataPackets.add(dp);
					int rfb = ss.receivedDataPackets.size();
					ap.recievedFromBatch = rfb;
					
					if(rfb == Session.PACKETS_PER_BATCH) {
						ss.receivedDataPackets.clear();
						ss.setBatchNumber(ss.getBatchNumber() + 1);
					}
				}
				
				return ap;
			}
			
		} else if(receivedPacket == null) {
			
		}
		simulation.enque(this);
		return new DataPacket(getNodeId(), state.getSessionId(), 0);//state.getSessionState().getBatchNumber());
	}
	
}
