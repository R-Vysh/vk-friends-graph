package kyiv.rvysh.vkfriends.domain.graph;

import java.util.List;

public class Neo4jResponse<T> {
	private List<Neo4jResult<T>> results;
	private List<Neo4jError> errors;
	
	public List<Neo4jResult<T>> getResults() {
		return results;
	}
	public void setResults(List<Neo4jResult<T>> results) {
		this.results = results;
	}
	public List<Neo4jError> getErrors() {
		return errors;
	}
	public void setErrors(List<Neo4jError> errors) {
		this.errors = errors;
	}
}
