package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

import java.util.List;

/**
 * Vocabulary mapping between W type elements and Indexing type elements
 * 
 * @author György Orosz
 * 
 * @param <I>
 *            Indexing type
 * @param <W>
 *            Mapped type
 */
public interface IVocabulary<W, I> {

	/**
	 * Returns the size of the dictionary
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns the index for a specific word
	 * 
	 * @param word
	 * @return
	 */
	public I getIndex(W word);

	/**
	 * Returns the word for a specific index
	 * 
	 * @param index
	 * @return
	 */
	public W getWord(I index);

	@Deprecated
	public NGram<I> getIndeces(List<W> words);

	public void addElement(W element);

	// public I getExtremalElement();
}
