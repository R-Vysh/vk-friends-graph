package kyiv.rvysh.vkfriends.jdbc;

import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;

public class QueryExecutorJdbc implements QueryExecutor {

	private NamedParameterJdbcTemplate jdbcTemplate;
	
	public QueryExecutorJdbc(NamedParameterJdbcTemplate template) {
		this.jdbcTemplate = template;
	}
	
	@Override
	public void update(String query, Map<String, Object> params) {
		jdbcTemplate.update(query, params);
	}

	@Override
	public Integer queryForInt(String query, Map<String, Object> params) {
		return jdbcTemplate.queryForObject(query, params, Integer.class);
	}

	@Override
	public Long queryForLong(String query, Map<String, Object> params) {
		return jdbcTemplate.queryForObject(query, params, Long.class);
	}

	@Override
	public <T> T queryForObject(String query, Map<String, Object> params, Class<T> clazz) {
		return jdbcTemplate.queryForObject(query, params, clazz);
	}

	@Override
	public <T> List<T> queryForList(String query, Map<String, Object> params, Class<T> clazz) {
		return jdbcTemplate.queryForList(query, params, clazz);
	}

	@Override
	public <T> Neo4jGraph<T> queryForGraph(String query, Map<String, Object> params, Class<T> clazz) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <K, V> Map<K, V> queryForMap(String query, Map<String, Object> params, Class<K> keyClazz,
			Class<V> valueClazz) {
		// TODO Auto-generated method stub
		return null;
	}

}
