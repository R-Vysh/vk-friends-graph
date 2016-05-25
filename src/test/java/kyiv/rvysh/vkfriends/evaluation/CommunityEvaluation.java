package kyiv.rvysh.vkfriends.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.graphalgorithms.evaluation.EvaluationService;
import kyiv.rvysh.vkfriends.services.FriendsService;
import kyiv.rvysh.vkfriends.services.GraphService;
import kyiv.rvysh.vkfriends.utils.DepthLevel;
import kyiv.rvysh.vkfriends.utils.Pair;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:/spring/test-properties.xml", "classpath:/spring/services.xml",
		"classpath:/spring/dao.xml" })
public class CommunityEvaluation {
	@Autowired
	GraphService<PersonInfo> graphService;
	@Autowired
	EvaluationService evaluationService;
	@Autowired
	FriendsService friendsService;

	@Test
	public void testGenerated() {
		execute(Method.KCLIQUE, 150);
	}

	@Test
	public void testReal() {
		int[] uids = { 17428494, 4204907, 5774444, 7984793, 7994381, 8928523, 345132141, 268383151,
				233362266 };
		List<Neo4jGraph<PersonInfo>> graphs = new ArrayList<>();
		for (int i : uids) {
			Neo4jGraph<PersonInfo> graph = friendsService.findFriendsGraph(i, DepthLevel.ONE);
			graphs.add(graph);
		}
		long start = System.currentTimeMillis();
		for (Neo4jGraph<PersonInfo> g : graphs) {
			execute(Method.KCLIQUE, 5, g);
		}
		long end = System.currentTimeMillis();
		System.out.println("Avg Time: " + ((end - start) / 1000.0) / (double) uids.length + " secs");
	}

	private void execute(Method method, double param) {
		System.out.println("Method : " + method.toString());
		double sum = 0.0;
		int times = 100;
		long start = System.currentTimeMillis();
		for (int i = 0; i < times; i++) {
			Map<PersonInfo, Double> expectedPersonComm = new HashMap<>();
			Pair<Collection<Set<PersonInfo>>, Neo4jGraph<PersonInfo>> testGraph = graphService
					.generateTestGraph();
			int j = 0;
			for (Set<PersonInfo> comm : testGraph.getFirst()) {
				for (PersonInfo pi : comm) {
					expectedPersonComm.put(pi, j * 1.0);
				}
				j++;
			}
			Map<PersonInfo, Double> actualPersonComm = new HashMap<>();
			Collection<Set<PersonInfo>> comms = execute(method, param, testGraph.getSecond());
			int k = 0;
			for (Set<PersonInfo> comm : comms) {
				for (PersonInfo pi : comm) {
					actualPersonComm.put(pi, k * 1.0);
				}
				k++;
			}
			double[] expected = new double[expectedPersonComm.entrySet().size()];
			double[] actual = new double[expectedPersonComm.entrySet().size()];
			int l = 0;
			int commSize = actualPersonComm.entrySet().size();
			for (Map.Entry<PersonInfo, Double> entry : expectedPersonComm.entrySet()) {
				expected[l] = entry.getValue();
				actual[l] = actualPersonComm.get(entry.getKey()) == null ? commSize + 1
						: actualPersonComm.get(entry.getKey());
				l++;
			}
			double nmi = evaluationService.evaluateNMI(expected, actual);
			sum += nmi;
			System.out.println(nmi);
		}
		long end = System.currentTimeMillis();
		System.out.println("Avg: " + sum / times);
		System.out.println("Avg Time: " + ((end - start) / 1000.0) / (double) times + " secs");
	}

	private Collection<Set<PersonInfo>> execute(Method method, double param, Neo4jGraph<PersonInfo> graph) {
		switch (method) {
		case CHINESE_WHISPERS:
			return graphService.findCommunitiesChineseWhisperer(graph);
		case GIRVAN_NEWMAN:
			return graphService.findCommunitiesGn(graph, (int) Math.round(param));
		case KCLIQUE:
			return graphService.findCommunitiesCpm(graph);
		case LABEL_PROP1:
			return graphService.findCommunitiesLabelPropagation1(graph);
		case LABEL_PROP2:
			return graphService.findCommunitiesLabelPropagation2(graph);
		case LABEL_PROP3:
			return graphService.findCommunitiesLabelPropagation3(graph, param);
		case TOP_LEADERS:
			return graphService.findCommunitiesTopleaders(graph, (int) Math.round(param));
		default:
			return null;
		}
	}

	private enum Method {
		GIRVAN_NEWMAN, CHINESE_WHISPERS, LABEL_PROP1, LABEL_PROP2, LABEL_PROP3, KCLIQUE, TOP_LEADERS;
	}
}
