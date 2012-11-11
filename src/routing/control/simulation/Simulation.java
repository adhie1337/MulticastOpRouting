package routing.control.simulation;

public class Simulation {
	
	public void reset() {
		step = 0;
	}
	
	public void step() {
		++step;
	}
	
	private int step;

	public int getStep() {
		return step;
	}
}
