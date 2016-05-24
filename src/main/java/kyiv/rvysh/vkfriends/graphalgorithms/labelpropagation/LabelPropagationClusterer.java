package kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.rules.LPAPropagationRule;
import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.rules.PropagationRuleBase;
import kyiv.rvysh.vkfriends.jung.JungTransform;

public class LabelPropagationClusterer<T> {

	private Map<T, Integer> nodeClusterMapping = new HashMap<>();
	private List<T> nodes;
	private Graph<T, Neo4jEdge> graph;
	
	public Collection<Set<T>> clusterize(Neo4jGraph<T> g) {
		return clusterize(JungTransform.neo4jToJungGraph(g), new LPAPropagationRule<T>(this));
	}
	
	public Collection<Set<T>> clusterize(Neo4jGraph<T> g, PropagationRuleBase<T> propagationRule) {
		return clusterize(JungTransform.neo4jToJungGraph(g), propagationRule);
	}
	
	private List<Set<T>> clusterize(Graph<T, Neo4jEdge> graph, PropagationRuleBase<T> propagationRule) {
		this.graph = graph;
		nodes = new ArrayList<>();
		nodes.addAll(graph.getVertices());
		for (int i = 0; i < nodes.size(); i++) {
			nodeClusterMapping.put(nodes.get(i), i);
		}
		while (!propagationRule.allNodesAssignedToDominantClusterInNeighbourhood()) {
			Collections.shuffle(nodes);
			for (T node : nodes) {
				nodeClusterMapping.put(node, propagationRule.getDominantClusterInNeighbourhood(node));
			}
		}
		return getResultingClusters(nodeClusterMapping);
	}

	private List<Set<T>> getResultingClusters(Map<T, Integer> nodeClusterMapping) {
		List<Set<T>> result = new ArrayList<>();
		
		Map<Integer, List<T>> clusters = new HashMap<>();
		for (Map.Entry<T, Integer> nodesCluster : nodeClusterMapping.entrySet()) {
			List<T> clusterNodes = new ArrayList<T>();
			Integer cluster = nodesCluster.getValue();
			if (clusters.containsKey(cluster)) {
				clusterNodes = clusters.get(cluster);
			}
			clusterNodes.add(nodesCluster.getKey());
			clusters.put(cluster, clusterNodes);
		}
		for (Map.Entry<Integer, List<T>> clusterNodes : clusters.entrySet()) {
			Set<T> nodes = new HashSet<>();
			nodes.addAll(clusterNodes.getValue());
			result.add(nodes);
		}
		return result;
	}

	public Map<T, Integer> getNodeClusterMapping() {
		return nodeClusterMapping;
	}

	public List<T> getNodes() {
		return nodes;
	}

	public Graph<T, Neo4jEdge> getGraph() {
		return graph;
	}
}