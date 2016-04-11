package kyiv.rvysh.vkfriends.domain.graph;

import java.util.Map;

public class Neo4jEdge {
	private String id;
	private String type;
	private String startNode;
	private String endNode;
	private Map<String, Object> properties;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStartNode() {
		return startNode;
	}
	public void setStartNode(String startNode) {
		this.startNode = startNode;
	}
	public String getEndNode() {
		return endNode;
	}
	public void setEndNode(String endNode) {
		this.endNode = endNode;
	}
	public Map<String, Object> getProperties() {
		return properties;
	}
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((endNode == null) ? 0 : endNode.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((startNode == null) ? 0 : startNode.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Neo4jEdge other = (Neo4jEdge) obj;
		if (endNode == null) {
			if (other.endNode != null)
				return false;
		} else if (!endNode.equals(other.endNode))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (startNode == null) {
			if (other.startNode != null)
				return false;
		} else if (!startNode.equals(other.startNode))
			return false;
		return true;
	}
}
