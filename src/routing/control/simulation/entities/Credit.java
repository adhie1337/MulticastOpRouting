package routing.control.simulation.entities;

public class Credit {
	public int destinationId;
	// -1 means it's not assigned to anyone
	public int nodeId;
	public int packetId;
	public int assignmentStep;
	
	public Credit(int nodeId, int packetId, int destinationId, int assignmentStep) {
		super();
		this.nodeId = nodeId;
		this.packetId = packetId;
		this.assignmentStep = assignmentStep;
		this.destinationId = destinationId;
	}
}
