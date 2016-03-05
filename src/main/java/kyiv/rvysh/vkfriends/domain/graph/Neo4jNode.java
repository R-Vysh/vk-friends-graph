package kyiv.rvysh.vkfriends.domain.graph;

import java.util.List;

public class Neo4jNode<T> {
	private String id;
	private List<String> labels;
	private T properties;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public List<String> getLabels() {
		return labels;
	}
	public void setLabels(List<String> labels) {
		this.labels = labels;
	}
	public T getProperties() {
		return properties;
	}
	public void setProperties(T properties) {
		this.properties = properties;
	}
}
