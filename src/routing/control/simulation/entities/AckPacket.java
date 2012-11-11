package routing.control.simulation.entities;

import java.util.Set;

public class AckPacket extends Packet {
	
	// id of the original data packet, which this acknowledgment belongs to
	public int dataPacketId;
	
	// number of linearly independent received from this batch 
	public int recievedFromBatch;
	
	public Set<Integer> reachableDestIds;
	
	public AckPacket(int sourceNodeId, int sessionId, int batchNumber) {
		super(sourceNodeId, sessionId, batchNumber);
	}
}
