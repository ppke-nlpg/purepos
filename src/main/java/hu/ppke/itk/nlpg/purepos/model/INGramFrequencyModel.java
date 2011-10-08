package hu.ppke.itk.nlpg.purepos.model;

import java.util.List;

/**
 * Implementors should implement a model which stores N grams, and their
 * frequency / probability. The last element of the N-gram is called word, and
 * rest is context.
 * 
 * @author György Orosz
 * 
 * @param <C>
 *            context type
 * @param <W>
 *            word type
 */
public abstract class INGramFrequencyModel<C, W> {
	protected final int n;

	public INGramFrequencyModel(int n) {
		this.n = n;
	}

	/**
	 * Adds a word to the frequency model.
	 * 
	 * Context must not be null.
	 * 
	 * @param context
	 *            context which is used for the n-gram
	 * @param word
	 *            the word which is added
	 */
	public abstract void addWord(List<C> context, W word);

	/**
	 * Returns the frequency of n-grams, starting with the unigram, and etc.
	 * 
	 * Context must not be null.
	 * 
	 * @param context
	 *            the context part of the n-gram
	 * @param word
	 *            the word part of the n-gram
	 */
	public abstract List<Double> getWordFrequency(List<C> context, W word);

	public abstract int getTotalFrequency();

	public abstract INGramProbabilityModel<C, W> createProbabilityModel();

}
