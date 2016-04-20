package kyiv.rvysh.vkfriends.jung;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jNode;

public class JungTransform {
	private JungTransform() {
	}

	public static <T> Graph<T, Neo4jEdge> neo4jToJungGraph(Neo4jGraph<T> neo4jGraph) {
		Graph<T, Neo4jEdge> result = new UndirectedSparseGraph<>();
		Map<String, T> data = new HashMap<>();
		for (Neo4jNode<T> node : neo4jGraph.getNodes()) {
			data.put(node.getId(), node.getProperties());
			result.addVertex(node.getProperties());
		}
		for (Neo4jEdge edge : neo4jGraph.getRelationships()) {
			result.addEdge(edge, data.get(edge.getStartNode()), data.get(edge.getEndNode()));
		}
		return result;
	}
}
