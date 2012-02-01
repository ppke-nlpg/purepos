package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

import java.util.List;

/**
 * Implementors should represent n-grams of a corpus.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            the type which is used to represent a token
 */
public interface INGram<T extends Comparable<T>> extends Comparable<NGram<T>> {
	public List<T> toList();
}
