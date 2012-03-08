package hu.ppke.itk.nlpg.purepos.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementors should represent a POS tag lexicon: storing which tags are
 * possible for a token during a POS tagging.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type representing a tag
 * @param <W>
 *            type representing a word
 * 
 */
public interface ILexicon<W, T> extends
		Iterable<Map.Entry<W, HashMap<T, Integer>>> {

	/**
	 * Adds a token, tag pair to the lexicon.
	 * 
	 * @param token
	 *            token string
	 * @param tag
	 *            tag id
	 */
	public void addToken(W token, T tag);

	/**
	 * Returns the size of the lexicon.
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Get possible tags for a word.
	 * 
	 * @param word
	 *            word which is looked up
	 * @return possible tags
	 */
	public Set<T> getTags(W word);

	/**
	 * Returns total occurrences of a word
	 * 
	 * @param word
	 * @return
	 */
	public int getWordCount(W word);

	/**
	 * Return total count of a word with a tag
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public int getWordCountForTag(W word, T tag);

}
