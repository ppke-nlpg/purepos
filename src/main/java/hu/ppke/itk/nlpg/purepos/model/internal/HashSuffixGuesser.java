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
	@SuppressWarnings("unused")
	private final Map<T, Double> aprioriProbs;

	// protected String lastWord = "";
	// protected T lastTag;
	// protected double lastLogProb;

	// protected Logger logger = Logger.getLogger(this.getClass());

	HashSuffixGuesser(
			HashMap<String, MutablePair<HashMap<T, Integer>, Integer>> freqTable,
			Map<T, Double> aprioriProbs, double theta) {
		this.aprioriProbs = aprioriProbs;
		this.freqTable = freqTable;
		this.theta = theta;
		this.thetaPlusOne = theta + 1;
	}

	@Override
	public double getTagLogProbability(String word, T tag) {
		System.out.println(tag + "\t" + word);
		// if (word == lastWord && tag == lastTag) {
		// return lastLogProb;
		// } else {
		double logProb = Math.log(getTagProbability(word, tag));
		// lastWord = word;
		// lastTag = tag;
		// lastLogProb = logProb;
		return logProb;// - Math.log(aprioriProbs.get(tag));
		// }
	}

	@Override
	public double getTagProbability(String word, T tag) {
		// TODO: are you sure to calculate with the empty suffix as well?
		// (Brants does this, but how about Halácsy?)
		// return getTagProbTnT(word, word.length(), tag);
		return getTagProbHunPOS(word, tag);
		// return getTagProbRevHunPOS(word, tag);
	}

	protected double getTagProbHunPOS(String word, T tag) {
		Double ret = 0.0;
		for (int i = word.length(); i >= 0; --i) {
			String suff = word.substring(i);
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suff);
			if (suffixValue != null) {
				Integer tagSufFreq = suffixValue.getLeft().get(tag);
				Double relFreq = 0.0;
				if (tagSufFreq != null) {
					Double tagSufFreqD = tagSufFreq.doubleValue();
					relFreq = tagSufFreqD / suffixValue.getRight();

					ret = (ret + (relFreq * theta)) / thetaPlusOne;
					// logger.debug("accu(" + tag + ") = (prev(" + retP
					// + ") + relfreq(" + relFreq + ") * theta(" + theta
					// + "))/thetaPO(" + thetaPlusOne + ") =" + ret);
				}
			}
		}
		return ret;
	}

	@Deprecated
	protected double getTagProbRevHunPOS(String word, T tag) {
		Double ret = 0.0;
		for (int i = 0; i <= word.length(); ++i) {
			String suff = word.substring(i);
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suff);
			if (suffixValue != null) {
				Integer tagSufFreq = suffixValue.getLeft().get(tag);
				Double relFreq = 0.0;
				if (tagSufFreq != null) {
					Double tagSufFreqD = tagSufFreq.doubleValue();
					relFreq = tagSufFreqD / suffixValue.getRight();

					ret = (ret + (relFreq * theta)) / thetaPlusOne;
					// logger.debug("accu(" + tag + ") = (prev(" + retP
					// + ") + relfreq(" + relFreq + ") * theta(" + theta
					// + "))/thetaPO(" + thetaPlusOne + ") =" + ret);
				}
			}
		}
		return ret;
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
	@Deprecated
	protected double getTagProbTnT(String word, int index, T tag) {

		if (index >= 0 && freqTable.containsKey(word.substring(index))) {
			String suffix = word.substring(index);

			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suffix);
			Integer tagSufFreq = suffixValue.getLeft().get(tag);
			Double tagSufFreqD;
			int newindex = index - 1;
			if (tagSufFreq == null) {
				newindex = -1;
				tagSufFreqD = 0.0;
			} else {
				tagSufFreqD = tagSufFreq.doubleValue();
			}
			Double relFreq = tagSufFreqD / suffixValue.getRight();
			double nTagProb = getTagProbTnT(word, newindex, tag);

			return (theta * relFreq + nTagProb) / thetaPlusOne;
		} else
			return 0;
	}

	@Override
	public Map<T, Double> getTagLogProbabilities(String word) {
		HashMap<T, Double> ret = new HashMap<T, Double>();
		Set<T> tags = freqTable.get("").getLeft().keySet();
		for (T tag : tags) {
			ret.put(tag, getTagLogProbability(word, tag));
		}
		return ret;
	}

	@Override
	public T getMaxProbabilityTag(String word) {
		return getMaxProbabilityTag(getTagLogProbabilities(word));
	}

	@Override
	public String toString() {
		return freqTable.toString();
	}

}
