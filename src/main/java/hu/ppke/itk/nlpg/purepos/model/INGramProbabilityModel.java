package hu.ppke.itk.nlpg.purepos.model;

import java.io.File;
import java.util.List;

/**
 * Implementors should implement a n-gram probability model upon an n-rgam
 * model.
 * 
 * @author György Orosz
 * 
 * @param <T>
 * @param <W>
 */
public interface INGramProbabilityModel<T, W> {

	/**
	 * Writes the model to a file.
	 * 
	 * @param modelfile
	 *            output file name
	 */
	public void save(File modelfile);

	/**
	 * Get probability for a word and its context according to the model.
	 * 
	 * @param context
	 *            list that must nut be null
	 * @param word
	 *            word
	 * @return probability
	 */
	public Double getProbs(List<T> context, W word);

}
