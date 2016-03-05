package kyiv.rvysh.vkfriends.jdbc;

import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;

// Workaround for named template which does not work
public class CustomJdbcTemplate extends NamedParameterJdbcTemplate {
	private JdbcTemplate template;

	public CustomJdbcTemplate(DataSource dataSource) {
		super(dataSource);
		template = new JdbcTemplate(dataSource);
	}

	@Override
	public <T> T query(String sql, Map<String, ?> paramMap, ResultSetExtractor<T> rse) throws DataAccessException {
		return template.query(replaceParameters(sql, paramMap), rse);
	}

	@Override
	public void query(String sql, Map<String, ?> paramMap, RowCallbackHandler rch) throws DataAccessException {
		template.query(replaceParameters(sql, paramMap), rch);
	}

	@Override
	public <T> List<T> query(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper) throws DataAccessException {
		return template.query(replaceParameters(sql, paramMap), rowMapper);
	}

	@Override
	public <T> T queryForObject(String sql, Map<String, ?> paramMap, RowMapper<T> rowMapper)
			throws DataAccessException {
		return template.queryForObject(replaceParameters(sql, paramMap), rowMapper);
	}

	@Override
	public <T> T queryForObject(String sql, Map<String, ?> paramMap, Class<T> requiredType) throws DataAccessException {
		return template.queryForObject(replaceParameters(sql, paramMap), requiredType);
	}

	@Override
	public Map<String, Object> queryForMap(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return template.queryForMap(replaceParameters(sql, paramMap));
	}

	@Override
	public <T> List<T> queryForList(String sql, Map<String, ?> paramMap, Class<T> elementType)
			throws DataAccessException {
		return template.queryForList(replaceParameters(sql, paramMap), elementType);
	}

	@Override
	public List<Map<String, Object>> queryForList(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return template.queryForList(replaceParameters(sql, paramMap));
	}

	@Override
	public SqlRowSet queryForRowSet(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return template.queryForRowSet(replaceParameters(sql, paramMap));
	}

	@Override
	public int update(String sql, Map<String, ?> paramMap) throws DataAccessException {
		return template.update(replaceParameters(sql, paramMap));
	}
	
	private String replaceParameters(String sql, Map<String, ?> paramMap) {
		for (Map.Entry<String, ?> entry : paramMap.entrySet()) {
			if (entry.getValue() instanceof String) {
				sql = sql.replaceAll("\\{" + entry.getKey() + "\\}", "\"" + entry.getValue() + "\"");
			} else {
				sql = sql.replaceAll("\\{" + entry.getKey() + "\\}", entry.getValue().toString());
			}
		}
		return sql;
	}
}
