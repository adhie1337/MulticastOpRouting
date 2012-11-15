package routing.control.simulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import routing.control.entities.Session;
import routing.control.simulation.entities.AckPacket;
import routing.control.simulation.entities.DataPacket;
import routing.control.simulation.entities.InfoPacket;
import routing.control.simulation.entities.NodeState;
import routing.control.simulation.entities.NodeState.SessionState;
import routing.control.simulation.entities.Packet;
import routing.control.simulation.entities.PacketHeader;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multiset;

public class NodeLogic {

	private HashMap<Integer, HashMap<Integer, Integer>> creditsAssignedByCurrentNode;

	private NodeState state;

	public NodeState getState() {
		return state;
	}

	public int getNodeId() {
		return state.getNodeId();
	}

	private boolean isSource(int sessionId) {
		for (Session s : simulation.getSessions()) {
			if (s.sourceId == getNodeId() && s.id == sessionId) {
				return true;
			}
		}

		return false;
	}

	private Simulation simulation;

	public NodeLogic(int id, Simulation simulation) {
		state = new NodeState(id);
		this.simulation = simulation;
		creditsAssignedByCurrentNode = new HashMap<Integer, HashMap<Integer, Integer>>();

		for (Session s : simulation.getSessions()) {
			if (s.sourceId == id) {
				state.getSessionStateById(s.id).isSource = true;
			}
			if (s.destinationIds.contains(id)) {
				state.getSessionStateById(s.id).isDestination = true;
			}
		}
	}

	public Packet execute(Packet receivedPacket) {

		if (receivedPacket != null) {
			processPacketHeader(receivedPacket.header);
		}

		Packet packetToSend = null;

		if (receivedPacket instanceof InfoPacket) {
			state.transformWithInfoPacket((InfoPacket) receivedPacket);
		} else if (receivedPacket instanceof AckPacket) {
			processAckPacket((AckPacket) receivedPacket);
		} else if (receivedPacket instanceof DataPacket) {
			packetToSend = processDataPacket((DataPacket) receivedPacket);
		} else if (receivedPacket == null) {
			simulation.enque(this);
			assignCredits();
			packetToSend = selectPacketToSend();
		}

		return updatePacket(packetToSend);
	}

	private void processPacketHeader(PacketHeader header) {
		if (header != null) {
			SessionState ss = state.getSessionStateById(header.sessionId);

			for (int destId : ss.getReachableDestIds()) {
				// the header contains information about this destination
				// and we have "unassigned" packets toward this destination
				if (header.creditMap.containsKey(destId)
						&& ss.unassignedPackets.containsKey(destId)) {
					HashMap<Integer, Integer> newCredits = header.creditMap
							.get(destId);
					Collection<Integer> packetIds = ss.unassignedPackets
							.get(destId);
					Collection<Integer> packetsToRemove = new LinkedList<Integer>();

					// for each packet id in unassigned packets
					for (int pid : packetIds) {
						// if the header contains credit association, update
						// credits
						if (newCredits.containsKey(pid)) {
							if (!ss.creditMap.containsKey(destId)) {
								ss.creditMap.put(destId,
										new HashMap<Integer, Integer>());
							}

							// we can't delete directly from the collection we are
							// iterating
							packetsToRemove.add(pid);

							int forwarderId = newCredits.get(pid);
							ss.creditMap.get(destId).put(pid, forwarderId);

							// if forwarderId == getNodeId() then we got the
							// credit
							if (forwarderId == getNodeId()) {
								ss.packetsToForward.add(pid);
								simulation.enque(this);
							}
						}
					}
					for (int pid : packetsToRemove) {
						ss.unassignedPackets.remove(destId, pid);
					}
				}
			}
		}
	}

	private void processAckPacket(AckPacket ap) {
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

				// put the ack sender to the list of the nodes, who received
				// the packet and are forwarders to the given destination
				ss.ackData.get(destId).put(ap.dataPacketId,
						ap.getSourceNodeId());
			}

		}
	}

	private Packet processDataPacket(DataPacket dp) {

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
			ss.receivedDataPackets.put(dp.getId(), dp);

			if (dp.getDestinationIds().contains(getNodeId())) {
				if (ss.getDestBatchNumber() > dp.getBatchNumber()) {
					ap.recievedFromBatch = Session.PACKETS_PER_BATCH;
				} else if (ss.getDestBatchNumber() == dp.getBatchNumber()) {
					ap.recievedFromBatch = ss.incRecievedCount();
				}
			} else {
				for (int destId : dp.getDestinationIds()) {
					ss.unassignedPackets.put(destId, dp.getId());
				}
			}

			return ap;
		}

		return null;
	}

	private Packet selectPacketToSend() {

		for (int sId : getState().getSessionIds()) {
			if (isSource(sId)) {
				SessionState ss = state.getSessionStateById(sId);
				DataPacket dp = new DataPacket(getNodeId(), sId,
						ss.getBatchNumber());
				dp.getDestinationIds().addAll(ss.getReachableDestIds());
				dp.getForwarderIds().addAll(ss.getForwarderIds());
				dp.header = new PacketHeader(sId);
				return dp;
			}
		}

		for (int sId : getState().getSessionIds()) {
			SessionState ss = state.getSessionStateById(sId);
			for (int destId : ss.getReachableDestIds()) {
				HashMap<Integer, Integer> c = ss.creditMap.get(destId);
				for (int pId : c.keySet()) {
					if (c.get(pId) == getNodeId()) {
						return ss.receivedDataPackets.get(pId);
					}
				}
			}
		}

		return null;
	}

	private void assignCredits() {
		// for each session
		for (int sId : state.getSessionIds()) {
			SessionState ss = state.getSessionStateById(sId);

			// for each destination
			for (int destId : ss.getReachableDestIds()) {
				// if no one ack-ed from the destination, skip
				if (!ss.ackData.containsKey(destId)) {
					continue;
				}

				if (!ss.creditMap.containsKey(destId)) {
					ss.creditMap.put(destId, new HashMap<Integer, Integer>());
				}

				// Summary of the credits of each node (for the session and
				// destination)
				// node id -> credit count ("backlog")
				HashMap<Integer, Integer> credits = new HashMap<Integer, Integer>();
				HashMap<Integer, Integer> c = ss.creditMap.get(destId);
				int mybacklog = 0;
				for (int pId : c.keySet()) {
					int nId = c.get(pId);

					if (!credits.containsKey(nId)) {
						credits.put(nId, 0);
					}

					credits.put(nId, credits.get(nId) + 1);

					if (nId == getNodeId()) {
						++mybacklog;
					}
				}

				// packet id -> node ids who ack-ed it
				HashMultimap<Integer, Integer> innermap = ss.ackData
						.get(destId);
				// mybacklog += simulation.getGraph().getNodeIds().size();

				Multiset<Integer> pIds = innermap.keys();
				for (int pid : pIds) {
					// get all nodes ack-ed packed with pid
					List<Integer> nodes = new LinkedList<Integer>(
							innermap.get(pid));
					int idx = -1;
					int value = 0;

					// find max
					for (int i = 0; i < nodes.size(); ++i) {
						int nBackLog = credits.containsKey(nodes.get(i)) ? credits
								.get(nodes.get(i)) : 0;
						if (idx == -1 || value > nBackLog - mybacklog) {
							idx = i;
							value = nodes.get(i);
						}
					}

					// we asssigned a cerdit
					if (idx > -1) {
						if (!creditsAssignedByCurrentNode.containsKey(destId)) {
							creditsAssignedByCurrentNode.put(destId,
									new HashMap<Integer, Integer>());
						}

						creditsAssignedByCurrentNode.get(destId).put(pid,
								nodes.get(idx));
						c.put(pid, nodes.get(idx));

						// we are source: check current batch number
						if (isSource(sId)) {
							HashMap<Integer, Integer> bm = ss.getBatchMap();
							bm.put(destId, bm.get(destId) + 1);
						}
					}

					innermap.removeAll(pid);
				}
			}
		}
	}

	private Packet updatePacket(Packet p) {
		if (p != null) {
			p.SetSourceNodeId(getNodeId());
			HashMap<Integer, HashMap<Integer, Integer>> newHeader = new HashMap<Integer, HashMap<Integer, Integer>>();

			// copy the credits from the original packet
			for (int destId : p.header.creditMap.keySet()) {
				HashMap<Integer, Integer> innermap = p.header.creditMap
						.get(destId);
				newHeader.put(destId, new HashMap<Integer, Integer>());

				for (int pid : innermap.keySet()) {
					newHeader.get(destId).put(pid, innermap.get(pid));
				}
			}

			// update the credits with the ones assigned by this node
			for (int destId : creditsAssignedByCurrentNode.keySet()) {
				HashMap<Integer, Integer> innermap = creditsAssignedByCurrentNode
						.get(destId);
				newHeader.put(destId, new HashMap<Integer, Integer>());

				for (int pid : innermap.keySet()) {
					newHeader.get(destId).put(pid, innermap.get(pid));
				}
			}

			p.header = new PacketHeader(p.header.sessionId);
			p.header.creditMap = newHeader;
		}
		return p;
	}
}
