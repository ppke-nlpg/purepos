package hu.ppke.itk.nlpg.purepos.model;

import java.util.Map;

/**
 * Suffix guesser interface.
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            word type
 * @param <T>
 *            tag type
 */
public interface ISuffixGuesser<W, T> {

	/**
	 * Returns the probability of word and tag pair according to the words
	 * suffixes.
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public double getTagProbability(W word, T tag);

	/**
	 * Returns the logprobability of word and tag pair according to the words
	 * suffixes.
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public double getTagLogProbability(W word, T tag);

	/**
	 * Returns the probabilities of a word with all previously seen tag pair
	 * according to the words suffixes.
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public Map<T, Double> getTagLogProbabilities(W word);

	/**
	 * Returns the tag which has the highest probability in the suffix guesser.
	 * 
	 * @param word
	 * @return
	 */
	public T getMaxProbabilityTag(W word);

}
