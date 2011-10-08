package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.INGramFrequencyModel;
import hu.ppke.itk.nlpg.purepos.model.INGramProbabilityModel;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * N-gram model implementation which uses tries to store these elements.
 * (Similar to SRILM.)
 * 
 * Tries are stored in HashTables, for the sake of efficiency.
 * 
 * @author György Orosz
 * 
 * @param <C>
 *            context type
 * @param <W>
 *            word type
 */
public class IntegerTrieNGramModel<W> extends INGramFrequencyModel<Integer, W> {
	protected static class IntTrieNode<T> extends TrieNode<Integer, Integer, T> {
		IntTrieNode(Integer id) {
			super(id);
		}

		public IntTrieNode(Integer id, T word) {
			super(id, word);
		}

		@Override
		protected Integer zero() {
			return 0;
		}

		@Override
		protected Integer increment(Integer n) {
			return n + 1;
		}

		@Override
		protected TrieNode<Integer, Integer, T> createNode(Integer id) {
			return new IntTrieNode<T>(id);
		}

	};

	IntTrieNode<W> root;

	public IntegerTrieNGramModel(int n) {
		super(n);
		root = new IntTrieNode<W>(IntVocabulary.getExtremalElement());
	}

	@Override
	public void addWord(List<Integer> context, W word) {
		// TODO: is there any space for performance improvement? arraylist or
		// linkedlist?
		ListIterator<Integer> iterator = context.listIterator(context.size());
		IntTrieNode<W> act = root;
		int i = 0;
		int size = n - 1;
		act.addWord(word);
		while (iterator.hasPrevious() && i < size) {
			act = (IntTrieNode<W>) act.addChild(iterator.previous());
			act.addWord(word);
			i++;
		}

	}

	@Override
	public List<Double> getWordFrequency(List<Integer> context, W word) {
		ArrayList<Double> ret = new ArrayList<Double>();

		ret.add(calculateFreq(root, word));
		if (!(context == null || context.size() == 0)) {
			ListIterator<Integer> it = context.listIterator(context.size());
			Integer previous;
			IntTrieNode<W> actNode = root;
			while (it.hasPrevious() && actNode != null) {
				previous = it.previous();
				if (actNode.hasChild(previous)) {
					actNode = (IntTrieNode<W>) actNode.getChild(previous);
					ret.add(calculateFreq(actNode, word));
				} else {
					ret.add(0.0);
					while (it.hasPrevious()) {
						ret.add(0.0);
					}
					actNode = null;
				}
			}

		}

		return ret;
	}

	public Double calculateFreq(IntTrieNode<W> node, W word) {
		if (node.hasWord(word)) {
			return (double) node.getWord(word) / (double) node.getNum();
		} else {
			return 0.0;
		}
	}

	@Override
	public int getTotalFrequency() {
		return root.getNum();
	}

	@Override
	public INGramProbabilityModel<Integer, W> createProbabilityModel() {
		// TODO Auto-generated method stub
		return null;
	}

}
