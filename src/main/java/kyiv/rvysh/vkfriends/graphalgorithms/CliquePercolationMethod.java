package kyiv.rvysh.vkfriends.graphalgorithms;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.jgrapht.Graph;
import org.jgrapht.alg.BronKerboschCliqueFinder;

import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jGraph;
import kyiv.rvysh.vkfriends.jgrapht.JgraphtTransform;

public abstract class CliquePercolationMethod<T> {
	private int k;
	private Collection<Set<T>> cliques = new ArrayList<Set<T>>();

	protected abstract Comparator<? super T> getComparator();

	protected abstract List<T> getLargerNodes(Graph<T, Neo4jEdge> g, T vi);

	public Collection<Set<T>> clusterize(Neo4jGraph<T> g) {
		return execute(JgraphtTransform.neo4jToJgraphtGraph(g));
	}

	private Collection<Set<T>> execute(Graph<T, Neo4jEdge> g) {
		BronKerboschCliqueFinder<T, Neo4jEdge> kf = new BronKerboschCliqueFinder<>(g);
		cliques = kf.getAllMaximalCliques();
		filterCliques();
		mergeCliques();
		return cliques;
	}

	private void filterCliques() {
		List<Set<T>> filteredCliques = new ArrayList<>();
		for (Set<T> clique : cliques) {
			if (clique.size() >= k) {
				filteredCliques.add(clique);
			}
		}
		cliques = filteredCliques;
	}

	private void mergeCliques() {
		boolean canMerge = true;
		while (canMerge) {
			canMerge = false;
			Set<T> cliqueToMerge = null;
			for (Set<T> firstClique : cliques) {
				for (Set<T> secondClique : cliques) {
					if (getSharedNodes(firstClique, secondClique) >= k - 1) {
						cliqueToMerge = secondClique;
						break;
					}
				}
				if (cliqueToMerge != null) {
					canMerge = true;
					firstClique.addAll(cliqueToMerge);
					break;
				}
			}
			cliques.remove(cliqueToMerge);
		}
	}

	private int getSharedNodes(Set<T> firstClique, Set<T> secondClique) {
		if (firstClique == secondClique)
			return 0;
		int res = 0;
		for (T el1 : firstClique) {
			for (T el2 : secondClique) {
				if (el1.equals(el2)) {
					res++;
				}
			}
		}
		return res;
	}

	public void setK(int k) {
		this.k = k;
	}
}
