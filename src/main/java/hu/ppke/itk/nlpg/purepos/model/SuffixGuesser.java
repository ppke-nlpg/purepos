package hu.ppke.itk.nlpg.purepos.model;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public abstract class SuffixGuesser<W, T> implements ISuffixGuesser<W, T> {
	public static <T> T getMaxProbabilityTag(Map<T, Double> probabilities) {
		if (probabilities == null || probabilities.size() == 0)
			return null;
		Set<Map.Entry<T, Double>> entries = probabilities.entrySet();
		Iterator<Entry<T, Double>> it = entries.iterator();
		Map.Entry<T, Double> e = it.next();
		T maxTag = e.getKey();
		Double maxValue = e.getValue();
		while (it.hasNext()) {
			e = it.next();
			if (e.getValue() > maxValue) {
				maxTag = e.getKey();
				maxValue = e.getValue();
			}
		}
		return maxTag;
	}
}
