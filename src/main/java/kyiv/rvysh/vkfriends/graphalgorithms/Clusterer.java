package kyiv.rvysh.vkfriends.graphalgorithms;

import java.util.Collection;
import java.util.Set;

import org.ejml.data.Matrix;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;

public abstract class Clusterer<T> {
	protected Matrix results;

	public Clusterer() {
	}

	public abstract Collection<Set<T>> clusterize(Neo4jGraph<T> g);
	
	public Matrix getResults() {
		return results;
	}

}
