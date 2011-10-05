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

	public double getTagProbability(W word, T tag);

	public Map<T, Double> getTagProbabilities(W word);

	public T getMaxProbabilityTag(W word);

}
