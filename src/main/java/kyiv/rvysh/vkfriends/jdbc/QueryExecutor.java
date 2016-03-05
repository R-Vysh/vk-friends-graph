package kyiv.rvysh.vkfriends.jdbc;

import java.util.List;
import java.util.Map;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;

public interface QueryExecutor {
	void update(String query, Map<String, Object> params);
	
	Integer queryForInt(String query, Map<String, Object> params);
	
	Long queryForLong(String query, Map<String, Object> params);
	
	<T> T queryForObject(String query, Map<String, Object> params, Class<T> clazz);
	
	<T> List<T> queryForList(String query, Map<String, Object> params, Class<T> clazz);
	
	<T> Neo4jGraph<T> queryForGraph(String query, Map<String, Object> params, Class<T> clazz);
}
