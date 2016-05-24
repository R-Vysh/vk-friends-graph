package kyiv.rvysh.vkfriends.graphalgorithms.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.UndirectedSparseGraph;
import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jung.JungTransform;
import kyiv.rvysh.vkfriends.utils.Pair;

public class PlantedLPartition {
	private int l = 4;
	private int g = 32;
	private double pin = 0.4;
	private double pout = (0.5 - pin) / 3;

	public Pair<Collection<Set<PersonInfo>>, Graph<PersonInfo, Neo4jEdge>> generateJungGraph() {
		Random rand = new Random();
		Graph<PersonInfo, Neo4jEdge> result = new UndirectedSparseGraph<PersonInfo, Neo4jEdge>();
		List<Set<PersonInfo>> communities = new ArrayList<>();
		for (Integer i = 0; i < l; i++) {
			Set<PersonInfo> community = new HashSet<>();
			for (Integer j = 0; j < g; j++) {
				PersonInfo pi = new PersonInfo();
				pi.setUid(i * g + j);
				pi.firstName = "test";
				pi.lastName = "test";
				result.addVertex(pi);
				community.add(pi);
			}
			communities.add(community);
		}
		int edgeId = 0;
		for (int k = 0; k < communities.size(); k++) {
			for (Integer i = 0; i < communities.get(k).size(); i++) {
				for (Integer j = i + 1; j < communities.get(k).size(); j++) {
					if (i != j) {
						if (rand.nextDouble() < pin) {
							Neo4jEdge edge = new Neo4jEdge();
							edge.setId(edgeId + "");
							edge.setProperties(new HashMap<>());
							edgeId++;
							PersonInfo pi1 = new PersonInfo();
							pi1.setUid(k * g + i);
							pi1.firstName = "test";
							pi1.lastName = "test";
							PersonInfo pi2 = new PersonInfo();
							pi2.setUid(k * g + j);
							pi2.firstName = "test";
							pi2.lastName = "test";
							result.addEdge(edge, pi1, pi2);
						}
					}
				}
			}
		}
		for (int k = 0; k < communities.size(); k++) {
			for (Integer i = 0; i < communities.get(k).size(); i++) {
				for (int l = k + 1; l < communities.size(); l++) {
					for (Integer j = 0; j < communities.get(l).size(); j++) {
						if (rand.nextDouble() < pout) {
							Neo4jEdge edge = new Neo4jEdge();
							edge.setId(edgeId + "");
							edge.setProperties(new HashMap<>());
							edgeId++;
							PersonInfo pi1 = new PersonInfo();
							pi1.setUid(k * g + i);
							pi1.firstName = "test";
							pi1.lastName = "test";
							PersonInfo pi2 = new PersonInfo();
							pi2.setUid(l * g + j);
							pi2.firstName = "test";
							pi2.lastName = "test";
							result.addEdge(edge, pi1, pi2);
						}
					}
				}
			}
		}
		return new Pair<Collection<Set<PersonInfo>>, Graph<PersonInfo, Neo4jEdge>>(communities, result);
	}

	public Pair<Collection<Set<PersonInfo>>, Neo4jGraph<PersonInfo>> generateNeo4jGraph() {
		Pair<Collection<Set<PersonInfo>>, Graph<PersonInfo, Neo4jEdge>> res = generateJungGraph();
		return new Pair<Collection<Set<PersonInfo>>, Neo4jGraph<PersonInfo>>(res.getFirst(),
				JungTransform.jungToNeo4jGraph(res.getSecond()));
	}
}
