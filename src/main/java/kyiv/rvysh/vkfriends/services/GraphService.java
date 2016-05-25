package kyiv.rvysh.vkfriends.services;

import java.util.Collection;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.graphalgorithms.ChineseWhispersClusterer;
import kyiv.rvysh.vkfriends.graphalgorithms.CliquePercolationMethod;
import kyiv.rvysh.vkfriends.graphalgorithms.GirvanNewmanClusterer;
import kyiv.rvysh.vkfriends.graphalgorithms.evaluation.PlantedLPartition;
import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.LabelPropagationClusterer;
import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.rules.LPAmPropagationRule;
import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.rules.LPArPropagationRule;
import kyiv.rvysh.vkfriends.graphalgorithms.markov.MarkovClusterer;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.TopLeaders;
import kyiv.rvysh.vkfriends.jgrapht.JgraphtTransform;
import kyiv.rvysh.vkfriends.utils.Pair;

public class GraphService<V> {
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphService.class);

	private CliquePercolationMethod<V> cpmCommunityFinder;
	private GirvanNewmanClusterer<V> gnCommunityFinder;
	private TopLeaders<V> topLeadersFinder;
	private LabelPropagationClusterer<V> lpCommunityFinder;
	private ChineseWhispersClusterer<V> cwCommunityFinder;
	private MarkovClusterer<V> markovCommunityFinder;
	private PlantedLPartition graphGenerator;

	public Pair<Collection<Set<PersonInfo>>, Neo4jGraph<PersonInfo>> generateTestGraph() {
		return graphGenerator.generateNeo4jGraph();
	}
	
	public Collection<Set<V>> findCliques(Neo4jGraph<V> graph) {
		Graph<V, Neo4jEdge> jGraph = JgraphtTransform.neo4jToJgraphtGraph(graph);
		BronKerboschCliqueFinder<V, Neo4jEdge> kf = new BronKerboschCliqueFinder<>(jGraph);
		return kf.getBiggestMaximalCliques();
	}

	public Collection<Set<V>> findCommunitiesCpm(Neo4jGraph<V> graph) {
		LOGGER.info("Finding communities Clique Percolation");
		return cpmCommunityFinder.clusterize(graph);
	}

	public Collection<Set<V>> findCommunitiesGn(Neo4jGraph<V> graph, int edgesToRemove) {
		LOGGER.info("Finding communities Girvan-Newman");
		return gnCommunityFinder.clusterize(graph, edgesToRemove);
	}

	public Collection<Set<V>> findCommunitiesTopleaders(Neo4jGraph<V> graph, int numOfClusters) {
		LOGGER.info("Finding communities top leaders");
		return topLeadersFinder.clusterize(graph, numOfClusters);
	}

	public Collection<Set<V>> findCommunitiesLabelPropagation1(Neo4jGraph<V> graph) {
		LOGGER.info("Finding communities LP1");
		return lpCommunityFinder.clusterize(graph);
	}

	public Collection<Set<V>> findCommunitiesLabelPropagation2(Neo4jGraph<V> graph) {
		LOGGER.info("Finding communities LP2");
		return lpCommunityFinder.clusterize(graph, new LPArPropagationRule<V>(lpCommunityFinder));
	}

	public Collection<Set<V>> findCommunitiesLabelPropagation3(Neo4jGraph<V> graph,
			double resolutionParameter) {
		LOGGER.info("Finding communities LP3");
		return lpCommunityFinder.clusterize(graph,
				new LPAmPropagationRule<V>(lpCommunityFinder, resolutionParameter));
	}

	public Collection<Set<V>> findCommunitiesChineseWhisperer(Neo4jGraph<V> graph) {
		LOGGER.info("Finding communities Chinese Whispers");
		return cwCommunityFinder.clusterize(graph);
	}

	public Collection<Set<V>> findCommunitiesMarkov(Neo4jGraph<V> graph) {
		LOGGER.info("Finding communities Markov");
		return markovCommunityFinder.clusterize(graph);
	}
	
	public void setCpmCommunityFinder(CliquePercolationMethod<V> cpmCommunityFinder) {
		this.cpmCommunityFinder = cpmCommunityFinder;
	}

	public void setGnCommunityFinder(GirvanNewmanClusterer<V> gnCommunityFinder) {
		this.gnCommunityFinder = gnCommunityFinder;
	}

	public void setTopLeadersFinder(TopLeaders<V> topLeadersFinder) {
		this.topLeadersFinder = topLeadersFinder;
	}

	public void setLpCommunityFinder(LabelPropagationClusterer<V> lpCommunityFinder) {
		this.lpCommunityFinder = lpCommunityFinder;
	}

	public void setCwCommunityFinder(ChineseWhispersClusterer<V> cwCommunityFinder) {
		this.cwCommunityFinder = cwCommunityFinder;
	}

	public void setMarkovCommunityFinder(MarkovClusterer<V> markovCommunityFinder) {
		this.markovCommunityFinder = markovCommunityFinder;
	}

	public void setGraphGenerator(PlantedLPartition graphGenerator) {
		this.graphGenerator = graphGenerator;
	}
}
