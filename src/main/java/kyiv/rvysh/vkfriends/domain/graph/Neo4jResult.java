package kyiv.rvysh.vkfriends.domain.graph;

import java.util.List;

public class Neo4jResult<T> {
	private List<String> columns;
	private List<Neo4jData<T>> data;
	
	public List<String> getColumns() {
		return columns;
	}
	
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public List<Neo4jData<T>> getData() {
		return data;
	}
	
	public void setData(List<Neo4jData<T>> data) {
		this.data = data;
	}
}
