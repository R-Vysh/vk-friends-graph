package kyiv.rvysh.vkfriends.services;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.graphalgorithms.CliquePercolationMethod;
import kyiv.rvysh.vkfriends.jgrapht.JgraphtTransform;

public class GraphService<V> {
	private CliquePercolationMethod<V> communityFinder;
	
	public Collection<Set<V>> findCliques(Neo4jGraph<V> graph) {
		Graph<V, Neo4jEdge> jGraph = JgraphtTransform.neo4jToJgraphtGraph(graph);
		BronKerboschCliqueFinder<V, Neo4jEdge> kf = new BronKerboschCliqueFinder<>(jGraph);
		return kf.getBiggestMaximalCliques();
	}

	public Collection<Set<V>> findCommunities(Neo4jGraph<V> graph) {
		return communityFinder.execute(graph);
	}

	public void setCommunityFinder(CliquePercolationMethod<V> communityFinder) {
		this.communityFinder = communityFinder;
	}
}
