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
	 * @param aprioriProbs
	 * @return the value of theta
	 */
	public abstract double calculateTheta(Map<T, Double> aprioriProbs);

}
