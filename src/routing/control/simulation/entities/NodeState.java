package routing.control.simulation.entities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import routing.control.entities.Session;

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
		return getSessionStateById(sessionId);
	}

	public SessionState getSessionStateById(int sessionId) {
		if (!sessionState.containsKey(sessionId)) {
			sessionState.put(sessionId, new SessionState());
		}

		return sessionState.get(sessionId);
	}

	public boolean isSourceAndActive() {
		for (SessionState s : sessionState.values()) {
			if (s.isSource && !s.isReady()) {
				return true;
			}
		}

		return false;
	}

	public NodeState(int nodeId) {
		this.nodeId = nodeId;
		sessionState = new HashMap<Integer, SessionState>();
	}

	public class SessionState {
		private int receivedCount;
		private int destBatchNumber;
		private boolean ready = false;

		public int batchCount = 0;

		public boolean isSource = false;

		public boolean isDestination = false;

		// Nodes, that ack-ed our packets, grouped by the destinations.
		// destination id, packet id, [node]
		public HashMap<Integer, HashMultimap<Integer, Integer>> ackData;

		// Nodes, that ack-ed our packets, grouped by the destinations.
		// destination id, current batch number (sources only)
		private HashMap<Integer, Integer> batchMap;

		// Packets we received, but waiting for credit assignment to happen.
		// destination id, packet id
		public HashMultimap<Integer, Integer> unassignedPackets;

		// All received data packets.
		public HashMap<Integer, DataPacket> receivedDataPackets;

		// count of data packets received from the current batch.
		public int receivedDataPacketsFromBatch;

		// destination id, packet id, current forwarder node id
		// a map of packets with their current forwarder id, who is got the
		// credit to forward the packet. Grouped by destination identifiers.
		public HashMap<Integer, HashMap<Integer, CreditAssignment>> creditMap;


		public Map<Integer, Packet> sentPackets;

		private Multimap<Integer, Integer> forwarderIds;
		private Set<Integer> reachableDestIds;

		public List<Integer> newPackets;

		public SessionState() {
			ackData = new HashMap<Integer, HashMultimap<Integer, Integer>>();
			unassignedPackets = HashMultimap.create();
			receivedDataPackets = new HashMap<Integer, DataPacket>();
			receivedDataPacketsFromBatch = 0;
			creditMap = new HashMap<Integer, HashMap<Integer, CreditAssignment>>();
			sentPackets = new HashMap<Integer, Packet>();
			forwarderIds = HashMultimap.create();
			reachableDestIds = new HashSet<Integer>();
			batchMap = new HashMap<Integer, Integer>();
			receivedCount = 0;
			destBatchNumber = 1;
			newPackets = new LinkedList<Integer>();

		}

		private HashMap<Integer, Integer> getBatchMap() {
			if (batchMap.size() != reachableDestIds.size()) {
				for (int id : reachableDestIds) {
					if (!batchMap.containsKey(id)) {
						batchMap.put(id, 0);
					}
				}
			}

			return batchMap;
		}

		public boolean isReady() {
			return ready;
		}

		public int getDestBatchNumber() {
			return destBatchNumber;
		}

		public int getReceivedCount() {
			return receivedCount;
		}

		public int incRecievedCount() {
			int retVal = receivedCount;
			if (!ready) {
				++receivedCount;

				if (receivedCount == Session.PACKETS_PER_BATCH) {
					if (destBatchNumber != batchCount) {
						++destBatchNumber;
						receivedCount = 0;
					} else {
						ready = true;
					}
				}
			}

			return retVal;
		}

		public void incPacketCount(int destId) {
			getBatchMap().put(destId, getBatchMap().get(destId) + 1);

			if (getBatchNumber() > batchCount) {
				ready = true;
			}
		}

		public int getBatchNumber() {
			int next = -1;

			for (int k : getBatchMap().keySet()) {
				int batchValue = getBatchNumber(k);
				if (next == -1 || next > batchValue) {
					next = batchValue;
				}
			}

			return next == -1 ? 1 : next;
		}

		public int getBatchNumber(int destId) {
			return (getBatchMap().get(destId) + Session.PACKETS_PER_BATCH)
					/ Session.PACKETS_PER_BATCH;
		}

		public int getCredits() {
			int retVal = 0;

			for (int destId : creditMap.keySet()) {
				retVal += getCredits(destId);
			}

			return retVal;
		}

		public int getCredits(int destId) {
			int retVal = 0;

			for (CreditAssignment ca : creditMap.get(destId).values()) {
				if (ca.nodeId == getNodeId()) {
					++retVal;
				}
			}

			return retVal;
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
		transformWithPacket(packet);
		sessionId = packet.sessionId;
	}

	// state changes by an acknowledgment packet
	public void transformWithAckPacket(AckPacket packet) {
		transformWithPacket(packet);
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

		if (packet.forwarderIds.size() > 0) {
			psd.forwarderIds = packet.forwarderIds;
		}
		if (packet.reachableDestIds.size() > 0) {
			psd.reachableDestIds = packet.reachableDestIds;
		}
	}
}
