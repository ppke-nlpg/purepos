package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Suffix guesser implementation for String suffixes with a HashTable
 * representation. This representation may save some memory space but needs a
 * bit more time to calculate the probability for a word and a tag.
 * 
 * @author György Orosz
 * 
 * @param <T>
 */
// TODO: is it worth to create an implementation which recalculates the
// probabilities for all the suffixes?
public class HashSuffixGuesser<T> extends SuffixGuesser<String, T> {
	private final HashMap<String, MutablePair<HashMap<T, Integer>, Integer>> freqTable;
	private final double theta;
	private final double thetaPlusOne;

	HashSuffixGuesser(
			HashMap<String, MutablePair<HashMap<T, Integer>, Integer>> freqTable,
			double theta) {
		// TODO: interesting why Brants and Halácsy assumes that the shortest
		// suffix has the greatest power instead of the longest suffix. It would
		// be good to investigate the other case.
		this.freqTable = freqTable;
		this.theta = theta;
		this.thetaPlusOne = theta + 1;
	}

	@Override
	public double getTagProbability(String word, T tag) {
		// TODO: are you sure to calculate with the empty suffix as well?
		// (Brants does this, but how about Halácsy?)
		return getTagProb(word, word.length(), tag);
	}

	/**
	 * Calculates the probability for a given suffix and tag.
	 * 
	 * @param word
	 *            the word which has the suffix
	 * @param index
	 *            end position of the suffix (usually the length of the suffix)
	 * @param tag
	 *            POS tag
	 * @return
	 */
	protected double getTagProb(String word, int index, T tag) {
		if (index >= 0 && freqTable.containsKey(word.substring(index))) {
			String suffix = word.substring(index);
			// TODO: how does automatic conversation happen? (should be float!)
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suffix);
			Integer tagSufFreq = suffixValue.getLeft().get(tag);
			Double tagSufFreqD;
			if (tagSufFreq == null)
				tagSufFreqD = 0.0;
			else
				tagSufFreqD = tagSufFreq.doubleValue();
			Double relFreq = tagSufFreqD / suffixValue.getRight();
			return (relFreq + theta * getTagProb(word, index - 1, tag))
					/ thetaPlusOne;
		} else
			return 0;
	}

	@Override
	public Map<T, Double> getTagProbabilities(String word) {
		HashMap<T, Double> ret = new HashMap<T, Double>();
		Set<T> tags = freqTable.get("").getLeft().keySet();
		for (T tag : tags) {
			ret.put(tag, getTagProbability(word, tag));
		}
		return ret;
	}

	@Override
	public T getMaxProbabilityTag(String word) {
		return getMaxProbabilityTag(getTagProbabilities(word));
	}

}
