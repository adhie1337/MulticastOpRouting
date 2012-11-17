package routing.control.simulation.entities;

import java.util.HashSet;
import java.util.Set;

import javax.xml.crypto.Data;

public class DataPacket extends Packet {

	private Set<Integer> forwarderIds;
	private Set<Integer> destinationIds;

	public Set<Integer> getForwarderIds() {
		return forwarderIds;
	}

	public Set<Integer> getDestinationIds() {
		return destinationIds;
	}

	public DataPacket(int sourceNodeId, int sessionId, int batchNumber) {
		super(sourceNodeId, sessionId, batchNumber);

		forwarderIds = new HashSet<Integer>();
		destinationIds = new HashSet<Integer>();
	}

	@Override
	public Object clone() {
		DataPacket retVal = new DataPacket(sourceNodeId, sessionId, batchNumber);
		retVal.id = this.id;
		retVal.header = (PacketHeader)header.clone();
		
		for(int i : forwarderIds) {
			retVal.forwarderIds.add(i);
		}
		
		for(int i : destinationIds) {
			retVal.destinationIds.add(i);
		}
		
		return retVal;
	}
}
