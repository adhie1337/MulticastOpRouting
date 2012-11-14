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

	@Action
	public void stepSimulationAction() {
		Transfer t = currentSimulation.step();
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
			for (int id : d.graph.getAdjacentNodeIds(ns.getNodeId())) {
				Color c = Color.RED;
				if (t.getSuccess().containsKey(id) && t.getSuccess().get(id)) { 
					c = t.getPacket() instanceof AckPacket ? Color.GREEN : Color.BLUE;
				}
				ri.directedEdges
						.add(new RenderInfo.Edge(ns.getNodeId(), id, c));
			}

			EditorController.setCurrentRenderInfo(ri);

			DataPanel dp = simulationDialog.getDataPanel();
			dp.setCurrentPacket(p);
			dp.setCurrentNodeState(ns);

			ri.nodeInfo = new HashMap<Integer, String>();

			ProgressPanel pp = simulationDialog.getProgressPanel();
			int maxPackets = ri.session.batchCount * Session.PACKETS_PER_BATCH;
			for (int nodeId : d.graph.getNodeIds()) {
				SessionState ss = currentSimulation.getSessionDataByNodeId(
						nodeId, ri.session.id);
				if (ss != null) {
					if (!ri.session.destinationIds.contains(nodeId)) {
						ri.nodeInfo.put(nodeId,
								ss.getCredits() + ";" + ss.getBatchNumber());
					} else {
						ri.nodeInfo.put(nodeId, "" + ss.getBatchNumber());

						int batchesComplete = ss.getBatchNumber() - 1;
						int packetsInCurrentBatch = ss.receivedDataPackets
								.size();
						int percentage = (int) (batchesComplete
								* Session.PACKETS_PER_BATCH + packetsInCurrentBatch
								/ (double) maxPackets);
						pp.setPercentage(ri.session.id, nodeId, percentage);
					}
				}
			}
		} else {
			simulationDialog.getToolbar().unselectAutoStep();
			RenderInfo ri = new RenderInfo();
			ri.session = RoutingDemo.getMF().sessionPanel.getSelectedSession();
			EditorController.setCurrentRenderInfo(ri);
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
