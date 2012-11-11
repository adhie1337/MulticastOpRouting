package routing.control;

import org.jdesktop.application.Action;

import routing.control.simulation.Simulation;
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

	public void setCurrentSimulation(Simulation currentSimulation) {
		this.currentSimulation = currentSimulation;
	}

	private SimulationDialog simulationDialog;
	
	@Action
	public void showSimulationDialogAction() {
		if(simulationDialog == null)
			simulationDialog = new SimulationDialog();
		
		simulationDialog.showDialog();
	}

	@Action
	public void stepSimulationAction() {
		System.out.print("step ");
		
		if(currentSimulation != null) {
			currentSimulation.step();
			System.out.print(currentSimulation.getStep());
		}
		System.out.println();
	}

	@Action
	public void resetSimulationAction() {
		System.out.println("reset");
	}
	
}
