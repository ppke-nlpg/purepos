package hu.ppke.itk.nlpg.purepos.model.internal;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Trie node storing values as Double
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            word type
 */
public class DoubleTrieNode<W> extends TrieNode<Integer, Double, W> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6085522644927269375L;

	protected DoubleTrieNode(Integer id) {
		super(id);
	}

	public DoubleTrieNode(Integer id, W word) {
		super(id, word);
	}

	@Override
	protected Double zero() {
		return 0.0;
	}

	@Override
	protected Double increment(Double n) {
		return n + 1.0;
	}

	@Override
	protected TrieNode<Integer, Double, W> createNode(Integer id) {
		return new DoubleTrieNode<W>(id);
	}

	public void addWord(W word, Double prob) {
		this.words.put(word, prob);
	}

	public void addChild(DoubleTrieNode<W> child) {
		if (childNodes == null) {
			childNodes = new HashMap<Integer, TrieNode<Integer, Double, W>>();
		}
		childNodes.put(child.getId(), child);
	}
}
