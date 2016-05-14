package kyiv.rvysh.vkfriends.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jersey.repackaged.com.google.common.collect.Lists;
import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jdbc.QueryExecutor;
import kyiv.rvysh.vkfriends.utils.DepthLevel;
import kyiv.rvysh.vkfriends.utils.ResourceUtils;

public class PersonDao {
	private static final Logger LOGGER = LoggerFactory.getLogger(PersonDao.class);
	private static String SAVE_FRIENDSHIP = ResourceUtils.classpathResourceAsString("sql/save-friends.sql");
	private static String SAVE_LINKS = ResourceUtils.classpathResourceAsString("sql/save-links.sql");
	private static String LOAD_FRIENDSHIP = ResourceUtils.classpathResourceAsString("sql/find-friends.sql");
	private static String LOAD_FRIENDSHIP_GRAPH = ResourceUtils.classpathResourceAsString("sql/find-friends-graph.sql");
	private static String LOAD_FRIENDSHIP_GRAPH_2 = ResourceUtils
			.classpathResourceAsString("sql/find-friends-graph-2.sql");
	private static String LOAD_CLOSEST_FRIENDS = ResourceUtils.classpathResourceAsString("sql/find-closest-friends.sql");
	private static String LOAD_CLOSEST_PEOPLE = ResourceUtils.classpathResourceAsString("sql/find-closest-people.sql");
	private static String LOAD_INFO = ResourceUtils.classpathResourceAsString("sql/find-person.sql");
	
	private QueryExecutor queryExecutor;

	public PersonDao(QueryExecutor queryExecutor) {
		this.queryExecutor = queryExecutor;
	}

	public void insertPerson(PersonInfo person) {
		LOGGER.info("Saving info for {}", person.getUid());
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", person.getUid());
		params.put("person", person);
		queryExecutor.update("MERGE (me:Person { uid: {user_id}}) SET me += {person}", params);
	}

	public void insertFriendship(int userId, List<PersonInfo> friends) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		// unlink friends
//		queryExecutor.update("MATCH (:Person { uid: {user_id} })-[r:FRIEND]-() DELETE r", params);
		// save friends
		LOGGER.info("Saving friends for {}", userId);
		for (List<PersonInfo> part : Lists.partition(friends, 100)) {
			params.put("friends", part);
			queryExecutor.update(SAVE_FRIENDSHIP, params);
		}
	}

	public List<PersonInfo> findFriends(int userId, int depth) {
		LOGGER.info("Querying friends for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		// Hack because path length can not be parametrized
		String query = LOAD_FRIENDSHIP.replaceAll("%DEPTH%", depth + "");
		return queryExecutor.queryForList(query, params, PersonInfo.class);
	}

	public Neo4jGraph<PersonInfo> findFriendsGraph(int userId, DepthLevel level) {
		LOGGER.info("Querying friends graph for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		switch (level) {
		case ONE: {
			return queryExecutor.queryForGraph(LOAD_FRIENDSHIP_GRAPH, params, PersonInfo.class);
		}
		case TWO: {
			return queryExecutor.queryForGraph(LOAD_FRIENDSHIP_GRAPH_2, params, PersonInfo.class);
		}
		default:
			return null;
		}
	}

	public Map<PersonInfo, Long> findClosestFriends(int userId, int size) {
		LOGGER.info("Querying closest friends for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("size", size);
		// Hack because path length can not be parametrized
		return queryExecutor.queryForMap(LOAD_CLOSEST_FRIENDS, params, PersonInfo.class, Long.class);
	}

	public Map<PersonInfo, Long> findClosestPeople(int userId, int size) {
		LOGGER.info("Querying closest people for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		params.put("size", size);
		// Hack because path length can not be parametrized
		return queryExecutor.queryForMap(LOAD_CLOSEST_PEOPLE, params, PersonInfo.class, Long.class);
	}
	
	public void insertLinksOnly(int userId, List<PersonInfo> filteredFriends) {
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		// save friends
		LOGGER.info("Saving friends for {}", userId);
		for (List<PersonInfo> part : Lists.partition(filteredFriends, 100)) {
			params.put("friends", part);
			queryExecutor.update(SAVE_LINKS, params);
		}
	}

	public PersonInfo loadPerson(int userId) {
		LOGGER.info("Querying info for {}", userId);
		Map<String, Object> params = new HashMap<>();
		params.put("user_id", userId);
		return queryExecutor.queryForObject(LOAD_INFO, params, PersonInfo.class);
	}
}
