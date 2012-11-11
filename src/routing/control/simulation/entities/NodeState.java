package routing.control.simulation.entities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class NodeState {

	private int nodeId;
	private int sessionId = 1;

	private HashMap<Integer, SessionState> sessionState;

	public int getNodeId() {
		return nodeId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public Iterable<Integer> getSessionIds() {
		return sessionState.keySet();
	}

	public SessionState getSessionState() {
		return sessionState.get(sessionId);
	}

	public NodeState(int nodeId) {
		this.nodeId = nodeId;
		sessionState = new HashMap<Integer, SessionState>();
	}

	public class SessionState {
		private int batchNumber;
		private int credits;

		// destinationid, packetid, [node]
		public HashMap<Integer, HashMultimap<Integer, Integer>> ackData;
		
		//public List<AckPacket> receivedAckPackets;
		public List<DataPacket> receivedDataPackets;
		public List<Packet> sentPackets;

		private Multimap<Integer, Integer> forwarderIds;
		private Set<Integer> reachableDestIds;

		public SessionState() {
			receivedDataPackets = new ArrayList<DataPacket>();
			sentPackets = new ArrayList<Packet>();
			forwarderIds = HashMultimap.create();
			reachableDestIds = new HashSet<Integer>();
			ackData = new HashMap<Integer, HashMultimap<Integer, Integer>>();
			batchNumber = 1;
		}

		public int getBatchNumber() {
			return batchNumber;
		}
		
		public void setBatchNumber(int batchNumber) {
			this.batchNumber = batchNumber;
		}

		public int getCredits() {
			return credits;
		}
		
		public void setCredits(int credits) {
			this.credits = credits;
		}

		public Set<Integer> getForwarderIds() {
			return new HashSet<Integer>(forwarderIds.values());
		}

		public Set<Integer> getReachableDestIds() {
			return reachableDestIds;
		}
	}

	// state changes by a data packet
	public void transformWithDataPacket(DataPacket packet) {
		SessionState s = transformWithPacket(packet);
		sessionId = packet.sessionId;
	}

	// state changes by an acknowledgment packet
	public void transformWithAckPacket(AckPacket packet) {
		SessionState s = transformWithPacket(packet);
		sessionId = packet.sessionId;
	}

	// common state changes by each packet
	private SessionState transformWithPacket(Packet packet) {
		SessionState psd;

		if (!sessionState.containsKey(packet.sessionId)) {
			psd = new SessionState();
			sessionState.put(packet.sessionId, psd);
		} else {
			psd = sessionState.get(packet.sessionId);
		}
		
		return psd;
	}

	// state changes by an info packet
	public void transformWithInfoPacket(InfoPacket packet) {
		SessionState psd;

		if (!sessionState.containsKey(packet.sessionId)) {
			psd = new SessionState();
			sessionState.put(packet.sessionId, psd);
		} else {
			psd = sessionState.get(packet.sessionId);
		}

		psd.forwarderIds = packet.forwarderIds;
		psd.reachableDestIds = packet.reachableDestIds;
	}
}
