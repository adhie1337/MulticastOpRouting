package routing.control.simulation.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NodeData {

	private int nodeId;
	private int sessionId;

	private HashMap<Integer, SessionData> sessionData;

	public int getNodeId() {
		return nodeId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public Iterable<Integer> getSessionIds() {
		return sessionData.keySet();
	}

	public SessionData getSessionData() {
		return sessionData.get(sessionId);
	}

	public NodeData(int nodeId) {
		this.nodeId = nodeId;
		sessionData = new HashMap<Integer, SessionData>();
	}

	public class SessionData {
		private int batchNumber;
		private int credits;

		public List<Packet> packets;
		
		private Set<Integer> forwarderIds;
		private Set<Integer> reachableDestIds;

		public SessionData() {
			packets = new ArrayList<Packet>();
			forwarderIds = new HashSet<Integer>();
			reachableDestIds = new HashSet<Integer>();
		}

		public int getBatchNumber() {
			return batchNumber;
		}

		public int getCredits() {
			return credits;
		}

		public Iterable<Integer> getForwarderIds() {
			return forwarderIds;
		}

		public Iterable<Integer> getReachableDestIds() {
			return reachableDestIds;
		}

		public void addForwarderId(int id) {
			forwarderIds.add(id);
		}

		public void addReachableDestId(int id) {
			reachableDestIds.add(id);
		}
	}
	
	// state changes by a data packet
	public void transformWithDataPacket(DataPacket packet) {
		transformWithPacket(packet);
		
		
	}
	
	// state changes by an acknowledgment packet
	public void transformWithAckPacket(AckPacket packet) {
		transformWithPacket(packet);
		
		
	}
	
	// common state changes by each packet
	private void transformWithPacket(Packet packet) {
		SessionData psd;
		
		if(!sessionData.containsKey(packet.sessionId)) {
			psd = new SessionData();
			sessionData.put(packet.sessionId, psd);
		} else {
			psd = sessionData.get(packet.sessionId);
		}
		
		psd.batchNumber = packet.getBatchNumber();
		psd.packets.add(packet);
	}
}
