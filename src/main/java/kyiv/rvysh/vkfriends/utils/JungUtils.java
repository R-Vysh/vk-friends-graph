package kyiv.rvysh.vkfriends.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.algorithms.cluster.WeakComponentClusterer;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jNode;

public class JungUtils {
	private JungUtils() {
	}

	public static <T> Graph<T, Neo4jEdge> toJungGraph(Neo4jGraph<T> neo4jGraph) {
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
	
	public static <T> Set<Set<T>> weakComponentClustering(Neo4jGraph<T> graph) {
		Graph<T, Neo4jEdge> jungGraph = toJungGraph(graph);
		WeakComponentClusterer<T, Neo4jEdge> clusterer = new WeakComponentClusterer<>();
		Set<Set<T>> cluster = clusterer.apply(jungGraph);
		return cluster;
	}
	
	public static <T> Set<Set<T>> edgeBetweennessClustering(Neo4jGraph<T> graph, int edgesToRemove) {
		Graph<T, Neo4jEdge> jungGraph = toJungGraph(graph);
		EdgeBetweennessClusterer<T, Neo4jEdge> clusterer = new EdgeBetweennessClusterer<>(edgesToRemove);
		Set<Set<T>> cluster = clusterer.apply(jungGraph);
		return cluster;
	}
	
}
