package kyiv.rvysh.vkfriends.domain.graph;

import java.util.List;

public class Neo4jData<T> {
	private List<List<T>> row;
	private List<Neo4jGraph<T>> graph;
	
	public List<List<T>> getRow() {
		return row;
	}
	public void setRow(List<List<T>> row) {
		this.row = row;
	}
	public List<Neo4jGraph<T>> getGraph() {
		return graph;
	}
	public void setGraph(List<Neo4jGraph<T>> graph) {
		this.graph = graph;
	}
}
