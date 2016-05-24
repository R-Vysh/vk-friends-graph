package kyiv.rvysh.vkfriends.graphalgorithms;

import java.util.Collection;
import java.util.Set;

import edu.uci.ics.jung.algorithms.cluster.EdgeBetweennessClusterer;
import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jung.JungTransform;

public abstract class GirvanNewmanClusterer<T> {
	public Collection<Set<T>> clusterize(Neo4jGraph<T>graph, int edgesToRemove) {
		Graph<T, Neo4jEdge> jungGraph = JungTransform.neo4jToJungGraph(graph);
		EdgeBetweennessClusterer<T, Neo4jEdge> clusterer = new EdgeBetweennessClusterer<>(edgesToRemove);
		Set<Set<T>> clusters = clusterer.apply(jungGraph);
		return clusters;
	}
}
