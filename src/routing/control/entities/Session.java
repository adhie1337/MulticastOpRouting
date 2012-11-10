package routing.control.entities;

import java.util.Vector;

public class Session {

	private static int nextSessionId = 1;

	public int id;

	public int weight;

	public int sourceId;

	public Vector<Integer> destinationIds;

	public String name;

	public Session() {
		id = nextSessionId++;

		weight = 1;
		destinationIds = new Vector<Integer>();
	}

	@Override
	public String toString() {
		return (name != null ? name : "unnamed session") + " (#" + id + ", w: " + weight + ")";
	}
}
