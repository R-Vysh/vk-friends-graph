package kyiv.rvysh.vkfriends.domain.graph;

import java.util.List;

public class Neo4jData<T> {
	private List<List<T>> row;
	private Neo4jGraph<T> graph;
	
	public List<T> getRow() {
		return row.get(0);
	}
	public void setRow(List<List<T>> row) {
		this.row = row;
	}
	public Neo4jGraph<T> getGraph() {
		return graph;
	}
	public void setGraph(Neo4jGraph<T> graph) {
		this.graph = graph;
	}
}
