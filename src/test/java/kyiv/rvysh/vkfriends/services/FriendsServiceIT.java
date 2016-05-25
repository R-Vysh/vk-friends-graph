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
		int[] uidsWarmup = { 17428494, 4204907, 5774444, 7984793, 7994381, 8928523, 345132141, 268383151,
				233362266 };
		for (int uid : uidsWarmup) {
			friendsService.findFriendsGraph(uid, DepthLevel.ONE);
		}
		int[] uidsAll = { 17428494, 4204907, 5774444, 6522850, 7278749, 7444550, 7984793, 7994381, 8928523, 9102105,
				9340467, 9729353, 10077925, 10768255, 11054941, 11284434, 11526361, 11664298, 11700645,
				12888818, 13105380, 13953002, 13965586, 15740666, 15931519, 17559474, 17584665, 17967581,
				18129155, 18169709, 18518108, 20344767, 20511307, 21424973, 21919724, 22270138, 22759820,
				23357124, 24442408, 39614768, 41669287, 48112817, 50588814, 55088553, 55254112, 55582883,
				58840446, 60864676, 85269001, 90580500, 91095145, 105762519, 107398927, 114494408, 115265601,
				118562102, 127179041, 156597051, 168467357, 171926388, 179070452, 197337469, 213633041,
				219720057, 222374895, 226110402, 233362266, 244797658, 268383151, 323891037, 345132141 };
		int size = uidsAll.length;
		int nodesAll = 0;
		int relsAll = 0;
		long startAll = System.currentTimeMillis();
		for (int uid : uidsAll) {
			Neo4jGraph<PersonInfo> res = friendsService.findFriendsGraph(uid, DepthLevel.ONE);
			nodesAll += res.getNodes().size();
			relsAll += res.getRelationships().size();
//			System.out.println(graphService.findCliques(res).iterator().next().size());
		}
		long endAll = System.currentTimeMillis();
		System.out.println("Time: " + (endAll - startAll) / 1000.0 + "secs");
		System.out.println("Avg Time: " + (endAll - startAll) / (1000.0 * size) + "secs");
		System.out.println("Nodes: " + nodesAll);
		System.out.println("Relations: " + relsAll);
	}

	@Test
	public void testFindClosestPeople() {
		Map<PersonInfo, Long> res = friendsService.findClosestFriends(17428494, 10);
		System.out.println(res);
	}
	
	
}
