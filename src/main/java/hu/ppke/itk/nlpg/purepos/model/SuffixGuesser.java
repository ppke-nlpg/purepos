package hu.ppke.itk.nlpg.purepos.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Map;
import java.util.Map.Entry;

public abstract class SuffixGuesser<W, T> implements ISuffixGuesser<W, T>,
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -39955036105607248L;

	public static <T> T getMaxProbabilityTag(Map<T, Double> probabilities) {
		// if (probabilities == null || probabilities.size() == 0)
		// return null;
		// Set<Map.Entry<T, Double>> entries = probabilities.entrySet();
		// Iterator<Entry<T, Double>> it = entries.iterator();
		// Map.Entry<T, Double> e = it.next();
		// T maxTag = e.getKey();
		// Double maxValue = e.getValue();
		// while (it.hasNext()) {
		// e = it.next();
		// if (e.getValue() > maxValue) {
		// maxTag = e.getKey();
		// maxValue = e.getValue();
		// }
		// }
		// return maxTag;
		Entry<T, Double> max = Collections.max(probabilities.entrySet(),
				new Comparator<Map.Entry<T, Double>>() {

					@Override
					public int compare(Entry<T, Double> o1, Entry<T, Double> o2) {
						return Double.compare(o1.getValue(), o2.getValue());
					}
				});

		return max.getKey();
	}
}
