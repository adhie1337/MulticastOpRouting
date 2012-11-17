package routing.control.simulation.entities;


public abstract class Packet implements Cloneable {

	private static int nextPacketId = 1;
	protected int id;
	protected int sourceNodeId;
	protected int sessionId;
	protected int batchNumber;
	
	public PacketHeader header;

	public int getId() {
		return id;
	}

	public int getSourceNodeId() {
		return sourceNodeId;
	}

	public void SetSourceNodeId(int sId) {
		sourceNodeId = sId;
	}

	public int getSessionId() {
		return sessionId;
	}

	public int getBatchNumber() {
		return batchNumber;
	}

	public Packet(int sourceNodeId, int sessionId, int batchNumber) {
		this.id = nextPacketId++;
		this.sourceNodeId = sourceNodeId;
		this.sessionId = sessionId;
		this.batchNumber = batchNumber;
		this.header = new PacketHeader(sessionId);
	}
	
	@Override
	public abstract Object clone();
}
