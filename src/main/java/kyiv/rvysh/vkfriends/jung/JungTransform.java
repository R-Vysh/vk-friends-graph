package kyiv.rvysh.vkfriends.jung;

import java.util.HashMap;
import java.util.Map;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import edu.uci.ics.jung.graph.util.Pair;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jNode;
import kyiv.rvysh.vkfriends.utils.MapUtils;

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
	
	public static <T> Neo4jGraph<T> jungToNeo4jGraph(Graph<T, Neo4jEdge> jungGraph) {
		Neo4jGraph<T> neo4jGraph = new Neo4jGraph<>();
		Integer i = 0;
		Map<Integer, T> vertices = new HashMap<>();
		for (T vertex : jungGraph.getVertices()) {
			vertices.put(i, vertex);
			Neo4jNode<T> node = new Neo4jNode<>();
			node.setProperties(vertex);
			node.setId(i + "");
			neo4jGraph.getNodes().add(node);
			i++;
		}
		for (Neo4jEdge edge : jungGraph.getEdges()) {
			Pair<T> verts = jungGraph.getEndpoints(edge);
			Integer inId = MapUtils.getKeyByValue(vertices, verts.getFirst());
			Integer outId = MapUtils.getKeyByValue(vertices, verts.getSecond());
			edge.setId(i + "");
			edge.setStartNode(inId + "");
			edge.setEndNode(outId + "");
			neo4jGraph.getRelationships().add(edge);
			i++;
		}
		return neo4jGraph;
	}
}
