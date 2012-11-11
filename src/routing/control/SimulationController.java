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
import routing.control.simulation.entities.NodeState;
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
		if(instance == null) {
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
		if(simulationDialog == null)
			simulationDialog = new SimulationDialog();

		currentSimulation = new Simulation(DocumentController.getInstance().getCurrentDocument());
		resetSimulationDialog();
		simulationDialog.showDialog();
	}

	@Action
	public void stepSimulationAction() {
		Transfer t = currentSimulation.step();
		if(currentSimulation.isRunning()) {
			Packet p = t.getPacket();
			Step s = currentSimulation.getCurrentStep();
			NodeState ns = s.getState();
			RenderInfo ri = new RenderInfo();
			ri.highlightedNodeIds = new HashSet<Integer>();
			ri.highlightedNodeIds.add(ns.getNodeId());
			Document d = DocumentController.getInstance().getCurrentDocument();
			Iterable<Session> sessions = d.sessions;
			
			for(Session session : sessions){
				if(session.id == ns.getSessionId()) {
					ri.session = session;
					break;
				}
			}
			
			ri.directedEdges = new LinkedList<RenderInfo.Edge>();
			for(int id : d.graph.getAdjacentNodeIds(ns.getNodeId())) {
				Color c = Color.GRAY;
				if(t.getSuccess().get(id)) {
					c = Color.GREEN;
				}
				ri.directedEdges.add(new RenderInfo.Edge(ns.getNodeId(), id, c));
			}	
	
			EditorController.setCurrentRenderInfo(ri);

			DataPanel dp = simulationDialog.getDataPanel();
			dp.setCurrentPacket(p);
			dp.setCurrentNodeState(ns);
		} else {
			simulationDialog.getToolbar().unselectAutoStep();
			RenderInfo ri = new RenderInfo();
			ri.session = RoutingDemo.getMF().sessionPanel.getSelectedSession();
			EditorController.setCurrentRenderInfo(ri);
			RoutingDemo.getApplication().getContext().getActionMap(this).get("stepSimulationAction").setEnabled(false);
			DataPanel dp = simulationDialog.getDataPanel();
			dp.setCurrentPacket(null);
			dp.setCurrentNodeState(null);
		}
	}

	@Action
	public void resetSimulationAction() {
		currentSimulation.reset();
		RoutingDemo.getApplication().getContext().getActionMap(this).get("stepSimulationAction").setEnabled(true);
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
		
		for(Session session : sessions){
			Map<Integer, String> destData = new HashMap<Integer, String>();
			
			for(int id : session.destinationIds) {
				Node n = d.graph.getNode(id);
				destData.put(id, n.label == null ? ("#" + id) : n.label);
			}
			
			p.addSession(session.id, session.name, destData);
		}

	}
	
}
