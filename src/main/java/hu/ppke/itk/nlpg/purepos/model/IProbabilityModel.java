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
 *            tag type
 * @param <W>
 *            word type
 */
public interface IProbabilityModel<T, W> {

	/**
	 * Get probability for a word and its context according to the model.
	 * 
	 * If the given context is too large -- larger then we have in the model --
	 * the adequate part is used.
	 * 
	 * @param context
	 *            list that must nut be null
	 * @param word
	 *            word
	 * @return probability
	 */
	public Double getProb(List<T> context, W word);

	/**
	 * Get log probability for a word and its context according to the model.
	 * 
	 * If the given context is too large -- larger then we have in the model --
	 * the adequate part is used.
	 * 
	 * @param context
	 *            list that must nut be null
	 * @param word
	 *            word
	 * @return probability
	 */
	public Double getLogProb(List<T> context, W word);

	/**
	 * Get probability word pairs for a given context
	 * 
	 * If the given context is too large -- larger then we have in the model --
	 * the adequate part is used.
	 * 
	 * @param context
	 *            list of tags, must not be null
	 */
	@Deprecated
	public Map<W, Double> getWordProbs(List<T> context);

	/**
	 * Return a map with the apriori probabilities of the words.
	 * 
	 * @return
	 */
	public Map<W, Double> getWordAprioriProbs();

}
