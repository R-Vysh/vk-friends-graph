package kyiv.rvysh.vkfriends.services;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-properties.xml", "classpath:/spring/dao.xml",
		"classpath:/spring/services.xml" })
public class FriendsServiceIT {
	@Autowired
	FriendsService service;

	@Test
	public void testInsertFriends() {
		service.upsertFriends(17428494, 1, true);
	}
	
	@Test
	public void testFindFriends() {
		List<PersonInfo> res = service.findFriends(17428494, 1);
		System.out.println(res.size());
	}
	
	@Test
	public void testFindFriendsGraph() {
		Neo4jGraph<PersonInfo> res = service.findFriendsGraph(17428494, 1);
		System.out.println(res.getRelationships().size());
		System.out.println(res.getNodes().size());
	}
}
