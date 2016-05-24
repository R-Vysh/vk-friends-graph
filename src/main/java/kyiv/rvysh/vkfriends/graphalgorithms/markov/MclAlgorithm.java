package kyiv.rvysh.vkfriends.graphalgorithms.markov;

/**
 *
 * @author Jiri Krizek
 */
class MclAlgorithm<T> {
	private NodesMatrix matrix;
	private final MclParameters mclParam;

	MclAlgorithm(NodesMatrix matrix, MclParameters mclParam) {
		this.matrix = matrix;
		this.mclParam = mclParam;
	}

	NodesMatrix execute() {
		// Add self loops to each node (optional)
		if (mclParam.isSelfLoop()) {
			addSelfLoops();
		}

		normalizeMatrix();

		NodesMatrix prev;
		while (true) {
			prev = matrix.copyMatrix();
			expand();

			// Inflate by taking inflation of the resulting matrix with
			// parameter r
			inflate();
			if (matricesEqual(matrix, prev)) {
				// matrix does not change values anymore, breaking from loop
				break;
			}
		}

		return matrix;
	}

	private void addSelfLoops() {
		if (matrix == null) {
			throw new IllegalStateException("matrix is null");
		}

		for (int i = 0; i < mclParam.getN(); i++) {
			matrix.set(i, i, 1);
		}
	}

	private void normalizeMatrix() {
		NodesMatrix sumCols = matrix.sumCols();
		for (int col = 0; col < mclParam.getN(); col++) {
			double sumCols_n = sumCols.get(col);
			for (int row = 0; row < mclParam.getN(); row++) {
				double val = matrix.get(row, col);
				matrix.set(row, col, val / sumCols_n);
			}
		}
	}

	private void expand() {
		for (int i = 1; i < mclParam.getPower(); i++) {
			NodesMatrix input = matrix.copyMatrix();
			matrix = matrix.multiply(matrix, input);
		}
	}

	private void inflate() {
		int N = mclParam.getN();
		// elements of matrix powered to inflation-param M_ij^inflation
		NodesMatrix poweredMatrix = matrix.copyMatrix();
		for (int i = 0; i < poweredMatrix.getNumElements(); i++) {
			double pow = poweredMatrix.get(i);
			double res = Math.pow(pow, mclParam.getInflation()); // M_pq ^ r
			poweredMatrix.set(i, res);
		}
		NodesMatrix sumCols = poweredMatrix.sumCols();

		for (int col = 0; col < N; col++) {
			double divisor = sumCols.get(col);
			for (int row = 0; row < N; row++) {
				double powElement = poweredMatrix.get(row, col);
				double res = powElement / divisor;

				// exact pruning
				if (res <= mclParam.getPrune()) {
					res = 0;
				}
				matrix.set(row, col, res);
			}
		}
		if (mclParam.getPrune() > 0) {
			normalizeMatrix();
		}
	}

	private boolean matricesEqual(NodesMatrix matrix, NodesMatrix prev) {
		int N = mclParam.getN();

		if (matrix.getNumCols() != N || matrix.getNumRows() != N || prev.getNumCols() != N
				|| prev.getNumRows() != N) {
			throw new IllegalArgumentException(
					"Matrices must be square and equal to number of nodes in graph");
		}
		for (int i = 0; i < matrix.getNumElements(); i++) {
			double val1 = matrix.get(i);
			double val2 = prev.get(i);

			if (!MarkovClusterer.precisionEqual(val1, val2)) {
				return false;
			}
		}
		return true;
	}

}
