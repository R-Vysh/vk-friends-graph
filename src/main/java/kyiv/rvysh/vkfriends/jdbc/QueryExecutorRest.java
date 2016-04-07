package kyiv.rvysh.vkfriends.jdbc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jData;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jResponse;
import kyiv.rvysh.vkfriends.domain.graph.tuple.Neo4jTupleData;
import kyiv.rvysh.vkfriends.domain.graph.tuple.Neo4jTupleResponse;
import kyiv.rvysh.vkfriends.utils.CustomCharacterEscapes;
import kyiv.rvysh.vkfriends.utils.ResourceUtils;

public class QueryExecutorRest implements QueryExecutor {
	private static final String TRANSACTION_PATH = "/db/data/transaction/commit";
	private static final String TEMPLATE = ResourceUtils.classpathResourceAsString("http-template");
	private static final CharMatcher STRING_CLEANER = CharMatcher.JAVA_DIGIT.or(CharMatcher.JAVA_LETTER)
			.or(CharMatcher.anyOf("()[]/ :\",-._{}")).precomputed();

	private ObjectMapper mapper = getEscapingMapper();
	private Client client;
	private String hostUrl;
	private String auth;

	public QueryExecutorRest(Client client) {
		this.client = client;
	}

	@Override
	public void update(String query, Map<String, Object> params) {
		execute(query, params, Void.class);
	}

	@Override
	public Integer queryForInt(String query, Map<String, Object> params) {
		return execute(query, params, Integer.class).get(0);
	}

	@Override
	public Long queryForLong(String query, Map<String, Object> params) {
		return execute(query, params, Long.class).get(0);
	}

	@Override
	public <T> T queryForObject(String query, Map<String, Object> params, Class<T> clazz) {
		return execute(query, params, clazz).get(0);
	}

	@Override
	public <T> List<T> queryForList(String query, Map<String, Object> params, Class<T> clazz) {
		return execute(query, params, clazz);
	}

	@Override
	public <T> Neo4jGraph<T> queryForGraph(String query, Map<String, Object> params, Class<T> clazz) {
		Neo4jResponse<T> response = executeInternal(query, params, clazz);
		Neo4jGraph<T> result = new Neo4jGraph<>();
		if (response != null) {
			result = response.getResults().get(0).getData().get(0).getGraph();
		}
		return result;
	}

	@Override
	public <K, V> Map<K, V> queryForMap(String query, Map<String, Object> params, Class<K> keyClazz,
			Class<V> valueClazz) {
		Neo4jTupleResponse response = executeTupleInternal(query, params);
		Map<K, V> result = new HashMap<K, V>();
		if (response != null) {
			for (Neo4jTupleData data : response.getResults().get(0).getData()) {
				try {
					K key = mapper.readValue(mapper.writeValueAsString(data.getRow().get(0)), keyClazz);
					V value = mapper.readValue(mapper.writeValueAsString(data.getRow().get(1)), valueClazz);
					result.put(key, value);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return result;
	}

	private <T> List<T> execute(String query, Map<String, Object> params, Class<T> clazz) {
		Neo4jResponse<T> response = executeInternal(query, params, clazz);
		List<T> result = new ArrayList<>();
		if (response != null) {
			for (Neo4jData<T> data : response.getResults().get(0).getData()) {
				for (List<T> row : data.getRow()) {
					result.addAll(row);
				}
			}
		}
		return result;
	}

	private <T> Neo4jResponse<T> executeInternal(String query, Map<String, Object> params, Class<T> clazz) {
		Invocation.Builder reqBuilder = defaultBuilder();
		try {
			String responseString = reqBuilder.post(prepareRequest(query, params), String.class);
			JavaType type = mapper.getTypeFactory().constructParametricType(Neo4jResponse.class, clazz);
			Neo4jResponse<T> response = mapper.readValue(responseString, type);
			if (!response.getErrors().isEmpty()) {
				throw new RuntimeException("Neo4j errors : " + response.getErrors());
			}
			return response;
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return null;
	}

	private Neo4jTupleResponse executeTupleInternal(String query, Map<String, Object> params) {
		Invocation.Builder reqBuilder = defaultBuilder();
		try {
			String responseString = reqBuilder.post(prepareRequest(query, params), String.class);
			Neo4jTupleResponse response = mapper.readValue(responseString, Neo4jTupleResponse.class);
			if (!response.getErrors().isEmpty()) {
				throw new RuntimeException("Neo4j errors : " + response.getErrors());
			}
			return response;
		} catch (IOException e) {
			Throwables.propagate(e);
		}
		return null;
	}

	private Builder defaultBuilder() {
		Builder builder = client.target(hostUrl).path(TRANSACTION_PATH).request().header("Authorization", auth)
				.accept("application/json; charset=UTF-8");
		return builder;
	}

	private Entity<String> prepareRequest(String query, Map<String, Object> params) throws JsonProcessingException {
		String paramsStr = STRING_CLEANER.retainFrom(mapper.writeValueAsString(params));
		String payload = TEMPLATE.replaceAll("%QUERY%", refineQuery(query)).replaceAll("%PARAMS%", paramsStr);
		return Entity.entity(payload, MediaType.APPLICATION_JSON);
	}

	private String refineQuery(String query) {
		return query.replace('\n', ' ').replace('\r', ' ');
	}

	public ObjectMapper getEscapingMapper() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.getFactory().setCharacterEscapes(new CustomCharacterEscapes());
		mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
		return mapper;
	}

	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}

	public void setAuth(String auth) {
		this.auth = auth;
	}
}
