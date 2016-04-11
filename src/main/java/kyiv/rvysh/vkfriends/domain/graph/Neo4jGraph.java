package kyiv.rvysh.vkfriends.domain.graph;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Neo4jGraph<T> {
	private Set<Neo4jNode<T>> nodes;
	private Set<Neo4jEdge> relationships;

	public static <T> Neo4jGraph<T> mergeGraphs(List<Neo4jGraph<T>> graphs) {
		Neo4jGraph<T> res = new Neo4jGraph<>();
		for (Neo4jGraph<T> graph : graphs) {
			res.nodes.addAll(graph.nodes);
			res.relationships.addAll(graph.relationships);
		}
		return res;
	}

	public Neo4jGraph() {
		nodes = new HashSet<>();
		relationships = new HashSet<>();
	}

	public Set<Neo4jNode<T>> getNodes() {
		return nodes;
	}

	public void setNodes(Set<Neo4jNode<T>> nodes) {
		this.nodes = nodes;
	}

	public Set<Neo4jEdge> getRelationships() {
		return relationships;
	}

	public void setRelationships(Set<Neo4jEdge> relationships) {
		this.relationships = relationships;
	}
}
