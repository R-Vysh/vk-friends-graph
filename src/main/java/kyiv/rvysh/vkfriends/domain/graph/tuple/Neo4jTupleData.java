package kyiv.rvysh.vkfriends.domain.graph.tuple;

import java.util.List;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;

public class Neo4jTupleData {
	private List<Object> row;
	private Neo4jGraph<Object> graph;
	
	public List<Object> getRow() {
		return row;
	}
	public void setRow(List<Object> row) {
		this.row = row;
	}
	public Neo4jGraph<Object> getGraph() {
		return graph;
	}
	public void setGraph(Neo4jGraph<Object> graph) {
		this.graph = graph;
	}
}
