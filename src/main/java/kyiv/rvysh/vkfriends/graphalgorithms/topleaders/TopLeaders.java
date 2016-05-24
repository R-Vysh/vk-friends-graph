package kyiv.rvysh.vkfriends.graphalgorithms.topleaders;

import java.util.Collection;
import java.util.Set;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.data.Grouping;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.data.Partitioning;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.global.GlobalTopLeaders;
import kyiv.rvysh.vkfriends.jung.JungTransform;

/**
 * @author Reihaneh
 *
 * @param <V>
 * @param <E>
 */
public class TopLeaders<V> {

	private double outlierThereshod = 0;
	private double centersClosenessThreshold = 4;
	private double minCommunitySizeThreshold = 4;
	private double hubThreshold = 0;

	public TopLeaders() {
		super();
	}

	public Collection<Set<V>> clusterize(Neo4jGraph<V> graph, int k) {
		edu.uci.ics.jung.graph.Graph<V, Neo4jEdge> jungGraph = JungTransform.neo4jToJungGraph(graph);
		Transformer<Graph<V, Neo4jEdge>, Partitioning<V>> topLeaders = new GlobalTopLeaders<V, Neo4jEdge>(k,
				outlierThereshod, hubThreshold, centersClosenessThreshold);
		return new Grouping<V>(topLeaders.transform(jungGraph).getCommunities()).toSetCollection();
	}
}
