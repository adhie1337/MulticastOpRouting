package routing.control.simulation.entities;

import java.util.HashSet;
import java.util.Set;

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

}
