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

	public int size();

	public I getIndex(W word);

	public W getWord(I index);

	public NGram<I> getIndeces(List<W> words);

	public void addElement(W element);
}
