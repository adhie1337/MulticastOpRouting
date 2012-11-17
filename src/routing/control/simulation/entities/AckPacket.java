package routing.control.simulation.entities;

import java.util.HashSet;
import java.util.Set;

public class AckPacket extends Packet {
	
	// id of the original data packet, which this acknowledgment belongs to
	public int dataPacketId;
	
	// number of linearly independent received from this batch 
	public int recievedFromBatch;
	
	public int originalSourceId;
	
	public Set<Integer> reachableDestIds;
	
	public AckPacket(int sourceNodeId, int originalSourceId, int dataPacketId, int sessionId, int batchNumber) {
		super(sourceNodeId, sessionId, batchNumber);
		
		this.originalSourceId = originalSourceId;
		this.dataPacketId = dataPacketId;
		
		this.reachableDestIds = new HashSet<Integer>();
	}

	@Override
	public Object clone() {
		AckPacket retVal = new AckPacket(sourceNodeId, originalSourceId, dataPacketId, sessionId, batchNumber);
		retVal.id = this.id;
		retVal.header = (PacketHeader)header.clone();
		
		for(int i : reachableDestIds) {
			retVal.reachableDestIds.add(i);
		}
		
		return retVal;
	}
}
