package kyiv.rvysh.vkfriends.graphalgorithms.topleaders;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.data.Grouping;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.data.Partitioning;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.global.GlobalTopLeaders;

/**
 * @author Reihaneh
 *
 * @param <V>
 * @param <E>
 */
public class TopLeaders<V, E> {

	double outlierThereshod = 0, centersClosenessThreshold = 4, minCommunitySizeThreshold = 4, hubThreshold = 0;

	public TopLeaders() {
		super();
	}

	public Grouping<V> findCommunities(Graph<V, E> graph, int k) {
		Transformer<Graph<V, E>, Partitioning<V>> topLeaders = new GlobalTopLeaders<V, E>(k, outlierThereshod,
				hubThreshold, centersClosenessThreshold);
		return new Grouping<V>(topLeaders.transform(graph).getCommunities());
	}
}
