package kyiv.rvysh.vkfriends.graphalgorithms.topleaders.global;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.data.Partitioning;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.AdjacencyPearsonCorrelation;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.AdjacencyRelation;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.ClusteringDegree;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.Degree;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.GraphCentralityBasedMedoid;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.GraphVertexScorer;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.ICloseness;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.NeighborOverlapOld;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.ShortestPath;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.base.Proximity;

/**
 * An approach for community mining in social networks. This Class detects given
 * number (k) of communities in a given Network. The algorithm is based on
 * k-means clustering method. It consists of these two main steps calculate
 * centers of k clusters based on a centrality measure determine clusters based
 * on current k centers using iCloseness closeness measure
 * 
 */
public class GlobalTopLeaders<V, E> implements Transformer<Graph<V, E>, Partitioning<V>> {
	protected Random rand;
	// TODO: there might not be k centers based on clustering method, caused
	// null pointer, change every numofclusters to number of centers, or after
	// init change it
	private Graph<V, E> graph;
	// private Initialization<V, E> initialization;
	Transformer<E, ? extends Number> weights;
	private HashSet<V> nodesToConsider;
	private HashSet<V> centerNodes;
	boolean checkSecond = true;
	private int iterated = 0;
	private int maxIteration = 10;
	private double outlierThereshod = 0, hubThreshold = 0, centersClossenessThreshold = 4;
	// private double MaxWeight = 0;

	private int numClusters;

	public int distanceMeasureType = 0;
	public final int ICLOSENESS = 0, SP = 1, ADR = 2, NOD = 3, PCD = 4;

	public void setDistanceMeasureType(int distanceMeasureType) {
		this.distanceMeasureType = distanceMeasureType;
	}

	private ArrayList<V> centers;
	int neighborhoodThreshold = 2;// 3;
	HashMap<V, Set<V>> neighborhoods;
	Proximity<V> distanceMeasure;
	private Partitioning<V> clustring;

	/**
	 * @param numClusters
	 *            : the number of clusters to be detected in the graph
	 * @param initMethod
	 *            : initialization method for choosing initial centers,
	 *            "k_central_common_neighbor" is recommended from [random,
	 *            k_centrals, ck_central_random, roulette_wheel,
	 *            k_central_one_neighbor_farther, [k_central_common_neighbor]]
	 * @param neighborhoodThreshold
	 *            : how far from each node we should search for the center (in
	 *            terms of SP depth)
	 * @param sourceNeighborhoodThreshold
	 *            : how far from a center we should consider the nodes
	 * @param outlierThereshod
	 *            : how close should be a node to a center to be considered
	 *            close, otherwise it would be marked as an outlier
	 * @param algorithm
	 *            : use basic k-means or k-means refine, default is fine
	 * @param centralityMethod
	 *            : the centrality method to be used: recommended "degree" from
	 *            [ distance, [degree], betweenness]
	 * @param maxIteration
	 *            : maximum number of iteration for the algorithm
	 * @param centersClossenessThreshold
	 *            : preferred farness of the initial centers, just used in
	 *            "k_central_common_neighbor" initialization method
	 * @param initBuffer
	 *            : the buffer size used in the initialization, it could be any
	 *            number greater than 1
	 */
	public GlobalTopLeaders(int numClusters, double outlierThereshod, double hubThreshold,
			double centersClossenessThreshold) {
		this.hubThreshold = hubThreshold;
		this.outlierThereshod = outlierThereshod;
		this.numClusters = numClusters;
		this.centersClossenessThreshold = centersClossenessThreshold;
	}

	public Partitioning<V> transform(Graph<V, E> graph, Transformer<E, ? extends Number> weight) {
		this.weights = weight;
		return transform(graph);
	}

	public double getCloseness(V v1, V v2) { // TODO: check this i changed the
												// distance
		double closeness = distanceMeasure.getProximity(v1, v2).doubleValue();
		if (!(distanceMeasure instanceof ICloseness<?, ?>)) {
			if (distanceMeasure instanceof NeighborOverlapOld<?, ?>
					|| distanceMeasure instanceof AdjacencyPearsonCorrelation<?, ?>)
				closeness = 1 - closeness;
			else
				closeness = 1 / closeness;
		}
		return closeness;
	}

	// K-means algorithm for finding clusters in in the given graph
	public Partitioning<V> transform(Graph<V, E> graph) {
		this.graph = graph;
		nodesToConsider = new HashSet<V>();
		if (numClusters < 0 || numClusters > graph.getVertexCount()) {
			System.err.println(numClusters);
			throw new IllegalArgumentException("Invalid number of clusters passed in.");
		}

		switch (distanceMeasureType) {
		case ICLOSENESS:
			distanceMeasure = new ICloseness<V, E>(graph, weights);
			break;
		case SP:
			distanceMeasure = new ShortestPath<V, E>(graph, weights);
			break;
		case ADR:
			distanceMeasure = new AdjacencyRelation<V, E>(graph, weights);
			break;
		case NOD:
			distanceMeasure = new NeighborOverlapOld<V, E>(graph, weights);
			break;
		case PCD:
			distanceMeasure = new AdjacencyPearsonCorrelation<V, E>(graph, weights);
			break;
		}
		centers = initializeCenters(numClusters, graph);
		centerNodes = new HashSet<V>();
		updateNodeSet();
		int itr = 0;
		boolean changed = true;
		iterated = 0;
		while (itr++ < maxIteration && changed) {
			changed = false;
			for (V c : centers) {
				centerNodes.add(c);
			}
			changed |= findClusters();
			iterated++;
			changed |= findCenters();
			updateNodeSet();
		}
		return clustring;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Set<V> getNeighbors(V n) {
		// iCloseness contains the neighbourhood
		if (distanceMeasure instanceof ICloseness<?, ?>) {
			return ((ICloseness) (distanceMeasure)).getNeighbors(n).keySet();
		} else {
			if (neighborhoods == null) {
				neighborhoods = new HashMap<V, Set<V>>();
			}
			if (neighborhoods.containsKey(n))
				return neighborhoods.get(n);

			HashSet<V> neighbors = new HashSet<V>();

			// length zero neighborhood
			neighbors.add(n);

			// length one to threshold neighborhoods
			for (int i = 1; i <= neighborhoodThreshold; i++) {
				Set<V> expanded_neighbours = new HashSet<V>(neighbors);
				for (V v : neighbors) {
					expanded_neighbours.addAll(graph.getNeighbors(v));
				}
				neighbors.addAll(expanded_neighbours);
			}

			neighborhoods.put(n, neighbors);
			return neighbors;
		}
	}

	// returns the ID of the cluster that this node should be added to
	private Vector<V> findCluster(V v) {
		Vector<V> candidates = new Vector<V>();

		// Find candidate centers
		for (V n1 : getNeighbors(v)) {
			for (V n2 : getNeighbors(n1)) {
				if (!candidates.contains(n2) && centerNodes.contains(n2))
					candidates.add(n2);
			}
		}

		// Find the most probable leader
		Vector<V> ties = new Vector<V>();
		double closenest = -1, closeness;

		for (int i = 0; i < candidates.size(); i++) {
			closeness = getCloseness(v, candidates.get(i));
			if (closeness >= outlierThereshod) {
				if (Math.abs(closeness - closenest) <= hubThreshold) {// tie
					ties.add(candidates.get(i));
					// System.err.println(node+ " : " +ties);
				} else if (closeness > closenest) {
					ties.clear();
					ties.add(candidates.get(i));
					closenest = closeness;
				}
			}
		}
		return ties;
	}

	private boolean findClusters() {
		boolean changed = true;

		Partitioning<V> new_clustring = new Partitioning<V>();
		for (int i = 0; i < centers.size(); i++) {
			new_clustring.addCluster(new HashSet<V>());
			new_clustring.getCommunities().lastElement().add(centers.get(i));
		}
		for (V v : nodesToConsider)
			if (!centerNodes.contains(v)) {
				// Find the most probable cluster for node V
				Vector<V> tmp = findCluster(v);

				if (tmp == null || tmp.size() == 0) {
					if (graph.getNeighborCount(v) > 5) {
						new_clustring.addHub(v);// Powerful but free nodes
					} else
						new_clustring.addOutlier(v);
				} else if (tmp.size() == 1)
					new_clustring.getCommunities().elementAt(centers.indexOf(tmp.firstElement())).add(v);
				else
					new_clustring.addHub(v);

			}
		if (clustring != null && new_clustring.equals(clustring))
			changed = false;

		clustring = new_clustring;

		return changed;
	}

	private V centerOfCluster(Set<V> clusterNodes) {
		GraphCentralityBasedMedoid<V, E> centrality = new GraphCentralityBasedMedoid<V, E>(new Degree<V, E>(),
				graph);
		return centrality.findCentroid(clusterNodes);
	}

	private boolean findCenters() {
		boolean changed = true;

		for (V c : centers) {
			centerNodes.remove(c);
		}

		ArrayList<V> new_centers = new ArrayList<V>();
		for (Set<V> cluster : clustring.getCommunities()) {
			new_centers.add(centerOfCluster(cluster));
		}

		if (new HashSet<V>(new_centers).equals(new HashSet<V>(centers)))
			changed = false;

		centers = new_centers;

		return changed;
	}

	private void updateNodeSet() {
		nodesToConsider.clear();
		for (V center : centers) {
			for (V v : getNeighbors(center)) {
				nodesToConsider.addAll(getNeighbors(v));
			}
		}
	}

	public int getIterated() {
		return iterated;
	}

	ArrayList<V> hardCodedInitialCenters;

	public void setHardCodedInitialCenters(ArrayList<V> centers) {

		hardCodedInitialCenters = centers;
	}

	public ArrayList<V> initializeCenters(int numberOfCenters, Graph<V, E> graph) {
		ArrayList<V> centers = new ArrayList<V>();
		if (hardCodedInitialCenters != null) {
			for (V v : hardCodedInitialCenters) {
				V mapped = null;
				for (V v2 : graph.getVertices()) {
					if (v2.equals(v))
						mapped = v2;
				}
				if (mapped != null)
					centers.add(mapped);
				else
					System.err.println("Invalid inital center");
			}
			return centers;
		}
		int maxCandidates = 1;
		ArrayList<ArrayList<V>> candidateCenters = new ArrayList<ArrayList<V>>();
		ArrayList<ArrayList<V>> remainingOfCandidateCenters = new ArrayList<ArrayList<V>>();
		final GraphVertexScorer<V, E> centrality = new ClusteringDegree<V, E>();
		centrality.setGraph(graph);
		ArrayList<V> ver = new ArrayList<V>(graph.getVertices());

		Collections.sort(ver, new Comparator<V>() {
			public int compare(V o1, V o2) {
				return (int) (centrality.getVertexScore(o2) - centrality.getVertexScore(o1));
			}
		});
		boolean found = false;
		while (!found) {
			if (ver.size() == 0)
				break;
			V result = ver.get(0);
			ver.remove(result);
			if (candidateCenters.size() == 0) {
				candidateCenters.add(new ArrayList<V>());
				remainingOfCandidateCenters.add(new ArrayList<V>());
				candidateCenters.get(candidateCenters.size() - 1).add(result);
				continue;
			}

			Vector<ArrayList<V>> probs = new Vector<ArrayList<V>>();

			for (ArrayList<V> cc : candidateCenters) {
				probs.add(new ArrayList<V>());
				for (V v : cc) {
					double tmp = getCloseness(v, result);
					if (tmp >= centersClossenessThreshold) {
						probs.lastElement().add(v);
					}
				}
			}
			ArrayList<ArrayList<V>> newCandidateCenters = new ArrayList<ArrayList<V>>();
			ArrayList<ArrayList<V>> newRemainingOfCandidateCenters = new ArrayList<ArrayList<V>>();
			for (int i = 0; i < candidateCenters.size(); i++) {
				if (probs.get(i).size() == 0) {
					newCandidateCenters.add(new ArrayList<V>(candidateCenters.get(i)));
					newCandidateCenters.get(i).add(result);
					newRemainingOfCandidateCenters.add(new ArrayList<V>(remainingOfCandidateCenters.get(i)));
					if (newCandidateCenters.get(newCandidateCenters.size() - 1).size() == numberOfCenters) {
						found = true;
						centers = newCandidateCenters.get(newCandidateCenters.size() - 1);
					}
				} else {
					newCandidateCenters.add(new ArrayList<V>(candidateCenters.get(i)));
					newRemainingOfCandidateCenters.add(new ArrayList<V>(remainingOfCandidateCenters.get(i)));
					newRemainingOfCandidateCenters.get(newRemainingOfCandidateCenters.size() - 1).add(result);

					if (newCandidateCenters.get(newCandidateCenters.size() - 1).size() == numberOfCenters) {
						found = true;
						centers = newCandidateCenters.get(newCandidateCenters.size() - 1);
					}
				}
			}
			for (int i = 0; i < candidateCenters.size(); i++) {
				if (probs.get(i).size() != 0) {
					newCandidateCenters.add(new ArrayList<V>(candidateCenters.get(i)));
					newCandidateCenters.get(newCandidateCenters.size() - 1).add(result);
					newRemainingOfCandidateCenters.add(new ArrayList<V>(remainingOfCandidateCenters.get(i)));
					for (V prob : probs.get(i)) {
						newCandidateCenters.get(newCandidateCenters.size() - 1).remove(prob);
						newRemainingOfCandidateCenters.get(newRemainingOfCandidateCenters.size() - 1)
								.add(prob);
					}

					if (newCandidateCenters.get(newCandidateCenters.size() - 1).size() == numberOfCenters) {
						found = true;
						centers = newCandidateCenters.get(newCandidateCenters.size() - 1);
					}
				}
			}
			if (newCandidateCenters.size() > maxCandidates) {
				candidateCenters = new ArrayList<ArrayList<V>>(newCandidateCenters.subList(0, maxCandidates));
				remainingOfCandidateCenters = new ArrayList<ArrayList<V>>(
						newRemainingOfCandidateCenters.subList(0, maxCandidates));
			} else {
				candidateCenters = newCandidateCenters;
				remainingOfCandidateCenters = newRemainingOfCandidateCenters;
			}
		}
		while (!found) {
			for (int i = 0; i < candidateCenters.size(); i++) {
				if (remainingOfCandidateCenters.get(i).size() > 0) {
					candidateCenters.get(i).add(remainingOfCandidateCenters.get(i).get(0));
				} else
					found = true;
				if (candidateCenters.get(i).size() == numberOfCenters) {
					centers = candidateCenters.get(i);
					found = true;
					break;
				}
			}

		}
		return centers;
	}

}
