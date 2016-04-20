package kyiv.rvysh.vkfriends.jgrapht;

import java.util.HashMap;
import java.util.Map;
import org.jgrapht.Graph;
import org.jgrapht.graph.SimpleGraph;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jNode;

public class JgraphtTransform {
	private JgraphtTransform() {
	}

	public static <T> Graph<T, Neo4jEdge> neo4jToJgraphtGraph(Neo4jGraph<T> neo4jGraph) {
		Graph<T, Neo4jEdge> result = new SimpleGraph<>(Neo4jEdge.class);
		Map<String, T> data = new HashMap<>();
		for (Neo4jNode<T> node : neo4jGraph.getNodes()) {
			data.put(node.getId(), node.getProperties());
			result.addVertex(node.getProperties());
		}
		for (Neo4jEdge edge : neo4jGraph.getRelationships()) {
			result.addEdge(data.get(edge.getStartNode()), data.get(edge.getEndNode()), edge);
		}
		return result;
	}
}
