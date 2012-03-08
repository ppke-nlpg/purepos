package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.HashMap;
import java.util.Map;

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
	// TODO: PERF: is it worth using berkeleylm, instead of my implementation:
	// it is
	// known to be fast and small
	protected final I id;
	protected N num;
	protected HashMap<I, TrieNode<I, N, W>> childNodes;
	protected final HashMap<W, N> words;

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

	protected abstract TrieNode<I, N, W> createNode(I id);

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The id of the node (usually the context part)
	 * @param word
	 *            word to store
	 */
	public TrieNode(I id, W word) {
		this(id);
		addWord(word);
	}

	protected TrieNode(I id) {
		this.id = id;
		num = zero();
		words = new HashMap<W, N>();
	}

	/**
	 * Add a word the the node, and increments its frequency.
	 * 
	 * @param word
	 */
	protected void addWord(W word) {
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
	public TrieNode<I, N, W> addChild(I child) {
		if (childNodes == null) {
			childNodes = new HashMap<I, TrieNode<I, N, W>>();
		}
		if (!childNodes.containsKey(child)) {
			TrieNode<I, N, W> childNode = createNode(child);
			childNodes.put(child, childNode);
			return childNode;
		} else {
			return childNodes.get(child);
		}
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
	public Map<I, TrieNode<I, N, W>> getChildNodes() {
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

	/**
	 * Returns true if has a child node with the specified id.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasChild(I id) {
		if (childNodes == null)
			return false;
		return childNodes.containsKey(id);
	}

	/**
	 * Returns the node with the specified id.
	 * 
	 * @param id
	 * @return
	 */
	public TrieNode<I, N, W> getChild(I id) {
		if (childNodes == null)
			return null;
		return childNodes.get(id);
	}

	/**
	 * Returns true if this node has the requested word.
	 * 
	 * @param word
	 * @return
	 */
	public boolean hasWord(W word) {
		if (words == null)
			return false;
		return words.containsKey(word);
	}

	/**
	 * Returns the numeric value according to the word.
	 * 
	 * @param word
	 * @return
	 */
	public N getWord(W word) {
		return words.get(word);
	}

	@Override
	public String toString() {
		return "(id:" + getId() // + ", childs:" + childNodes.toString()
				+ ", words:" + words.toString() + ")";
	}

	public String getReprString() {
		return getReprString("\t");
	}

	public String getReprString(String tab) {
		String ret = tab;
		ret += "(id:" + getId() + ", freq:" + num;
		ret += ", words:" + words.toString();
		if (childNodes != null && childNodes.size() > 0) {
			ret += ", childs:\n";
			for (TrieNode<I, N, W> node : childNodes.values())
				ret += node.getReprString(tab + tab);
		}
		ret += tab + ")\n";
		return ret;
	}
}
