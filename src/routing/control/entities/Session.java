package routing.control.entities;

import java.util.Vector;

public class Session {
	
	public static final int PACKETS_PER_BATCH=10;

	private static int nextSessionId = 1;

	public int id;

	public int weight;

	public int sourceId;
	
	public int batchCount;

	public Vector<Integer> destinationIds;

	public String name;

	public Session() {
		id = nextSessionId++;

		weight = 1;
		batchCount = 5;
		destinationIds = new Vector<Integer>();
	}

	@Override
	public String toString() {
		return (name != null ? name : "unnamed session") + " (#" + id + ", w: " + weight + ", bc: " + batchCount + ")";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + batchCount;
		result = prime * result
				+ ((destinationIds == null) ? 0 : destinationIds.hashCode());
		result = prime * result + id;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + sourceId;
		result = prime * result + weight;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Session other = (Session) obj;
		if (batchCount != other.batchCount)
			return false;
		if (destinationIds == null) {
			if (other.destinationIds != null)
				return false;
		} else if (!destinationIds.equals(other.destinationIds))
			return false;
		if (id != other.id)
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sourceId != other.sourceId)
			return false;
		if (weight != other.weight)
			return false;
		return true;
	}

}
