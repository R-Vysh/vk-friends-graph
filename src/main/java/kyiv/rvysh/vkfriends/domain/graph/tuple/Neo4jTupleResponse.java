package kyiv.rvysh.vkfriends.domain.graph.tuple;

import java.util.List;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jError;

public class Neo4jTupleResponse {
	private List<Neo4jTupleResult> results;
	private List<Neo4jError> errors;
	
	public List<Neo4jTupleResult> getResults() {
		return results;
	}
	public void setResults(List<Neo4jTupleResult> results) {
		this.results = results;
	}
	public List<Neo4jError> getErrors() {
		return errors;
	}
	public void setErrors(List<Neo4jError> errors) {
		this.errors = errors;
	}
}
