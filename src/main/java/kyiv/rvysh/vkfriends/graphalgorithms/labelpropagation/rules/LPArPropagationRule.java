package kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.rules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.LabelPropagationClusterer;
import kyiv.rvysh.vkfriends.utils.MapUtils;

/**
 * Provides an implementation of LPAr's propagation rule as described in Barber,
 * Michael J., and John W. Clark.
 * "Detecting network communities by propagating labels under constraints."
 * Physical Review E 80.2 (2009): 026129.
 * 
 * @author Oualid Boutemine <boutemine.oualid@courrier.uqam.ca>
 */
public class LPArPropagationRule<T> extends PropagationRuleBase<T> {

	public LPArPropagationRule(LabelPropagationClusterer<T> clusterer) {
		super(clusterer);
	}

	/**
	 * This rule selects the most frequent cluster label amongst the neighbors
	 * of the processed node as its new cluster. The frequency is calculated
	 * based on the total weight of links connecting the neighbors bearing the
	 * same cluster label. In case two or more labels are equally frequent, the
	 * rule selects a random one from the dominant group regardless of the
	 * current label of the processed node.
	 */
	@Override
	public Integer getDominantClusterInNeighbourhood(T node) {
		Collection<T> neighbours = clusteringEngine.getGraph().getNeighbors(node);
		if (neighbours.isEmpty()) {
			return this.clusteringEngine.getNodeClusterMapping().get(node);
			// no clusters in neighbourhood, a disconnected node.
		}

		Map<Integer, Integer> neighborClusterWeights = new HashMap<Integer, Integer>();

		// Calculating the clusters' weights in the neighbourhood of the
		// processed node.
		for (T currentNeighbor : neighbours) {
			Integer neighboringCluster = this.clusteringEngine.getNodeClusterMapping().get(currentNeighbor);
			// Although the original LPA algorithm doesn't consider the weight
			// on edges, Barber and Clark
			// in (Detecting network communities by propagating labels under
			// constraints. Physical Review E,80(2),026129.)
			// indicate that the discrete nature of the objective function being
			// optimized by LPA makes it possible to support weighted networks
			// too.
			int clusterWeight = 1;

			if (neighborClusterWeights.containsKey(neighboringCluster)) {
				clusterWeight += neighborClusterWeights.get(neighboringCluster);
			}
			neighborClusterWeights.put(neighboringCluster, clusterWeight);
		}

		// picking the cluster with the heighest weight. In case two or more
		// clusters exhibit the same weight, the LPA rule must pick a random one
		// from the dominant group.
		// Shuffling the clusters list and picking the cluster with the highest
		// weight.
		// This rule selects a random dominant cluster regardless the current
		// cluster of the processed node.
		neighborClusterWeights = MapUtils.shuffle(neighborClusterWeights, new Random());
		int maxWeight = Collections.max(neighborClusterWeights.values());
		Integer prevailingClusterColor = MapUtils.getKeyByValue(neighborClusterWeights, maxWeight);
		return prevailingClusterColor;
	}

	/**
	 * Returns a list containing the dominant cluster labels in the neighborhood
	 * of a node. Used to check whether each node is already bearing a dominant
	 * cluster label among its neighborhood.
	 */
	private List<Integer> getDominantClustersInNeighbourhood(T node) {
		Collection<T> neighbours = clusteringEngine.getGraph().getNeighbors(node);
		Map<Integer, Integer> neighborClusterWeights = new HashMap<Integer, Integer>();
		// Calculating clusters weights in the processed node's neighbourhood.
		for (T currentNeighbor : neighbours) {
			Integer neighboringCluster = this.clusteringEngine.getNodeClusterMapping().get(currentNeighbor);
			// Although the original LPA algorithm doesn't consider the weight
			// on edges, Barber and Clark
			// in (Detecting network communities by propagating labels under
			// constraints. Physical Review E,80(2),026129.)
			// indicate that the discrete nature of the objective function being
			// optimized by LPA makes it possible to support weighted networks
			// too.
			int clusterWeight = 1;

			if (neighborClusterWeights.containsKey(neighboringCluster)) {
				clusterWeight += neighborClusterWeights.get(neighboringCluster);
			}
			neighborClusterWeights.put(neighboringCluster, clusterWeight);
		}
		// returning the clusters
		ArrayList<Integer> result = new ArrayList<Integer>();
		if (neighborClusterWeights.isEmpty()) {
			// disconnected node, must be assigned to its own cluster.
			Integer prevailingCluster = this.clusteringEngine.getNodeClusterMapping().get(node);
			result.add(prevailingCluster);
			return result;
		}
		// selecting the dominant clusters based on the combined weight of the
		// edges linking their members to the processed node.
		int maxWeight = Collections.max(neighborClusterWeights.values());
		for (Map.Entry<Integer, Integer> neighborClusterWeight : neighborClusterWeights.entrySet()) {
			double currentWeight = neighborClusterWeight.getValue();
			if (currentWeight == maxWeight) {
				result.add(neighborClusterWeight.getKey());
			}
		}
		return result;
	}

	@Override
	public boolean allNodesAssignedToDominantClusterInNeighbourhood() {
		boolean result = true;
		for (T node : clusteringEngine.getGraph().getVertices()) {
			List<Integer> prevailingClusters = this.getDominantClustersInNeighbourhood(node);
			result = result && prevailingClusters.contains(this.clusteringEngine.getNodeClusterMapping().get(node));
		}
		return result;
	}
}
