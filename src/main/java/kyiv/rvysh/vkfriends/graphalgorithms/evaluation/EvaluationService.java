package kyiv.rvysh.vkfriends.graphalgorithms.evaluation;

import javaMI.Entropy;
import javaMI.MutualInformation;

public class EvaluationService {
	public double evaluateNMI(double[] expected, double[] actual) {
		double mi = MutualInformation.calculateMutualInformation(expected, actual);
		double hx = Entropy.calculateEntropy(expected);
		double hy = Entropy.calculateEntropy(actual);
		double nmi = mi / (Math.sqrt(hx * hy));
		return nmi;
	}
}
