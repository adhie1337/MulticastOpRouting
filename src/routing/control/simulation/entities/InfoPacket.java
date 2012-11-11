package routing.control.simulation.entities;

import java.util.HashSet;
import java.util.Set;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

public class InfoPacket extends Packet {
	
	public Multimap<Integer, Integer> forwarderIds;
	public Set<Integer> reachableDestIds;
	
	public InfoPacket(int sessionId) {
		super(-1, sessionId, -1);

		forwarderIds = HashMultimap.create();
		reachableDestIds = new HashSet<Integer>();
	}
}
