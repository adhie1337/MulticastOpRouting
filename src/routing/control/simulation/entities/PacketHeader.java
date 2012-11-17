package routing.control.simulation.entities;

import java.util.HashMap;

public class PacketHeader implements Cloneable {

	public int sessionId;

	// destinationid, packetid, courrent forwarder node id
	public HashMap<Integer, HashMap<Integer, Credit>> creditMap;

	public PacketHeader(int sessionId) {
		this.creditMap = new HashMap<Integer, HashMap<Integer, Credit>>();
		this.sessionId = sessionId;
	}
	
	@Override
	public Object clone() {
		PacketHeader retVal = new PacketHeader(sessionId);

		for(int i : creditMap.keySet()) {
			HashMap<Integer, Credit> inner = creditMap.get(i);
			HashMap<Integer, Credit> newInner = new HashMap<Integer, Credit>();
			retVal.creditMap.put(i, newInner);
		
			for(int j : creditMap.get(i).keySet()) {
				newInner.put(j, inner.get(j));
			}
		}
		
		return retVal;
	}
}
