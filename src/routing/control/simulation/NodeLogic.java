package routing.control.simulation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;

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

	private boolean creditsChanged;

	private HashMap<Integer, HashMap<Integer, Integer>> creditsAssignedByCurrentNode;

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
		creditsAssignedByCurrentNode = new HashMap<Integer, HashMap<Integer, Integer>>();

		// initialize the isSource and isDestionation properties for session
		// states
		for (Session s : simulation.getSessions()) {
			SessionState st = state.getSessionStateById(s.id);
			if (s.sourceId == id) {
				st.isSource = true;
				st.batchCount = s.batchCount;
			}
			if (s.destinationIds.contains(id)) {
				st.isDestination = true;
				st.batchCount = s.batchCount;
			}
		}
	}

	// what to do when we woke up (we got a new incoming packet... or not...
	public Packet execute(Packet receivedPacket) {

		// if we did receive a packet, refresh our credit assignment table with
		// the new assignments written in the package's MORE header
		if (receivedPacket != null) {
			processPacketHeader(receivedPacket.header);
		}

		Packet packetToSend = null;

		// if it was an Info packet, let the state handle it. (sets forwarder
		// and reachable destination ids...)
		if (receivedPacket instanceof InfoPacket) {
			state.transformWithInfoPacket((InfoPacket) receivedPacket);
		}
		// if we received an ack packet, update the ack table
		else if (receivedPacket instanceof AckPacket) {
			processAckPacket((AckPacket) receivedPacket);
		}
		// if we received a data packet update the received packets list and
		// send an ack back, when needed.
		else if (receivedPacket instanceof DataPacket) {
			packetToSend = processDataPacket((DataPacket) receivedPacket);
		}
		// if we didn't receive a packet at all, we chose one to send (if we are
		// a source) or just forward (if we are a forwarder)
		else if (receivedPacket == null) {
			assignCredits();
			packetToSend = selectPacketToSend();

			// if we are sending something, we expect an ack, we will process it
			// later
			if (packetToSend != null || getState().isSourceAndActive()) {
				simulation.enque(this);
			}
		}

		// before sending, we update the packet's MORE header with our credit
		// assignment modifications
		return updatePacket(packetToSend);
	}

	// processes a packet header (it updates it's credit map based on the MORE
	// header's credit map
	private void processPacketHeader(PacketHeader header) {
		creditsChanged = false;
		if (header != null) {
			SessionState ss = state.getSessionStateById(header.sessionId);
			HashMap<Integer, HashMap<Integer, Integer>> credits = ss.creditMap;
			HashMultimap<Integer, Integer> unassignedPs = ss.unassignedPackets;

			// for all destinations we can reach (we don't care about the ones
			// we can't reach)
			for (int dId : ss.getReachableDestIds()) {
				HashMap<Integer, Integer> hCredits = header.creditMap.get(dId);
				HashMap<Integer, Integer> aCredits = creditsAssignedByCurrentNode
						.get(dId);

				// if the header has some information
				if (hCredits != null) {

					// for each packet id in the assignments of the header
					for (int pid : hCredits.keySet()) {
						if (!credits.containsKey(dId)) {
							credits.put(dId, new HashMap<Integer, Integer>());
						}

						int forwarderId = hCredits.get(pid);

						// if we didn't know about this association
						if (!credits.get(dId).containsKey(pid)
								|| credits.get(dId).get(pid) != forwarderId) {
							creditsChanged = true;
							credits.get(dId).put(pid, forwarderId);

							// we knew the packet, but stored it as "unassigned"
							if (unassignedPs.containsKey(dId)
									&& unassignedPs.get(dId).contains(pid)) {
								unassignedPs.remove(dId, pid);
							}

							// if forwarderId == getNodeId() then we got the
							// credit: enque ourselves to send the packet next
							// time
							if (forwarderId == getNodeId()) {
								simulation.enque(this);
							}

							// if we assigned the original sender to the packet,
							// we remove this connection
							if (aCredits != null && aCredits.containsKey(pid)) {
								aCredits.remove(pid);
							}
						}
					}
				}
			}
		}
	}

	// what we do when we receive an ack packet
	private void processAckPacket(AckPacket ap) {
		state.transformWithAckPacket(ap);
		SessionState ss = state.getSessionStateById(ap.getSessionId());

		// if we are the original sender of the data packet
		if (ss.sentPackets.containsKey(ap.dataPacketId)
				&& ap.originalSourceId == getNodeId()) {

			// for each destination id that is reachable from the ack's sender
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

	// what we do when we receive a data packet
	private Packet processDataPacket(DataPacket dp) {
		state.transformWithDataPacket(dp);
		SessionState ss = state.getSessionStateById(dp.getSessionId());

		// prepare an ack packet
		AckPacket ap = new AckPacket(getNodeId(), dp.getSourceNodeId(),
				dp.getId(), dp.getSessionId(), dp.getBatchNumber());
		ap.reachableDestIds = ss.getReachableDestIds();

		// if we are a possible forwarder for this packet
		if (dp.getForwarderIds().contains(getNodeId())
				&& !ss.getForwarderIds().contains(dp.getSourceNodeId())) {
			ss.receivedDataPackets.put(dp.getId(), dp);
			// store the packet as unassigned, and send an ack back. When the
			// original sender decides who to forward the packet, we will find
			// it here
			for (int destId : dp.getDestinationIds()) {
				if (ss.getReachableDestIds().contains(destId)) {
					ss.unassignedPackets.put(destId, dp.getId());
				}
			}

			return ap;
		}
		// we are the destination of this packet, and the packet is NOT too new
		else if (ss.isDestination
				&& ss.getDestBatchNumber() >= dp.getBatchNumber()) {

			// if we already received that package (e.g. the ack packet is lost)
			// we don't need to increase our packet count
			if (!ss.receivedDataPackets.containsKey(dp.getId())) {
				ss.receivedDataPackets.put(dp.getId(), dp);

				if (ss.getDestBatchNumber() > dp.getBatchNumber()) {
					ap.recievedFromBatch = Session.PACKETS_PER_BATCH;
				} else if (ss.getDestBatchNumber() == dp.getBatchNumber()) {
					ap.recievedFromBatch = ss.incRecievedCount();
				}
			}

			// send back to the sender that we are the destination, we need the
			// credit
			ap.reachableDestIds = new HashSet<Integer>();
			ap.reachableDestIds.add(getNodeId());

			return ap;
		}

		// we are not either forwarder or destination of this data, we simply
		// drop the packet and don't ACK
		return null;
	}

	// we select a packet to send
	private Packet selectPacketToSend() {

		// first we search for sessions those source is the current node
		for (int sId : getState().getSessionIds()) {
			SessionState ss = state.getSessionStateById(sId);

			if (ss.newPackets.size() > 0
					&& !ss.sentPackets.containsKey(ss.newPackets.get(0))) {
				ss.newPackets.remove(0);
			}

			if (ss.newPackets.size() > 0) {
				return ss.sentPackets.get(ss.newPackets.get(0));
			}

			// if we are source, we send a packet from the least ready batch
			if (ss.isSource && !ss.isReady()) {
				DataPacket dp = new DataPacket(getNodeId(), sId,
						ss.getBatchNumber());
				dp.getDestinationIds().addAll(ss.getReachableDestIds());
				dp.getForwarderIds().addAll(ss.getForwarderIds());
				dp.header = new PacketHeader(sId);
				ss.newPackets.add(dp.getId());
				return dp;
			}
		}

		int chosenPid = -1;
		int chosenBatch = -1;
		int chosenSid = -1;
		// if we are here, there is no session wich's source is the current node
		// if we have credits, we send a corresponding packet
		for (int sId : getState().getSessionIds()) {
			SessionState ss = state.getSessionStateById(sId);
			// for all destinations that is reachable from here
			for (int destId : ss.getReachableDestIds()) {
				if (!ss.creditMap.containsKey(destId)) {
					continue;
				}

				HashMap<Integer, Integer> c = ss.creditMap.get(destId);
				// for each packet going to that direction
				for (int pId : c.keySet()) {
					// if we are the current forwarder of that packet, we
					// forward it
					if (c.get(pId) == getNodeId()
							&& (chosenPid == -1 || chosenBatch > ss.receivedDataPackets
									.get(pId).getBatchNumber())) {
						chosenPid = pId;
						chosenSid = sId;
						chosenBatch = ss.receivedDataPackets.get(pId)
								.getBatchNumber();
					}
				}
			}
		}

		if (chosenPid != -1) {
			return state.getSessionStateById(chosenSid).receivedDataPackets
					.get(chosenPid);
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
				for (int pId : c.keySet()) {
					int nId = c.get(pId);

					if (!credits.containsKey(nId)) {
						credits.put(nId, 0);
					}

					credits.put(nId, credits.get(nId) + 1);
				}

				// packet id -> node ids who ack-ed it
				HashMultimap<Integer, Integer> ackmap = ss.ackData.get(destId);
				// mybacklog += simulation.getGraph().getNodeIds().size();

				// we can't delete from the set while we are iterating in it
				Set<Integer> packetsToRemove = new HashSet<Integer>();
				Set<Integer> pIds = new HashSet<Integer>(ackmap.keys());
				for (int pid : pIds) {
					// get all nodes ack-ed packed with pid
					List<Integer> nodes = new LinkedList<Integer>(
							ackmap.get(pid));
					int idx = -1;
					int value = 0;

					// find max
					for (int i = 0; i < nodes.size(); ++i) {
						int nBackLog = credits.containsKey(nodes.get(i)) ? credits
								.get(nodes.get(i)) : 0;
						if (idx == -1 || value > nBackLog) {
							idx = i;
							value = nBackLog;
						}
					}

					// we assigned a cerdit
					if (idx > -1) {
						if (!creditsAssignedByCurrentNode.containsKey(destId)) {
							creditsAssignedByCurrentNode.put(destId,
									new HashMap<Integer, Integer>());
						}

						int pidx = ss.newPackets.indexOf(pid);
						if (pidx != -1) {
							ss.newPackets.remove(pidx);
						}

						creditsAssignedByCurrentNode.get(destId).put(pid,
								nodes.get(idx));
						c.put(pid, nodes.get(idx));

						// we are source: check current batch number
						if (ss.isSource) {
							ss.incPacketCount(destId);
						}

						packetsToRemove.add(pid);
						creditsChanged = true;
					}
				}

				for (int pid : packetsToRemove) {
					ackmap.removeAll(pid);
				}
			}
		}
	}

	private Packet updatePacket(Packet p) {
		if (p == null && (creditsChanged || new Random().nextDouble() < 0.1)) {
			p = new InfoPacket(state.getSessionId());
		}

		if (p != null) {
			p.SetSourceNodeId(getNodeId());
			HashMap<Integer, HashMap<Integer, Integer>> newHeader = new HashMap<Integer, HashMap<Integer, Integer>>();
			SessionState ss = state.getSessionStateById(p.getSessionId());

			if (ss.isDestination && p instanceof AckPacket) {
				((AckPacket) p).reachableDestIds.add(getNodeId());
			}

			// copy the credits from the original packet
			for (int destId : p.header.creditMap.keySet()) {
				HashMap<Integer, Integer> innermap = p.header.creditMap
						.get(destId);
				newHeader.put(destId, new HashMap<Integer, Integer>());

				for (int pid : innermap.keySet()) {
					newHeader.get(destId).put(pid, innermap.get(pid));
				}
			}

			// update the credits with our credit map
			for (int destId : ss.creditMap.keySet()) {
				HashMap<Integer, Integer> innermap = ss.creditMap.get(destId);
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
