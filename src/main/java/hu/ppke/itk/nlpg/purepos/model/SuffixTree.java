package hu.ppke.itk.nlpg.purepos.model;

import java.util.Map;

/**
 * Implementors should implement a representation of a tree with word suffixes
 * combined with suffox counts.
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            Word type
 * @param <T>
 *            Tag type
 */
public abstract class SuffixTree<W, T> {
	/*
	 * maximum length of suffixes which are stored
	 */
	protected final int maxSuffixLength;

	public SuffixTree(int maxSuffixLength) {
		this.maxSuffixLength = maxSuffixLength;
	}

	/**
	 * Adds a word with a specific and count to the representation.
	 * 
	 * @param word
	 *            word added
	 * @param tag
	 *            tag added
	 * 
	 * @param count
	 *            tag count added
	 */
	public abstract void addWord(W word, T tag, int count);

	/**
	 * Using theta, it creates the guesser object.
	 * 
	 * @return a suffix guesser
	 */
	public abstract ISuffixGuesser<W, T> createGuesser(double theta);

	/**
	 * Calculate theta from the apriori probabilities.
	 * 
	 * Using weighted average for standard deviation: E_{P_t}(P_t()). For
	 * details see libmoot.
	 * 
	 * @param aprioriProbs
	 * @return the value of theta
	 */
	public static <T> double calculateTheta(Map<T, Double> aprioriProbs) {
		// TODO: understand how it really works -> weighted average of stddev
		// TODO: it can be moved to some util class as a static method
		double pAv = 0;
		for (Double val : aprioriProbs.values()) {
			pAv += Math.pow(val, 2);
		}
		double theta = 0;
		for (Double aProb : aprioriProbs.values()) {
			theta += aProb * Math.pow(aProb - pAv, 2);
		}

		return Math.sqrt(theta);
	}

}
