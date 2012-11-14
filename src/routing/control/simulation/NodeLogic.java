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
		for(Session s : simulation.getSessions()) {
			if(s.sourceId == getNodeId()
				&& s.id == sessionId) {
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

		return updatePacketHeader(packetToSend);
	}

	private void processPacketHeader(PacketHeader header) {
		if (header != null) {
			SessionState ss = state.getSessionStateById(header.sessionId);

			for (int destId : ss.getReachableDestIds()) {
				if (header.creditMap.containsKey(destId)
						&& ss.unassignedPackets.containsKey(destId)) {
					HashMap<Integer, Integer> innermap = header.creditMap
							.get(destId);
					Collection<Integer> packetIds = ss.unassignedPackets
							.get(destId);

					for (int pid : packetIds) {
						if (innermap.containsKey(pid)) {
							if(!ss.creditMap.containsKey(destId)) {
								ss.creditMap.put(destId, new HashMap<Integer, Integer>());
							}
							
							int forwarderId = innermap.get(pid);
							ss.unassignedPackets.remove(destId, pid);
							ss.creditMap.get(destId).put(pid, forwarderId);

							// if forwarderId == getNodeId() then we got the
							// credit
							if (forwarderId == getNodeId()) {
								ss.packetsToForward.add(pid);
								simulation.enque(this);
							}
						}
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

			if (ss.getBatchNumber() > dp.getBatchNumber()) {
				ap.recievedFromBatch = Session.PACKETS_PER_BATCH;
			} else if (ss.getBatchNumber() == dp.getBatchNumber()) {
				ss.receivedDataPackets.put(dp.getId(), dp);
				++ss.receivedDataPacketsFromBatch;
				ap.recievedFromBatch = ss.receivedDataPacketsFromBatch;

				if (ss.receivedDataPacketsFromBatch == Session.PACKETS_PER_BATCH) {
					ss.receivedDataPacketsFromBatch = 0;
					ss.setBatchNumber(ss.getBatchNumber() + 1);
				}
			}

			for (int destId : dp.getDestinationIds()) {
				ss.unassignedPackets.put(destId, dp.getId());
			}

			return ap;
		}

		return null;
	}

	private Packet selectPacketToSend() {

		for(int sId : getState().getSessionIds()) {
			if(isSource(sId)) {
				SessionState ss = state.getSessionStateById(sId);
				DataPacket dp = new DataPacket(getNodeId(), sId, ss.getBatchNumber());
				dp.getDestinationIds().addAll(ss.getReachableDestIds());
				dp.getForwarderIds().addAll(ss.getForwarderIds());
				dp.header = new PacketHeader(sId);
				return dp;
			}
		}
		
		for(int sId : getState().getSessionIds()) {
			SessionState ss = state.getSessionStateById(sId);
			for(int destId : ss.getReachableDestIds()) {
				HashMap<Integer, Integer> c = ss.creditMap.get(destId);
				for(int pId : c.keySet()) {
					if(c.get(pId) == getNodeId()) {
						return ss.receivedDataPackets.get(pId);
					}
				}
			}
		}
		
		return null;
	}

	private void assignCredits() {
		SessionState ss = state.getSessionState();

		for (int destId : ss.getReachableDestIds()) {
			if(!ss.ackData.containsKey(destId)) {
				continue;
			}
			
			if(!ss.creditMap.containsKey(destId)) {
				ss.creditMap.put(destId, new HashMap<Integer, Integer>());
			}
			
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

			HashMultimap<Integer, Integer> innermap = ss.ackData.get(destId);
			// mybacklog += simulation.getGraph().getNodeIds().size();

			for (int pid : innermap.keys()) {
				List<Integer> nodes = new LinkedList<Integer>(innermap.get(pid));
				int idx = -1;
				int value = 0;

				for (int i = 0; i < nodes.size(); ++i) {
					int nBackLog = credits.containsKey(nodes.get(i)) ? credits.get(nodes.get(i)) : 0;
					if (idx == -1 || value > nBackLog - mybacklog) {
						idx = i;
						value = nodes.get(i);
					}
				}

				if (idx > -1) {
					if (!creditsAssignedByCurrentNode.containsKey(destId)) {
						creditsAssignedByCurrentNode.put(destId,
								new HashMap<Integer, Integer>());
					}

					creditsAssignedByCurrentNode.get(destId).put(pid,
							nodes.get(idx));
					c.put(pid, nodes.get(idx));
				}
			}

		}
	}

	private Packet updatePacketHeader(Packet p) {
		if(p != null) {
			HashMap<Integer, HashMap<Integer, Integer>> newHeader = new HashMap<Integer, HashMap<Integer, Integer>>();
	
			// copy the credits from the original packet
			for (int destId : p.header.creditMap.keySet()) {
				HashMap<Integer, Integer> innermap = p.header.creditMap.get(destId);
				newHeader.put(destId, new HashMap<Integer, Integer>());
	
				for (int pid : innermap.keySet()) {
					newHeader.get(destId).put(pid, innermap.get(pid));
				}
			}
	
			// update the credits with the ones assigned by this node
			for (int destId : creditsAssignedByCurrentNode.keySet()) {
				HashMap<Integer, Integer> innermap = creditsAssignedByCurrentNode.get(destId);
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
