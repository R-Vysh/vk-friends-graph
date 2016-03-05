package kyiv.rvysh.vkfriends.domain.graph;

import java.util.List;

public class Neo4jGraph<T> {
	private List<Neo4jNode<T>> nodes;
	private List<Neo4jEdge> relationships;
	
	public List<Neo4jNode<T>> getNodes() {
		return nodes;
	}
	public void setNodes(List<Neo4jNode<T>> nodes) {
		this.nodes = nodes;
	}
	public List<Neo4jEdge> getRelationships() {
		return relationships;
	}
	public void setRelationships(List<Neo4jEdge> relationships) {
		this.relationships = relationships;
	}
}
