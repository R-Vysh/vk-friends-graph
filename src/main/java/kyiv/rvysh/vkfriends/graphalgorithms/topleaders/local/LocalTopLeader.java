package kyiv.rvysh.vkfriends.graphalgorithms.topleaders.local;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.data.Partitioning;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.ClusteringDegree;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.GraphVertexScorer;
import kyiv.rvysh.vkfriends.graphalgorithms.topleaders.measure.ICloseness;

public class LocalTopLeader<V, E> implements Transformer<Graph<V, E>, Partitioning<V>> {
	Graph<V, E> graph;
	ArrayList<V> sortedVertices;
	Set<V> seeds;
	// HashMap<V, Vector<Set<V>>> neighborhoods;
	private double outlierThereshod = 0, hubThreshold = 0, potentialLeaderThreshold = 3, minCommunitySizeThreshold = 3,
			MAX_CLOSENESS = 10000;

	double centersClosenessThreshold = 300;
	int nodeNeighborhoodThreshold = 3;// , sourceNeighborhoodThreshold = 2;
	Vector<V> powerfullButFree;
	HashMap<V, HashMap<Community, Double>> memberships;

	ICloseness<V, E> iCloseness;

	public LocalTopLeader() {
		super();
	}

	public LocalTopLeader(double outlierThereshod, double hubThreshold, double minCommunitySizeThreshold,
			double centersClosenessThreshold) {
		super();
		this.outlierThereshod = outlierThereshod;
		this.hubThreshold = hubThreshold;
		// this.potentialLeaderThreshold = potentialLeaderThreshold;
		this.minCommunitySizeThreshold = minCommunitySizeThreshold;
		this.centersClosenessThreshold = centersClosenessThreshold;
		// this.nodeNeighborhoodThreshold = nodeNeighborhoodThreshold;
		// this.sourceNeighborhoodThreshold = sourceNeighborhoodThreshold;
	}

	public double avgCluteringCoefficient(Graph<V, E> graph) {
		double cc = 0;
		for (V v : graph.getVertices()) {
			double cv = 0;
			for (V n1 : graph.getNeighbors(v)) {
				for (V n2 : graph.getNeighbors(v)) {
					if (n1 != n2 && graph.findEdge(n1, n2) != null)
						cv++;
				}
			}
			if (cv != 0)
				cv /= graph.degree(v) * (graph.degree(v) - 1);
			cc += cv;
		}
		cc /= graph.getVertexCount();
		// System.err.println(cc);
		return cc;
	}

	public double avgAgumentedCluteringCoefficient(Graph<V, E> graph) {
		double cc = 0;
		int outees = 0;

		for (V v : graph.getVertices()) {
			double cv = 0;

			for (V n1 : graph.getNeighbors(v)) {
				for (V n2 : graph.getNeighbors(v)) {
					if (n1 != n2 && graph.findEdge(n1, n2) != null)
						cv++;
				}
			}
			if (graph.getNeighborCount(v) * 100 <= outlierThereshod)
				outees++;
			else {
				if (cv != 0)
					cv /= graph.degree(v) * (graph.degree(v) - 1);
				cc += cv;
			}
		}
		System.err.println(outees);
		cc /= (graph.getVertexCount() - outees);
		return cc;
	}

	public double avgDegree(Graph<V, E> graph) {
		double res = 0;
		for (V v : graph.getVertices()) {
			res += graph.degree(v);
		}
		res /= graph.getVertexCount();
		return res;
	}

	public Partitioning<V> transform(Graph<V, E> graph) {
		this.graph = graph;

		double avgD = 0, varD = 0;
		for (V v : graph.getVertices()) {
			avgD += graph.degree(v);
			varD += graph.degree(v) * graph.degree(v);
		}
		avgD /= graph.getVertexCount();
		varD = Math.sqrt(varD / graph.getVertexCount() - avgD * avgD);

		potentialLeaderThreshold = avgD;// +varD;//+1; //+1 could be change to
										// var
		initialize();

		System.err.println("Running Local with [" + "mc: " + minCommunitySizeThreshold + ", pl: "
				+ potentialLeaderThreshold + ", ot: " + outlierThereshod + ", ht: " + hubThreshold + ", cc: "
				+ centersClosenessThreshold + ", nn: " + nodeNeighborhoodThreshold + " ] ");

		Partitioning<V> resultPartitioning = new Partitioning<V>();

		V seed = getNextSeed();
		while (seed != null) {
			Set<V> followers = findFollowersOf(seed);
			if (followers.size() >= minCommunitySizeThreshold) {
				resultPartitioning.addCluster(followers);

				// Free previous commitments
				for (V v : followers) {
					Set<Community> toBeFreed = new HashSet<Community>();
					HashMap<Community, Double> commitments = memberships.get(v);
					for (Community belongness : commitments.keySet()) {
						if (!belongness.contains(v)) {
							toBeFreed.add(belongness);
						}
					}
					for (Community belongness : toBeFreed) {
						commitments.remove(belongness);
					}
					if (commitments.size() > 1) {
						resultPartitioning.addHub(v);
					} else if (resultPartitioning.isHub(v)) {
						resultPartitioning.getHubs().remove(v);
					}
				}
			} else {
				seeds.remove(seed);
				Community selfBelongness = null;
				for (Community belongness : memberships.get(seed).keySet()) {
					if (memberships.get(seed).get(belongness) == MAX_CLOSENESS)
						selfBelongness = belongness;
				}
				// Return to your previous commitment cheaters
				for (V v : followers) {
					memberships.get(v).remove(selfBelongness);
					for (Community belongness : memberships.get(v).keySet()) {
						if (!belongness.contains(v))
							belongness.add(v);
					}
				}
				memberships.get(seed).remove(selfBelongness);
				if (memberships.get(seed).isEmpty())
					powerfullButFree.add(seed);

			}
			seed = getNextSeed();
		}

		for (V v : powerfullButFree) {
			// System.out.println(v+" "+memberships.get(v));
			if (memberships.get(v) == null || memberships.get(v).isEmpty())
				resultPartitioning.addHub(v);
		}
		for (V v : sortedVertices) {
			if (memberships.get(v) == null)
				resultPartitioning.addOutlier(v);
		}
		// Non-overlapping
		for (V v : resultPartitioning.getHubs()) {
			for (Set<V> cluster : resultPartitioning.getCommunities()) {
				if (cluster.contains(v))
					cluster.remove(v);
			}
		}
		return resultPartitioning;
	}

	public void initialize() {
		seeds = new HashSet<V>();
		sortedVertices = new ArrayList<V>(graph.getVertices());
		powerfullButFree = new Vector<V>();

		final GraphVertexScorer<V, E> centrality = new ClusteringDegree<V, E>();
		centrality.setGraph(graph);
		Collections.sort(sortedVertices, new Comparator<V>() {
			public int compare(V o1, V o2) {
				// System.err.println(o1 +"("+centrality.getVertexScore(o1)+")"
				// + (((int)(centrality.getVertexScore(o2) -
				// centrality.getVertexScore(o1)))>0?"<":">") + o2
				// +"("+centrality.getVertexScore(o2)+")" );
				return (int) (centrality.getVertexScore(o2) - centrality.getVertexScore(o1));
			}
		});
		iCloseness = new ICloseness<V, E>(nodeNeighborhoodThreshold, graph, null);
		memberships = new HashMap<V, HashMap<Community, Double>>();
	}

	// Returning next powerful leader
	public V getNextSeed() {
		V nextSeed = null;

		while (sortedVertices.size() > 0 && nextSeed == null) {
			nextSeed = sortedVertices.get(0);

			// System.err.println(graph.degree(nextSeed));
			if (graph.degree(nextSeed) < potentialLeaderThreshold)
				return null;
			sortedVertices.remove(0);

			powerfullButFree.add(nextSeed);
			for (V v : seeds) {
				if (iCloseness.getProximity(nextSeed, v).doubleValue() > centersClosenessThreshold) {
					nextSeed = null;
					break;
				}
			}

		}

		if (nextSeed != null) {
			seeds.add(nextSeed);
			powerfullButFree.remove(nextSeed);
		}

		return nextSeed;
	}

	public Set<V> findFollowersOf(V seed) {
		// Set<V> followers = new HashSet<V>();
		Community followers = new Community();

		Vector<V> candidFollowers = new Vector<V>();
		Set<V> inQueue = new HashSet<V>();

		followers.add(seed);
		inQueue.add(seed);
		candidFollowers.add(seed);

		// candidFollowers.addAll(graph.getNeighbors(seed));
		while (!candidFollowers.isEmpty()) {
			V cur = candidFollowers.get(0);

			candidFollowers.remove(0);

			if (branch(cur, seed, followers))
				for (V v : graph.getNeighbors(cur)) {
					if ((!inQueue.contains(v)) && (!seeds.contains(v))) {
						inQueue.add(v);
						candidFollowers.add(v);
					}
				}

		}
		return followers.getMembers();
	}

	private void selfMembership(V seed, Community followers) {
		// Seed should not belonging to any community
		if (memberships.get(seed) != null) {
			for (Community belongness : memberships.get(seed).keySet()) {
				belongness.remove(seed);
			}
		} else
			memberships.put(seed, new HashMap<Community, Double>());

		memberships.get(seed).put(followers, MAX_CLOSENESS);
	}

	private boolean branch(V cur, V seed, Community followers) {
		if (cur == seed) {
			selfMembership(seed, followers);
			return true;
		}
		double belong = iCloseness.getProximity(cur, seed).doubleValue();
		if (belong < outlierThereshod)
			return false;
		boolean add = false;
		if (memberships.containsKey(cur)) {
			HashMap<Community, Double> commitments = memberships.get(cur);
			for (Community belongness : commitments.keySet()) {
				if (Math.abs(belong - commitments.get(belongness)) <= hubThreshold) { // they
																						// are
																						// tied
					add = true;
				} else if (belong > commitments.get(belongness)) {
					add = true;
					belongness.remove(cur); // the other should be removed
				}
			}
		} else {
			memberships.put(cur, new HashMap<Community, Double>());
			add = true;
		}
		if (add) {
			memberships.get(cur).put(followers, belong);
			followers.add(cur);
		}

		return add;
	}

	private class Community {
		Set<V> nodes;

		public Community() {
			this.nodes = new HashSet<V>();
		}

		public Community(Set<V> nodes) {
			this.nodes = nodes;
		}

		public boolean contains(V v) {
			return nodes.contains(v);
		}

		public void add(V v) {
			nodes.add(v);
		}

		public void remove(V v) {
			nodes.remove(v);
		}

		@Override
		public String toString() {
			return nodes.toString() + "\n";
		}

		public Set<V> getMembers() {
			return nodes;
		}
	}
}
