package kyiv.rvysh.vkfriends.graphalgorithms.markov;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.uci.ics.jung.graph.Graph;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jung.JungTransform;

/**
 *
 * @author JiriKrizek
 */
public abstract class MarkovClusterer<T> {
	static final double EPSILON = 0.00000001;
	// Algorithm settings
	private double power = 2L;
	private double inflation = 2L;
	private double prune = 0.02;
	private boolean selfLoop = true;
	private MclAlgorithm<T> mclComputation;
	private NodesMatrix matrix;
	private boolean extraClusters = false;
	private NodesMap<T> mapping;

	public Collection<Set<T>> clusterize(Neo4jGraph<T> graph) {
		Graph<T, Neo4jEdge> g = JungTransform.neo4jToJungGraph(graph);
		if (inflation < 0) {
			throw new IllegalStateException("inflation parameter must be >= 0");
		}
		// Preparing column for clusters data
		int N = g.getVertexCount();
		matrix = new PrintableDenseMatrix64F(N, N);
		matrix.setEpsilon(EPSILON);
		mapping = new NodesMap<>(g);
		// Create an associated matrix
		for (Neo4jEdge edge : g.getEdges()) {
			T sourceNode = g.getEndpoints(edge).getFirst();
			T dstNode = g.getEndpoints(edge).getSecond();
			int src = mapping.getSequentialIdFor(sourceNode);
			int dst = mapping.getSequentialIdFor(dstNode);
			matrix.set(src, dst, 1);
			matrix.set(dst, src, 1);
		}
		MclParameters mclParam = new MclParameters(selfLoop, inflation, prune, power, N);
		mclComputation = new MclAlgorithm<T>(matrix, mclParam);
		matrix = mclComputation.execute();
		//
		// MCL algorithm beginning
		// 1. Input is an un-directed graph, power parameter e, inflation
		// parameter r and boolean add self loops
		// 2. Create the associated matrix
		// 3. Add self loops to each node (optional)
		// 4. Normalize the matrix
		// for
		// 5. Expand
		// 6. Inflate by taking inflation of the resulting matrix with parameter
		// r
		// endfor
		// 7. Repeat steps 5 and 6 until a steady state is reached (convergence)
		// 8. Interpret resulting matrix to discover clusters
		// MCL algorithm end

		// resize matrix to have only rows that contain some non zero value
		// (number of rows equals number of clusters)
		matrix.removeZeroRows();

		// save to gephi attributes
		return getResults(mapping);
	}

	private Collection<Set<T>> getResults(NodesMap<T> mapping) {
		int numberOfClusters = matrix.getNumRows();
		Map<Integer, Set<T>> resMap = new HashMap<>(numberOfClusters);
		List<Set<T>> result = new ArrayList<>();

		// read by columns down then right
		for (int nodeId = 0; nodeId < matrix.getNumCols(); nodeId++) {
			// for every column, which clusters node belongs to
			// Node currentNode = mapping.getNodeForId(nodeId);
			T mcCurrentNode = mapping.getNodeForId(nodeId);

			for (int cluster = 0; cluster < matrix.getNumRows(); cluster++) {
				// Initialize cluster
				if ((!resMap.containsKey(cluster))) {
					resMap.put(cluster, new HashSet<>());
				}

				double clusterClassification = matrix.get(cluster, nodeId);
				// clusterClassification not zero or really close to zero - has
				// some cluster classification
				if (!precisionEqual(clusterClassification, 0)) {
					Set<T> clust = resMap.get(cluster);
					clust.add(mcCurrentNode);
					resMap.put(cluster, clust);
				}
			}
		}

		for (Map.Entry<Integer, Set<T>> entry : resMap.entrySet()) {
			result.add(entry.getValue());
		}
		return result;
	}

	/**
	 * @return the power
	 */
	public double getPower() {
		return power;
	}

	/**
	 * @param power
	 *            the power to set
	 */
	public void setPower(double power) {
		this.power = power;
	}

	/**
	 * @return the inflation
	 */
	public double getInflation() {
		return inflation;
	}

	/**
	 * @param inflation
	 *            the inflation to set
	 */
	public void setInflation(double inflation) {
		this.inflation = inflation;
	}

	/**
	 * @return the prune
	 */
	public double getPrune() {
		return prune;
	}

	/**
	 * @param prune
	 *            the prune to set
	 */
	public void setPrune(double prune) {
		this.prune = prune;
	}

	/**
	 * @return the isSelfLoop
	 */
	public boolean isSelfLoop() {
		return selfLoop;
	}

	/**
	 * @param isSelfLoop
	 *            the isSelfLoop to set
	 */
	public void setSelfLoop(boolean selfLoop) {
		this.selfLoop = selfLoop;
	}

	public static boolean precisionEqualZero(double a) {
		return MarkovClusterer.precisionEqual(a, 0);
	}

	public static boolean precisionEqual(double a, double b) {
		return Math.abs(a - b) <= ((Math.abs(a) > Math.abs(b) ? Math.abs(b) : Math.abs(a)) * EPSILON);
	}

	/**
	 * @return the extraClusters
	 */
	public boolean isExtraClusters() {
		return extraClusters;
	}

	/**
	 * @param extraClusters
	 *            the extraClusters to set
	 */
	public void setExtraClusters(boolean extraClusters) {
		this.extraClusters = extraClusters;
	}
}
