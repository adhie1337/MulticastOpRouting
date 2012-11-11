package routing.control.simulation.entities;

import java.util.HashSet;
import java.util.Set;

public class DataPacket extends Packet {

	private Set<Integer> forwarderIds;

	public Iterable<Integer> getForwarderIds() {
		return forwarderIds;
	}

	public DataPacket(int sourceNodeId, int sessionId, int batchNumber) {
		super(sourceNodeId, sessionId, batchNumber);

		forwarderIds = new HashSet<Integer>();
	}

}
