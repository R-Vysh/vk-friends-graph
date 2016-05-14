package kyiv.rvysh.vkfriends.services;

import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kyiv.rvysh.vkfriends.dao.PersonDao;
import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.utils.DepthLevel;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-properties.xml", "classpath:/spring/dao.xml",
		"classpath:/spring/services.xml" })
public class FriendsServiceIT {
	private static final Logger LOGGER = LoggerFactory.getLogger(FriendsServiceIT.class);
	
	@Autowired
	FriendsService friendsService;

	@Autowired
	GraphService<PersonInfo> graphService;

	@Test
	@Ignore
	public void testInsertFriends() {
		int[] vars = { 44256175 };
		for (int i = 0; i < vars.length; i++) {
			System.out.println("I ============" + i + " / " + vars.length);
			friendsService.upsertFriendsGraph(vars[i], 1, true);
		}
	}

	@Test
	// @Ignore
	public void testClustering() {
		int[] vars = { 44256175 };
		LOGGER.info("Start");
		for (int i = 0; i < vars.length; i++) {
			System.out.println("I ============" + i + " / " + vars.length);
			Neo4jGraph<PersonInfo> friendsGraph = friendsService.findFriendsGraph(vars[i], DepthLevel.ONE);
			graphService.findCommunitiesCpm(friendsGraph);
		}
		LOGGER.info("Finish");
	}

	@Test
	public void testFindFriends2() {
		List<PersonInfo> res = friendsService.findFriends(17428494, 1);
		System.out.println(res.size());
	}

	@Test
	public void testFindFriendsGraph() {
		Neo4jGraph<PersonInfo> res = friendsService.findFriendsGraph(17428494, DepthLevel.ONE);
		System.out.println(res.getRelationships().size());
		System.out.println(res.getNodes().size());
	}

	@Test
	public void testFindClosestPeople() {
		Map<PersonInfo, Long> res = friendsService.findClosestFriends(17428494, 10);
		System.out.println(res);
	}
}
