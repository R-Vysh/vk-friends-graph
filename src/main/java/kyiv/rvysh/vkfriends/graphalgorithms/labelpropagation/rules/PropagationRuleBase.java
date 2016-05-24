package kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.rules;

import kyiv.rvysh.vkfriends.graphalgorithms.labelpropagation.LabelPropagationClusterer;

public abstract class PropagationRuleBase<T> {
	public LabelPropagationClusterer<T> clusteringEngine;

	public PropagationRuleBase(LabelPropagationClusterer<T> clusterer) {
		this.clusteringEngine = clusterer;
	}

	public abstract Integer getDominantClusterInNeighbourhood(T node);

	public boolean allNodesAssignedToDominantClusterInNeighbourhood() {
		boolean result = true;
		for (T node : clusteringEngine.getNodes()) {
			result = result && clusteringEngine.getNodeClusterMapping().get(node)
					.equals(getDominantClusterInNeighbourhood(node));
		}
		return result;
	}
}
