package routing.control.simulation.entities;

import java.util.HashMap;

public class PacketHeader {
	
	public int sessionId;
	
	// destinationid, packetid, courrent forwarder node id 
	public HashMap<Integer, HashMap<Integer, Integer>> creditMap;

	public PacketHeader(int sessionId) {
		this.creditMap = new HashMap<Integer, HashMap<Integer, Integer>>();
		this.sessionId = sessionId;
	}
}
