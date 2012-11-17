package routing.control;

import java.awt.Color;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;

import org.jdesktop.application.Action;

import routing.RoutingDemo;
import routing.control.entities.Node;
import routing.control.entities.Session;
import routing.control.simulation.Simulation;
import routing.control.simulation.Simulation.Step;
import routing.control.simulation.Simulation.Transfer;
import routing.control.simulation.entities.AckPacket;
import routing.control.simulation.entities.DataPacket;
import routing.control.simulation.entities.NodeState;
import routing.control.simulation.entities.NodeState.SessionState;
import routing.control.simulation.entities.Packet;
import routing.view.editor.RenderInfo;
import routing.view.simulation.DataPanel;
import routing.view.simulation.ProgressPanel;
import routing.view.simulation.SimulationDialog;

public class SimulationController {

	/**
	 * The singleton reference to the only instance of the controller class.
	 */
	private static SimulationController instance;

	/**
	 * Gets the singleton reference to the only instance of the controller
	 * class.
	 */
	public static SimulationController getInstance() {
		if (instance == null) {
			instance = new SimulationController();
		}

		return instance;
	}

	/**
	 * Constructor.
	 */
	private SimulationController() {
		if (instance != null) {
			throw new Error("Invalid use of singleton pattern!");
		}
	}

	private Simulation currentSimulation;

	public Simulation getCurrentSimulation() {
		return currentSimulation;
	}

	private SimulationDialog simulationDialog;

	@Action
	public void showSimulationDialogAction() {
		if (simulationDialog == null)
			simulationDialog = new SimulationDialog();

		currentSimulation = new Simulation(DocumentController.getInstance()
				.getCurrentDocument());
		resetSimulationDialog();
		simulationDialog.showDialog();
	}

	public static int credits;

	@Action
	public void stepSimulationAction() {
		Transfer t = currentSimulation.step();
		boolean done = true;
		
		// if we stopped, we have to make sure that all detinations ack-ed the last packets
		if(!currentSimulation.isRunning()) {
			for(Session s : currentSimulation.getSessions()) {
				for(int destId : s.destinationIds) {
					if(done) {
						break;
					}
					
					SessionState st = currentSimulation.getSessionDataByNodeId(destId, s.id);
					if(!st.isReady()) {
						done = true;
						// if not so, we force to restart
						currentSimulation.enque(currentSimulation.getLogicByNodeId(s.sourceId));
					}
				}
			}
		}
		
		if (currentSimulation.isRunning()) {
			Packet p = t.getPacket();
			Step s = currentSimulation.getCurrentStep();
			NodeState ns = s.getState();
			RenderInfo ri = new RenderInfo();
			ri.highlightedNodeIds = new HashSet<Integer>();
			ri.highlightedNodeIds.add(ns.getNodeId());
			Document d = DocumentController.getInstance().getCurrentDocument();
			Iterable<Session> sessions = d.sessions;

			for (Session session : sessions) {
				if (session.id == ns.getSessionId()) {
					ri.session = session;
					break;
				}
			}

			ri.directedEdges = new LinkedList<RenderInfo.Edge>();
			Packet init = t.getInitiator();
			if(init != null) {
				Color c = Color.RED;
				if (init instanceof AckPacket) {
					c = Color.GREEN;
				} else if (init instanceof DataPacket) {
					c = Color.BLUE;
				} else {
					c = Color.ORANGE;
				}
				ri.directedEdges.add(new RenderInfo.Edge(init.getSourceNodeId(),
						ns.getNodeId(), c));
			}
			if (p != null) {
				for (int id : d.graph.getAdjacentNodeIds(p.getSourceNodeId())) {
					Color c = Color.RED;
					if (t.getSuccess().containsKey(id)
							&& t.getSuccess().get(id)) {
						if (t.getPacket() instanceof AckPacket) {
							c = Color.GREEN;
						} else if (t.getPacket() instanceof DataPacket) {
							c = Color.BLUE;
						} else {
							c = Color.ORANGE;
						}
					}
					ri.directedEdges.add(new RenderInfo.Edge(ns.getNodeId(),
							id, c));
				}
			}

			EditorController.setCurrentRenderInfo(ri);

			DataPanel dp = simulationDialog.getDataPanel();
			dp.setCurrentPacket(p);
			dp.setCurrentNodeState(ns);

			ri.nodeInfo = new HashMap<Integer, String>();

			simulationDialog.updateStep(currentSimulation.getStep());
			ProgressPanel pp = simulationDialog.getProgressPanel();
			
			if(ri.session == null) {
				return;
			}
			
			int maxPackets = ri.session.batchCount * Session.PACKETS_PER_BATCH;
			for (int nodeId : d.graph.getNodeIds()) {
				SessionState ss = currentSimulation.getSessionDataByNodeId(
						nodeId, ri.session.id);
				if (ss != null) {
					if (ss.isSource) {
						ri.nodeInfo.put(nodeId, "b:" + ss.getBatchNumber());
					} else if (ss.isDestination) {
						int packetsInCurrentBatch = ss.getReceivedCount();
						ri.nodeInfo
								.put(nodeId, "b:" + ss.getDestBatchNumber() + "+" + packetsInCurrentBatch);

						int batchesComplete = ss.getDestBatchNumber() - 1;
						int percentage = (int) ((batchesComplete
								* Session.PACKETS_PER_BATCH + packetsInCurrentBatch) / (double) maxPackets * 100);
						pp.setPercentage(ri.session.id, nodeId, percentage);
					} else {
						ri.nodeInfo.put(nodeId, "c:" + ss.getCredits() + "(" + ss.unassignedPackets.size() + ")");
					}
				}
			}
		} else {
			simulationDialog.getToolbar().unselectAutoStep();
			RoutingDemo.getApplication().getContext().getActionMap(this)
					.get("stepSimulationAction").setEnabled(false);
			DataPanel dp = simulationDialog.getDataPanel();
			dp.setCurrentPacket(null);
			dp.setCurrentNodeState(null);
		}
	}

	@Action
	public void resetSimulationAction() {
		currentSimulation.reset();
		RoutingDemo.getApplication().getContext().getActionMap(this)
				.get("stepSimulationAction").setEnabled(true);
		resetSimulationDialog();
		RenderInfo ri = new RenderInfo();
		ri.session = RoutingDemo.getMF().sessionPanel.getSelectedSession();
		EditorController.setCurrentRenderInfo(ri);
	}

	private void resetSimulationDialog() {
		DataPanel dp = simulationDialog.getDataPanel();
		dp.setCurrentPacket(null);
		dp.setCurrentNodeState(null);
		ProgressPanel p = simulationDialog.getProgressPanel();
		p.clearSessions();
		Document d = DocumentController.getInstance().getCurrentDocument();

		Iterable<Session> sessions = d.sessions;

		for (Session session : sessions) {
			Map<Integer, String> destData = new HashMap<Integer, String>();

			for (int id : session.destinationIds) {
				Node n = d.graph.getNode(id);
				destData.put(id, n.label == null ? ("#" + id) : n.label);
			}

			p.addSession(session.id, session.name, destData);
		}

	}

}
