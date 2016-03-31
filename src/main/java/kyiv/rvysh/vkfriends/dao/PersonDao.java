package kyiv.rvysh.vkfriends.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jdbc.QueryExecutor;
import kyiv.rvysh.vkfriends.utils.ResourceUtils;

public class PersonDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonDao.class);
	private static String SAVE_FRIENDSHIP = ResourceUtils.classpathResourceAsString("sql/save-friends.sql");
	private static String LOAD_FRIENDSHIP = ResourceUtils.classpathResourceAsString("sql/find-friends.sql");
	private static String LOAD_FRIENDSHIP_GRAPH = ResourceUtils.classpathResourceAsString("sql/find-friends-graph.sql");

	private QueryExecutor queryExecutor;

	public PersonDao(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	public void insertPerson(PersonInfo person) {
		LOGGER.info("Saving info for {}", person.uid);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", person.uid);
		params.put("person", person);
		queryExecutor.update("MERGE (me:Person { uid: {user_id}}) SET me += {person}", params);
	}
	
	public void insertFriendship(int userId, List<PersonInfo> friends) {
		LOGGER.info("Saving friends for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("friends", friends);
		params.put("user_id", userId);
		// unlink friends
		queryExecutor.update("MATCH (:Person { uid: {user_id} })-[r:FRIEND]-() DELETE r", params);
		// save friends
		queryExecutor.update(SAVE_FRIENDSHIP, params);
	}

	public List<PersonInfo> findFriends(int userId, int depth) {
		LOGGER.info("Querying friends for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		// Hack because path length can not be parametrized
		String query = LOAD_FRIENDSHIP.replaceAll("%DEPTH%", depth + "");
		return queryExecutor.queryForList(query, params, PersonInfo.class);
	}
	
	public Neo4jGraph<PersonInfo> findFriendsGraph(int userId, int depth) {
		LOGGER.info("Querying friends graph for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		// Hack because path length can not be parametrized
		String query = LOAD_FRIENDSHIP_GRAPH.replaceAll("%DEPTH%", depth + "");
		return queryExecutor.queryForGraph(query, params, PersonInfo.class);
	}
}
