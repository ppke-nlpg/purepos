package hu.ppke.itk.nlpg.purepos.model;

import java.util.Map;

//TODO: test
public abstract class SuffixGuesser<W, T> implements ISuffixGuesser<W, T> {
	public static <T> T getMaxProbabilityTag(Map<T, Double> probabilities) {
		if (probabilities == null)
			return null;
		T maxTag = null;
		Double maxValue = Double.MIN_VALUE;
		for (Map.Entry<T, Double> entry : probabilities.entrySet()) {
			if (entry.getValue() > maxValue) {
				maxValue = entry.getValue();
				maxTag = entry.getKey();
			}
		}
		return maxTag;
	}

}
