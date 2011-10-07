package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Trie node, storing frequencies / probabilities of a given n-gram part
 * 
 * @author György Orosz
 * 
 * @param <I>
 *            type for nodeID
 * @param <N>
 *            type for frequency / probability
 * @param <W>
 *            type for words
 */
public abstract class TrieNode<I, N extends Number, W> {
	private final I id;
	private N num;
	private HashSet<I> childNodes;
	private final HashMap<W, N> words;

	/**
	 * Zero element of the given Number type
	 * 
	 * @return
	 */
	protected abstract N zero();

	/**
	 * Incrementing the given Number object
	 * 
	 * @param n
	 * @return
	 */
	protected abstract N increment(N n);

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The id of the node (usually the context part)
	 * @param word
	 *            word to store
	 */
	public TrieNode(I id, W word) {
		this.id = id;
		num = zero();
		words = new HashMap<W, N>();
		addWord(word);
	}

	/**
	 * Add a word the the node, and increments its frequency.
	 * 
	 * @param word
	 */
	public void addWord(W word) {
		if (words.containsKey(word)) {
			words.put(word, increment(words.get(word)));
		} else {
			words.put(word, increment(zero()));
		}
		num = increment(num);
	}

	/**
	 * Add a child to the node.
	 * 
	 * @param child
	 */
	public void addChild(I child) {
		if (childNodes == null) {
			childNodes = new HashSet<I>();
		}
		childNodes.add(child);
	}

	/**
	 * Return the id of the node.
	 * 
	 * @return
	 */
	public I getId() {
		return id;
	}

	/**
	 * Returns the frequency / probability of the node.
	 * 
	 * @return
	 */
	public N getNum() {
		return num;
	}

	/**
	 * Returns child nodes.
	 * 
	 * @return
	 */
	public Set<I> getChildNodes() {
		return childNodes;
	}

	/**
	 * Returns words and their frequencies / probabilities.
	 * 
	 * @return
	 */
	public Map<W, N> getWords() {
		return words;
	}
}
