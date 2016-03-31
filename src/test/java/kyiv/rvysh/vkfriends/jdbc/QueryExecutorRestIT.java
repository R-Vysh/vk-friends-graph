package kyiv.rvysh.vkfriends.jdbc;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kyiv.rvysh.vkfriends.domain.PersonInfo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-properties.xml", "classpath:/spring/dao.xml" })
public class QueryExecutorRestIT {
	@Autowired
	@Qualifier("queryExecutorRest")
	QueryExecutor executor;
	
	@Test
	public void testQueryForLong() {
		String query = "MATCH (n) RETURN count(n)";
		Long result = executor.queryForLong( query, new HashMap<>() );
		System.out.println(result);
	}
	
	@Test
	@Ignore
	public void testDelete() {
		String query = "MATCH (n) DETACH DELETE n";
		executor.update( query, new HashMap<>() );
		String query2 = "MATCH (n) RETURN count(n)";
		Long result = executor.queryForLong( query2, new HashMap<>() );
		Assert.assertEquals(0L, result.longValue());
	}
	
	@Test
	public void testGetFriendsForId() {
		String query = "MATCH (me:Person {uid: {props}.user_id }) MATCH (me)-[:FRIEND*..2 ]-(fr:Person)  RETURN fr";
		Map<String, Object> params = new HashMap<>();
		params.put("depth", 1);
		params.put("user_id", 17428494);
		Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("props", params);
		List<PersonInfo> result = executor.queryForList(query, queryParams, PersonInfo.class);
		System.out.println(result.size());
	}
	
}
