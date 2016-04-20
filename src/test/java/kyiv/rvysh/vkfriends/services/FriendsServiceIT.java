package kyiv.rvysh.vkfriends.services;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.utils.DepthLevel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-properties.xml", "classpath:/spring/dao.xml",
		"classpath:/spring/services.xml" })
public class FriendsServiceIT {
	@Autowired
	FriendsService service;

	@Test
	@Ignore
	public void testInsertFriends() {
		service.upsertFriends(17428494, 1, true);
	}
	
	@Test
	public void testFindFriends() {
		List<PersonInfo> res = service.findFriends(17428494, 1);
		System.out.println(res.size());
	}
	
	@Test
	public void testFindFriends2() {
		List<PersonInfo> res = service.findFriends(17428494, 2);
		System.out.println(res.size());
	}
	
	@Test
	public void testFindFriendsGraph() {
		Neo4jGraph<PersonInfo> res = service.findFriendsGraph(17428494, DepthLevel.ONE);
		System.out.println(res.getRelationships().size());
		System.out.println(res.getNodes().size());
	}
	
	@Test
	public void testFindClosestPeople() {
		Map<PersonInfo, Long> res = service.findClosestPeople(17428494, 10);
		System.out.println(res);
	}
}
