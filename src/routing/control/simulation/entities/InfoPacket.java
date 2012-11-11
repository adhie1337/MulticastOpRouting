package routing.control.simulation.entities;

import java.util.HashSet;
import java.util.Set;

public class InfoPacket extends Packet {
	
	public Set<Integer> forwarderIds;
	public Set<Integer> reachableDestIds;
	
	public InfoPacket(int sessionId) {
		super(-1, sessionId, -1);

		forwarderIds = new HashSet<Integer>();
		reachableDestIds = new HashSet<Integer>();
	}
}
