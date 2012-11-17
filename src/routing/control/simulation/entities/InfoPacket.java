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

	@Override
	public Object clone() {
		InfoPacket retVal = new InfoPacket(sessionId);
		retVal.id = this.id;
		retVal.header = (PacketHeader)header.clone();
		
		for(int i : reachableDestIds) {
			retVal.reachableDestIds.add(i);
		}
		
		for(int i : forwarderIds.keySet()) {
			for(int j : forwarderIds.get(i)) {
				retVal.forwarderIds.put(i, j);
			}
		}
		
		return retVal;
	}
}
