package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Implements a probability model for n-grams. Tags are represented as Integers.
 * 
 * @author György Orosz
 * 
 * @param <W>
 */
public class ProbModel<W> implements IProbabilityModel<Integer, W> {

	// TODO: use a more memory efficient model for storing these data
	protected DoubleTrieNode<W> root;

	protected ProbModel(DoubleTrieNode<W> root) {
		this.root = root;
	}

	public ProbModel(IntTrieNode<W> origRoot, ArrayList<Double> lambdas) {
		this.root = createRoot(origRoot, lambdas);
	}

	@Override
	public Double getProb(List<Integer> context, W word) {
		// for a context which is greater then the size of the model, the
		// context is cut and the greatest context probability is calculated
		ListIterator<Integer> iterator = context.listIterator(context.size());
		TrieNode<Integer, Double, W> node = root;
		Boolean findMore = true;
		Integer prev = -1;
		if (iterator.hasPrevious()) {
			prev = iterator.previous();
			findMore = node.hasChild(prev) && node.getChild(prev).hasWord(word);
		} else
			findMore = false;

		while (findMore) {
			node = node.getChild(prev);
			if (iterator.hasPrevious()) {
				prev = iterator.previous();
				findMore = node.hasChild(prev)
						&& node.getChild(prev).hasWord(word);
			} else
				findMore = false;
		}
		if (node.hasWord(word))
			return node.getWord(word);
		else
			return null;
	}

	@Override
	public Double getLogProb(List<Integer> context, W word) {
		return Math.log(getProb(context, word));
	}

	@Override
	// TODO: implement getProb() using this function
	public Map<W, Double> getWordProbs(List<Integer> context) {
		ListIterator<Integer> iterator = context.listIterator(context.size());
		TrieNode<Integer, Double, W> node = root;
		Boolean findMore = true;
		Integer prev = -1;
		if (iterator.hasPrevious()) {
			prev = iterator.previous();
			findMore = node.hasChild(prev);// &&
											// node.getChild(prev).hasWord(word);
		} else
			findMore = false;

		while (findMore) {
			node = node.getChild(prev);
			if (iterator.hasPrevious()) {
				prev = iterator.previous();
				findMore = node.hasChild(prev);
				// && node.getChild(prev).hasWord(word);
			} else
				findMore = false;
		}
		return node.getWords();
	}

	protected DoubleTrieNode<W> createRoot(IntTrieNode<W> node,
			ArrayList<Double> lambdas) {
		DoubleTrieNode<W> newRoot = calcProbs(node);
		// apply lambda multiplication
		double prob;
		for (Map.Entry<W, Double> e : newRoot.getWords().entrySet()) {
			prob = lambdas.get(0) + lambdas.get(1) * e.getValue();
			newRoot.addWord(e.getKey(), prob);
		}
		// for all childs in the node calculate the probs (check null case)
		Map<W, Double> words = newRoot.getWords();
		if (node.getChildNodes() != null)
			for (TrieNode<Integer, Integer, W> child : node.getChildNodes()
					.values()) {
				DoubleTrieNode<W> ch = createChild((IntTrieNode<W>) child,
						words, lambdas, 2);
				// TODO: should I handle the null case?
				newRoot.addChild(ch);

			}
		return newRoot;
	}

	protected DoubleTrieNode<W> createChild(IntTrieNode<W> originalNode,
			Map<W, Double> parentWords, ArrayList<Double> lambdas, int level) {
		if (lambdas.size() > level) {
			DoubleTrieNode<W> node = calcProbs(originalNode);
			// apply lambdas to word probs
			double lambda = lambdas.get(level);
			for (Map.Entry<W, Integer> wordEntry : originalNode.getWords()
					.entrySet()) {
				W word = wordEntry.getKey();
				double prob = parentWords.get(word);
				prob += lambda * originalNode.getAprioriProb(word);
				node.addWord(word, prob);
			}

			// for all child nodes apply the same
			if (originalNode.getChildNodes() != null)
				for (TrieNode<Integer, Integer, W> child : originalNode
						.getChildNodes().values()) {
					DoubleTrieNode<W> ch = createChild((IntTrieNode<W>) child,
							node.getWords(), lambdas, level + 1);
					if (ch != null)
						node.addChild(ch);
				}
			return node;
		} else {
			return null;
		}
	}

	protected DoubleTrieNode<W> calcProbs(IntTrieNode<W> node) {
		DoubleTrieNode<W> newRoot = new DoubleTrieNode<W>(node.getId());
		double tmpPrb = 0.0;
		// for all words in node calculate the probs
		for (W word : node.getWords().keySet()) {
			tmpPrb = node.getAprioriProb(word);
			newRoot.addWord(word, tmpPrb);
		}
		return newRoot;
	}

	@Override
	public Map<W, Double> getWordAprioriProbs() {
		return root.getWords();
	}

}
