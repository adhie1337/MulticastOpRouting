package routing.control.simulation;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Random;

import com.google.common.collect.Multimap;

import routing.control.Document;
import routing.control.entities.Graph;
import routing.control.entities.Node;
import routing.control.entities.Session;
import routing.control.simulation.entities.AckPacket;
import routing.control.simulation.entities.InfoPacket;
import routing.control.simulation.entities.NodeState;
import routing.control.simulation.entities.Packet;
import routing.view.editor.RenderInfo;

public class Simulation {

	private Graph graph;
	private List<Session> sessions;
	private Queue<Step> steps;
	private HashMap<Integer, HashMap<Integer, InfoPacket>> infoPackets;
	private Map<Integer, NodeLogic> nodeLogics;
	private Random r;
	private boolean isRunning;
	private Step currentStep;

	public Step getCurrentStep() {
		return currentStep;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public Simulation(Document doc) {
		graph = doc.graph;
		sessions = doc.sessions;
		init();
		reset();
	}

	public void reset() {
		step = 0;
		steps = new LinkedList<Step>();
		nodeLogics = new HashMap<Integer, NodeLogic>();

		for (Node n : graph.getNodeList()) {
			NodeLogic l = new NodeLogic(n.id, this);
			nodeLogics.put(n.id, l);

			for (Session s : sessions) {
				if (l.getNodeId() == s.sourceId) {
					steps.add(new Step(l, null));
				}
				
				InfoPacket ip = infoPackets.get(s.id).get(n.id);
				if(ip != null) {
					l.execute(ip);
				}
			}
		}
		
		isRunning = true;
	}

	public Transfer step() {
		++step;
		currentStep = steps.poll();
		if (currentStep != null) {
			Packet p = currentStep.execute();
			HashMap<Integer, Boolean> successMap = new HashMap<Integer, Boolean>();
			
			for(int id : graph.getAdjacentNodeIds(p.getSourceNodeId())) {
				double chance = graph.getWeight(id, p.getSourceNodeId());
				boolean success = r.nextDouble() <= chance;
				successMap.put(id, success);
				
				if (success) {
					if(p instanceof AckPacket) {
						nodeLogics.get(id).execute(p);
					} else {
						steps.add(new Step(nodeLogics.get(id), p));
					}
				}
			}	
			
			return new Transfer(p, successMap);
		} else {
			step = 0;
			isRunning = false;
		}
		
		return null;
	}

	private int step;

	public int getStep() {
		return step;
	}

	public void init() {
		step = 0;
		r = new Random();

		HashMap<Integer, HashMap<Integer, Double>> mtx = SimulationUtil
				.createFloydMatrix(graph);

		infoPackets = new HashMap<Integer, HashMap<Integer, InfoPacket>>();
		for (Session s : sessions) {
			HashMap<Integer, InfoPacket> sessionPackets = new HashMap<Integer, InfoPacket>();
			infoPackets.put(s.id, sessionPackets);
			InfoPacket spk = new InfoPacket(s.id);
			sessionPackets.put(s.sourceId, spk);
			spk.reachableDestIds.addAll(s.destinationIds);

			for (int nodeId : graph.getNodeIds()) {
				if (nodeId != s.sourceId && !s.destinationIds.contains(nodeId)) {
					InfoPacket pk = new InfoPacket(s.id);
					sessionPackets.put(nodeId, pk);

					for (int destId : s.destinationIds) {
						if (mtx.get(nodeId).get(destId) < mtx.get(s.sourceId)
								.get(destId)) {
							pk.reachableDestIds.add(destId);
						}
					}
				}
			}

			for (int nodeId : graph.getNodeIds()) {
				if (!s.destinationIds.contains(nodeId)) {
					InfoPacket pk = sessionPackets.get(nodeId);

					for (int destId : pk.reachableDestIds) {
						for (int otherNodeId : graph.getNodeIds()) {
							if (mtx.get(otherNodeId).get(destId) < mtx.get(
									nodeId).get(destId)) {
								pk.forwarderIds.put(destId, otherNodeId);
							}
						}
					}
				}
			}
		}
	}
	
	public void enque(NodeLogic logic) {
		steps.add(new Step(logic, null));
	}

	public class Step {
		
		private NodeLogic logic;
		private Packet packetToReceive;

		public NodeState getState() {
			return logic.getState();
		}

		public Packet getPacketToReceive() {
			return packetToReceive;
		}

		public Step(NodeLogic logic, Packet packetToReceive) {
			this.logic = logic;
			this.packetToReceive = packetToReceive;
		}

		public Packet execute() {
			return logic.execute(packetToReceive);
		}
	}
	
	public class Transfer {
		private Packet packet;
		private Map<Integer, Boolean> success;
		
		public Packet getPacket() {
			return packet;
		}

		public Map<Integer, Boolean> getSuccess() {
			return success;
		}

		public Transfer(Packet packet, Map<Integer, Boolean> success) {
			super();
			this.packet = packet;
			this.success = success;
		}
	}
}
