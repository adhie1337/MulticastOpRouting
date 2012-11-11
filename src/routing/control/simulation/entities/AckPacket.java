package routing.control.simulation.entities;

public class AckPacket extends Packet {
	
	// id of the original data packet, which this acknowledgment belongs to
	public int dataPacketId;
	
	// number of linearly independent recieved from this batch 
	public int recievedFromBatch;
	
	public AckPacket(int sourceNodeId, int sessionId, int batchNumber) {
		super(sourceNodeId, sessionId, batchNumber);
	}
}
