package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.ILexicon;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Standard implementation of the ILexicon interface which uses HashMap for
 * storing tags and their frequency.
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            Word type
 * @param <T>
 *            Tag type
 */
public class Lexicon<W, T> implements ILexicon<W, T> {
	protected HashMap<W, HashMap<T, Integer>> representation;

	protected int size;

	public Lexicon() {
		representation = new HashMap<W, HashMap<T, Integer>>();
		size = 0;
	}

	@Override
	public void addToken(W token, T tag) {
		if (representation.containsKey(token)) {
			HashMap<T, Integer> value = representation.get(token);
			if (value.containsKey(tag)) {
				value.put(tag, value.get(tag) + 1);
			} else {
				value.put(tag, 1);
			}
		} else {
			HashMap<T, Integer> value = new HashMap<T, Integer>();
			value.put(tag, 1);
			representation.put(token, value);
		}

		size++;
	}

	@Override
	public int size() {
		return size;
	}

	@Override
	public Set<T> getTags(W word) {
		HashMap<T, Integer> value = representation.get(word);
		if (value != null)
			return value.keySet();
		else
			return null;
	}

	@Override
	public int getWordCount(W word) {
		int totalcount = 0;
		HashMap<T, Integer> value = representation.get(word);
		if (value == null)
			return 0;
		for (Integer count : value.values())
			totalcount += count;
		return totalcount;
	}

	@Override
	public Iterator<Entry<W, HashMap<T, Integer>>> iterator() {
		return representation.entrySet().iterator();
	}

}
