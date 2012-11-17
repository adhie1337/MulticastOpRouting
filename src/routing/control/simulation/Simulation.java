package routing.control.simulation;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

import routing.control.Document;
import routing.control.entities.Graph;
import routing.control.entities.Node;
import routing.control.entities.Session;
import routing.control.simulation.entities.AckPacket;
import routing.control.simulation.entities.InfoPacket;
import routing.control.simulation.entities.NodeState;
import routing.control.simulation.entities.NodeState.SessionState;
import routing.control.simulation.entities.Packet;

public class Simulation {

	private Graph graph;
	private List<Session> sessions;
	private Queue<Step> steps;
	private HashMap<Integer, HashMap<Integer, InfoPacket>> infoPackets;
	private Map<Integer, NodeLogic> nodeLogics;
	private Random r;
	private boolean isRunning;
	private Step currentStep;
	private HashMap<Integer, HashMap<Integer, Double>> etxMatrix;

	public Graph getGraph() {
		return graph;
	}

	public Step getCurrentStep() {
		return currentStep;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public List<Session> getSessions() {
		return sessions;
	}
	
	public Session getSessionById(int id) {
		for(Session s : sessions) {
			if(s.id == id) {
				return s;
			}
		}
		return null;
	}
	
	public NodeLogic getLogicByNodeId(int nodeId) {
		if (nodeLogics.containsKey(nodeId)) {
			return nodeLogics.get(nodeId);
		}

		return null;
	}

	public SessionState getSessionDataByNodeId(int nodeId, int sessionId) {
		if (nodeLogics.containsKey(nodeId)) {
			NodeLogic logic = nodeLogics.get(nodeId);
			return logic.getState().getSessionStateById(sessionId);
		}

		return null;
	}

	public Simulation(Document doc) {
		graph = doc.graph;
		sessions = doc.sessions;
		init();
		reset();
	}

	public void reset() {
		step = 0;
		steps = new PriorityQueue<Step>(graph.getNodeList().size(),
				new StepComparer());
		nodeLogics = new HashMap<Integer, NodeLogic>();
		Set<Integer> quedIds = new HashSet<Integer>();

		for (Node n : graph.getNodeList()) {
			NodeLogic l = new NodeLogic(n.id, this);
			nodeLogics.put(n.id, l);

			for (Session s : sessions) {
				if (l.getNodeId() == s.sourceId
						&& !quedIds.contains(l.getNodeId())) {
					enque(l);
					quedIds.add(l.getNodeId());
				}

				InfoPacket ip = infoPackets.get(s.id).get(n.id);
				if (ip != null) {
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

			if (p != null) {
				for (int id : graph.getAdjacentNodeIds(p.getSourceNodeId())) {
					double chance = graph.getWeight(id, p.getSourceNodeId());
					boolean success = r.nextDouble() <= chance;
					successMap.put(id, success);

					if (success) {
						currentStep.logic.getState().getSessionStateById(
								p.getSessionId()).sentPackets.put(p.getId(), p);
						steps.add(new Step(nodeLogics.get(id), p));
					}
				}
			}

			return new Transfer(p, successMap, currentStep.getPacketToReceive());
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

		etxMatrix = SimulationUtil.createFloydMatrix(graph);

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
						if (etxMatrix.get(nodeId).get(destId) < etxMatrix.get(
								s.sourceId).get(destId)) {
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
							if (etxMatrix.get(otherNodeId).get(destId) < etxMatrix
									.get(nodeId).get(destId)
									&& !s.destinationIds.contains(otherNodeId)) {
								pk.forwarderIds.put(destId, otherNodeId);
							}
						}
					}
				}
			}
		}
	}
	
	public double getETXBeteen(int nodeId, int otherNodeId) {
		if(etxMatrix != null) {
			return etxMatrix.get(nodeId).get(otherNodeId);
		}
		
		return Double.POSITIVE_INFINITY;
	}

	public void enque(NodeLogic logic) {
		enque(new Step(logic, null));
	}

	public void enque(Step s) {
		isRunning = true;
		steps.add(new Step(s.logic, s.packetToReceive != null ? (Packet)s.packetToReceive.clone() : null));
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
		private Packet initiator;

		private Packet packet;
		private Map<Integer, Boolean> success;

		public Packet getPacket() {
			return packet;
		}

		public Packet getInitiator() {
			return initiator;
		}

		public Map<Integer, Boolean> getSuccess() {
			return success;
		}

		public Transfer(Packet packet, Map<Integer, Boolean> success,
				Packet initiator) {
			super();
			this.packet = packet;
			this.success = success;
			this.initiator = initiator;
		}
	}

	private class StepComparer implements Comparator<Step> {
		@Override
		public int compare(Step o1, Step o2) {
			int retVal = 0;

			if (o1.packetToReceive == null) {
				retVal = o2.packetToReceive == null ? 0 : 1;
			} else if (o2.packetToReceive == null) {
				retVal = -1;
			} else if (!o1.packetToReceive.getClass().equals(
					o2.packetToReceive.getClass())) {
				if (o1.packetToReceive instanceof AckPacket
						|| o1.packetToReceive instanceof InfoPacket) {
					retVal = -1;
				} else if (o2.packetToReceive instanceof AckPacket
						|| o2.packetToReceive instanceof InfoPacket) {
					retVal = 1;
				}
			}
			return retVal;
		}

	}
}
