package routing.control;

import org.jdesktop.application.Action;

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
	
	private SimulationDialog simulationDialog;
	
	@Action
	public void showSimulationDialogAction() {
		if(simulationDialog == null)
			simulationDialog = new SimulationDialog();
		
		simulationDialog.showDialog();
	}
	
}
