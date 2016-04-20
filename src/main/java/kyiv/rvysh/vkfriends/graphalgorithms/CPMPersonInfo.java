package kyiv.rvysh.vkfriends.graphalgorithms;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.jgrapht.Graph;

import kyiv.rvysh.vkfriends.domain.PersonInfo;
import kyiv.rvysh.vkfriends.domain.graph.Neo4jEdge;

public class CPMPersonInfo extends CliquePercolationMethod<PersonInfo> {
	
	@Override
	protected Comparator<PersonInfo> getComparator() {
		return new SortByUid();
	}
	
	@Override
	protected List<PersonInfo> getLargerNodes(Graph<PersonInfo, Neo4jEdge> g, PersonInfo vi) {
		List<PersonInfo> output = new ArrayList<PersonInfo>();
        for (PersonInfo n : g.vertexSet()) {
            if (n.getUid() > vi.getUid() && (g.containsEdge(n, vi) || g.containsEdge(vi, n))) {
                output.add(n);
            }
        }
        return output;
	}

	private class SortByUid implements Comparator<PersonInfo> {
        @Override
		public int compare(PersonInfo o1, PersonInfo o2) {
			if (o1.getUid() > o2.getUid()) {
                return 1;
            } else {
                return -1;
            }
		}
    }
}
