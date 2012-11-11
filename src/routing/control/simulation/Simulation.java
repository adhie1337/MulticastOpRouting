package routing.control.simulation;

import java.util.List;

import routing.control.Document;
import routing.control.entities.Graph;
import routing.control.entities.Session;

public class Simulation {

	private Graph graph;
	private List<Session> sessions;

	public Simulation(Document doc) {
		graph = doc.graph;
		sessions = doc.sessions;
	}

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
