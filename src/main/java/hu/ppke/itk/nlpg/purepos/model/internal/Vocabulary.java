package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Standard implementation of the IVocabulary interface which uses BidiMap for
 * efficient storing of key value pairs.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            the type which is used to map words
 * 
 */
public abstract class Vocabulary<W, T> implements IVocabulary<W, T> {

	@Override
	public Set<T> getTagIndeces() {
		return vocabulary.values();
	}

	protected BiMap<W, T> vocabulary = HashBiMap.create();

	@Override
	public int size() {
		return vocabulary.size();
	}

	@Override
	public T getIndex(W word) {
		return vocabulary.get(word);
	}

	@Override
	public W getWord(T index) {
		return vocabulary.inverse().get(index);
	}

	@Override
	public NGram<T> getIndeces(List<W> words) {
		ArrayList<T> tmp = new ArrayList<T>();
		for (W w : words) {
			T val = vocabulary.get(w);
			if (val == null)
				return null;
			tmp.add(val);
		}

		return new NGram<T>(tmp);

	}

	@Override
	public T addElement(W element) {
		T key = vocabulary.get(element);
		if (key == null)
			return addVocabularyElement(element);
		else
			return key;
	}

	protected abstract T addVocabularyElement(W element);

}
