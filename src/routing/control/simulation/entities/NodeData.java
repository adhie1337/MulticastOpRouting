package routing.control.simulation.entities;

import java.util.HashMap;
import java.util.HashSet;
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

	public SessionData getSessionData() {
		return sessionData.get(sessionId);
	}

	public NodeData(int nodeId) {
		this.nodeId = nodeId;
		sessionData = new HashMap<Integer, SessionData>();
	}

	class SessionData {
		public int batchNumber;
		public int credits;

		private Set<Integer> forwarderIds;
		private Set<Integer> reachableDestIds;

		public SessionData() {
			forwarderIds = new HashSet<Integer>();
			reachableDestIds = new HashSet<Integer>();
		}

		public void addForwarderId(int id) {
			forwarderIds.add(id);
		}

		public void addReachableDestId(int id) {
			reachableDestIds.add(id);
		}

		public Iterable<Integer> getForwarderIds() {
			return forwarderIds;
		}

		public Iterable<Integer> getReachableDestIds() {
			return reachableDestIds;
		}
	}
}
