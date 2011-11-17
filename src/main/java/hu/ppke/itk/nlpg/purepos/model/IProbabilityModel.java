package hu.ppke.itk.nlpg.purepos.model;

import java.util.List;
import java.util.Map;

/**
 * Implementors should implement a n-gram probability model upon an n-rgam
 * model.
 * 
 * @author György Orosz
 * 
 * @param <T>
 * @param <W>
 */
public interface IProbabilityModel<T, W> {

	/**
	 * Get probability for a word and its context according to the model.
	 * 
	 * @param context
	 *            list that must nut be null
	 * @param word
	 *            word
	 * @return probability
	 */
	public Double getProb(List<T> context, W word);

	/**
	 * Return a map with the apriori probabilities of the words.
	 * 
	 * @return
	 */
	public Map<W, Double> getWordAprioriProbs();

}
