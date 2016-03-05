package kyiv.rvysh.vkfriends.jdbc;

import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Result;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;

public class QueryExecutorEmbedded implements QueryExecutor {
	private GraphDatabaseService graphDb;

	public QueryExecutorEmbedded(GraphDatabaseService graphDb) {
		this.graphDb = graphDb;
	}

	@Override
	public void update(String query, Map<String, Object> params) {
		graphDb.execute(query, params);
	}

	@Override
	public Integer queryForInt(String query, Map<String, Object> params) {
		Result res = graphDb.execute(query, params);
		Map<String, Object> row = res.next();
		return (Integer) row.get(res.columns().get(0));
	}
	
	@Override
	public Long queryForLong(String query, Map<String, Object> params) {
		Result res = graphDb.execute(query, params);
		Map<String, Object> row = res.next();
		return (Long) row.get(res.columns().get(0));
	}

	@Override
	public <T> T queryForObject(String query, Map<String, Object> params, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> List<T> queryForList(String query, Map<String, Object> params, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> Neo4jGraph<T> queryForGraph(String query, Map<String, Object> params, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}
}
