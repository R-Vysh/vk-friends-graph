package kyiv.rvysh.vkfriends.services;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.graphalgorithms.CliquePercolationMethod;
import kyiv.rvysh.vkfriends.graphalgorithms.GirvanNewmanClusterer;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.TopLeaders;
import kyiv.rvysh.vkfriends.jgrapht.JgraphtTransform;
import kyiv.rvysh.vkfriends.jung.JungTransform;

public class GraphService<V> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphService.class);

	private CliquePercolationMethod<V> cpmCommunityFinder;
	private GirvanNewmanClusterer<V> gnCommunityFinder;
	private TopLeaders<V, Neo4jEdge> topLeadersFinder;

	public Collection<Set<V>> findCliques(Neo4jGraph<V> graph) {
		Graph<V, Neo4jEdge> jGraph = JgraphtTransform.neo4jToJgraphtGraph(graph);
		BronKerboschCliqueFinder<V, Neo4jEdge> kf = new BronKerboschCliqueFinder<>(jGraph);
		return kf.getBiggestMaximalCliques();
	}

	public Collection<Set<V>> findCommunitiesCpm(Neo4jGraph<V> graph) {
		LOGGER.info("Finding communities CPM");
		return cpmCommunityFinder.execute(graph);
	}

	public Collection<Set<V>> findCommunitiesGn(Neo4jGraph<V> graph, int edgesToRemove) {
		LOGGER.info("Finding communities GN");
		return gnCommunityFinder.clusterize(graph, edgesToRemove);
	}

	public Collection<Set<V>> findCommunitiesTopleaders(Neo4jGraph<V> graph, int clusters) {
		LOGGER.info("Finding communities");
		edu.uci.ics.jung.graph.Graph<V, Neo4jEdge> jungGraph = JungTransform.neo4jToJungGraph(graph);
		return topLeadersFinder.findCommunities(jungGraph, clusters).toSetCollection();
	}

	public void setCpmCommunityFinder(CliquePercolationMethod<V> cpmCommunityFinder) {
		this.cpmCommunityFinder = cpmCommunityFinder;
	}

	public void setGnCommunityFinder(GirvanNewmanClusterer<V> gnCommunityFinder) {
		this.gnCommunityFinder = gnCommunityFinder;
	}

	public void setTopLeadersFinder(TopLeaders<V, Neo4jEdge> topLeadersFinder) {
		this.topLeadersFinder = topLeadersFinder;
	}

}
