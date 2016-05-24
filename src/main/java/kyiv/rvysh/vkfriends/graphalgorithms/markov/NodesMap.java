package kyiv.rvysh.vkfriends.graphalgorithms.markov;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;

/**
 *
 * @author Jiri Krizek
 */
class NodesMap<T> {
	// Mapping of nodeId to sequential id
	Map<T, Integer> nodeTable = new HashMap<>();
	// Mapping of sequential id to nodeId
	Map<Integer, T> idsTable = new HashMap<>();

	public NodesMap(Graph<T, Neo4jEdge> graph) {
		int i = 0;
		for (T node : graph.getVertices()) {
			nodeTable.put(node, new Integer(i));
			idsTable.put(new Integer(i), node);
			i++;
		}
	}

	public int getSequentialIdFor(T node) {
		return nodeTable.get(node).intValue();
	}
	
	public T getNodeForId(int id) {
		return idsTable.get(id);
	}
}
