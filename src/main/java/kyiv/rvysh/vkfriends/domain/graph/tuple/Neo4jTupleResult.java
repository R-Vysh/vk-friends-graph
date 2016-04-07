package kyiv.rvysh.vkfriends.domain.graph.tuple;

import java.util.List;

public class Neo4jTupleResult {
	private List<String> columns;
	private List<Neo4jTupleData> data;
	
	public List<String> getColumns() {
		return columns;
	}
	
	public void setColumns(List<String> columns) {
		this.columns = columns;
	}
	
	public List<Neo4jTupleData> getData() {
		return data;
	}
	
	public void setData(List<Neo4jTupleData> data) {
		this.data = data;
	}
}
