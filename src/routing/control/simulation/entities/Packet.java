package routing.control.simulation.entities;

public abstract class Packet {

	private static int nextPacketId = 1;
	protected int id;
	protected int sourceNodeId;
	protected int sessionId;
	protected int batchNumber;

	public int getId() {
		return id;
	}

	public int getSourceNodeId() {
		return sourceNodeId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public Packet(int sourceNodeId, int sessionId, int batchNumber) {
		id = nextPacketId++;
		this.sourceNodeId = sourceNodeId;
		this.sessionId = sessionId;
		this.batchNumber = batchNumber;
	}
}
