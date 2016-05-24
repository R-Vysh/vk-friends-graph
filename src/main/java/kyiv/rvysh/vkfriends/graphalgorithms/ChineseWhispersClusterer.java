package kyiv.rvysh.vkfriends.graphalgorithms;

import java.util.*;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jung.JungTransform;

public class ChineseWhispersClusterer<T> {
	private int iterations = 10;
	private boolean randomColoring = true;
	private Propagation propagation = Propagation.TOP;
	private double propagationVoteValue = 0.1d;
	private boolean stepwiseUpdate = false;
	private double minimumEdgeWeight = 0.0d;
	private boolean randomizedNodeOrder = false;
	private Unconnected unconnected = Unconnected.IGNORE;

	public enum Propagation {
		TOP, DIST, DIST_LOG, VOTE;
	}

	public enum Unconnected {
		IGNORE, COMBINE, INDIVIDUAL;
	}

	public Collection<Set<T>> clusterize(Neo4jGraph<T> g) {
		Graph<T, Neo4jEdge> graph = JungTransform.neo4jToJungGraph(g);
		List<Set<T>> result = new ArrayList<>();
		Map<T, Integer> classes = new HashMap<>();
		int counter = 1;
		List<T> connectedNodes = new ArrayList<T>();
		for (T node : graph.getVertices()) {
			if (unconnected == Unconnected.INDIVIDUAL)
				classes.put(node, counter++);
			if (graph.getNeighbors(node).iterator().hasNext()) {
				connectedNodes.add(node);
				if (unconnected != Unconnected.INDIVIDUAL)
					classes.put(node, counter++);
			} else if (unconnected == Unconnected.COMBINE)
				classes.put(node, 0);
		}

		for (int i = 0; i < iterations; i++) {
			Set<T> inputNodes = new HashSet<T>();
			if (randomizedNodeOrder) {
				inputNodes = new TreeSet<T>(new Comparator<T>() {
					@Override
					public int compare(T o1, T o2) {
						int decision = (int) (2 * Math.round(Math.random()) - 1);
						return decision == 0 ? 1 : decision;
					}
				});
			}
			inputNodes.addAll(connectedNodes);
			Map<T, Integer> classesBuffer = new HashMap<T, Integer>(classes);
			for (T node : inputNodes) {
				Map<Integer, Double> rankedClasses = new HashMap<Integer, Double>();
				for (Neo4jEdge edge : graph.getIncidentEdges(node)) {
					// if (edge.getWeight() < minimumEdgeWeight)
					// continue;
					int neighbourClass;
					T otherNode = graph.getOpposite(node, edge);
					neighbourClass = classes.get(otherNode);
					double impact = 0;
					switch (propagation) {
					case TOP:
					case VOTE:
						impact = 1;
						// edge.getWeight();
						break;
					case DIST:
						impact = 1 / graph.degree(otherNode);
						// edge.getWeight() / graph.getDegree(otherNode);
						break;
					case DIST_LOG:
						impact = 1 / Math.log(1 + graph.degree(otherNode));
						// edge.getWeight() / Math.log(1 +
						// graph.getDegree(otherNode));
						break;
					}
					if (rankedClasses.get(neighbourClass) != null)
						rankedClasses.put(neighbourClass, rankedClasses.get(neighbourClass) + impact);
					else
						rankedClasses.put(neighbourClass, impact);
				}

				double sum = 0;
				int highestRankedClass = classes.get(node);
				double highestWeight = Double.MIN_VALUE;
				for (Map.Entry<Integer, Double> entry : rankedClasses.entrySet()) {
					sum += entry.getValue();
					if (entry.getValue() > highestWeight) {
						highestWeight = entry.getValue();
						highestRankedClass = entry.getKey();
					}
				}

				if (propagation != Propagation.VOTE || highestWeight / sum > propagationVoteValue) {
					// sample mutation: (TODO: autoincrement new class)
					// if (Math.random()<0.1-i/iterations/20) highestRankedClass
					// = (int)Math.round(Math.random()*100000));
					if (stepwiseUpdate) {
						classesBuffer.put(node, highestRankedClass);
					} else {
						classes.put(node, highestRankedClass);
					}
				}
			}
			if (stepwiseUpdate) {
				classes = classesBuffer;
			}
		}

		Map<Integer, List<T>> clusters = new HashMap<>();
		for (Map.Entry<T, Integer> nodesCluster : classes.entrySet()) {
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

	public int getIterations() {
		return iterations;
	}

	public void setIterations(int iterations) {
		this.iterations = Math.max(1, iterations);
	}

	public boolean getRandomColoring() {
		return randomColoring;
	}

	public void setRandomColoring(boolean randomColoring) {
		this.randomColoring = randomColoring;
	}

	public Propagation getPropagation() {
		return propagation;
	}

	public void setPropagation(Propagation propagation) {
		this.propagation = propagation;
	}

	public double getPropagationVoteValue() {
		return propagationVoteValue;
	}

	public void setPropagationVoteValue(double propagationVoteValue) {
		this.propagationVoteValue = propagationVoteValue;
	}

	public boolean getStepwiseUpdate() {
		return stepwiseUpdate;
	}

	public void setStepwiseUpdate(boolean stepwiseUpdate) {
		this.stepwiseUpdate = stepwiseUpdate;
	}

	public double getMinimumEdgeWeight() {
		return minimumEdgeWeight;
	}

	public void setMinimumEdgeWeight(double minimumEdgeWeight) {
		this.minimumEdgeWeight = minimumEdgeWeight;
	}

	public Unconnected getUnconnected() {
		return unconnected;
	}

	public void setUnconnected(Unconnected unconnected) {
		this.unconnected = unconnected;
	}

	public boolean getRandomizedNodeOrder() {
		return randomizedNodeOrder;
	}

	public void setRandomizedNodeOrder(boolean randomizedNodeOrder) {
		this.randomizedNodeOrder = randomizedNodeOrder;
	}
}
