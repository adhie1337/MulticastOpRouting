package routing.control.simulation;

import java.util.HashMap;
import java.util.List;

import routing.control.entities.Graph;

public class SimulationUtil {

	public static HashMap<Integer, HashMap<Integer, Double>> createFloydMatrix(
			Graph graph) {
		HashMap<Integer, HashMap<Integer, Double>> retVal = graph
				.copyWeightMap();

		List<Integer> ids = graph.getNodeIds();

		for (int i = 0; i < ids.size(); ++i) {
			int x = ids.get(i);
			HashMap<Integer, Double> fromX;

			if (!retVal.containsKey(x)) {
				retVal.put(x, fromX = new HashMap<Integer, Double>());
			} else {
				fromX = retVal.get(x);
			}

			for (int j = 0; j < ids.size(); ++j) {
				int y = ids.get(j);

				if (!fromX.containsKey(y)) {
					fromX.put(y, Double.POSITIVE_INFINITY);
				} else {
					fromX.put(y, 1.0 / fromX.get(y));
				}
			}
		}

		for (int k = 0; k < ids.size(); ++k) {
			int z = ids.get(k);

			for (int i = 0; i < ids.size(); ++i) {
				int x = ids.get(i);

				for (int j = 0; j < i; ++j) {
					int y = ids.get(j);
					double min = Math.min(retVal.get(x).get(y), retVal.get(x)
							.get(z) + retVal.get(z).get(y));
					retVal.get(x).put(y, min);
					retVal.get(y).put(x, min);
				}
			}
		}

		return retVal;
	}

}
