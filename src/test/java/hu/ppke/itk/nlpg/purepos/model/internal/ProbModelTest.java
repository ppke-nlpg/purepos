package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.ArrayList;
import java.util.Arrays;

import junit.framework.Assert;

import org.junit.Test;

public class ProbModelTest {

	@Test
	public void creation() {
		ArrayList<Double> lambdas = new ArrayList<Double>(Arrays.asList(0.0,
				1.0, 2.0, 4.0));
		// IntTrieNode<Integer> node = new IntTrieNode<Integer>(0);
		NGramModel<Integer> imodel = new NGramModel<Integer>(3);
		imodel.addWord(Arrays.asList(1, 2), 3);
		imodel.addWord(Arrays.asList(2, 3), 4);
		imodel.addWord(Arrays.asList(22, 3), 6);
		imodel.addWord(Arrays.asList(1, 2), 5);

		ProbModel<Integer> model = new ProbModel<Integer>(imodel.root, lambdas);
		Double val;

		// unigrams
		val = model.getProb(new ArrayList<Integer>(), 3);
		Assert.assertEquals(0.25, val);
		val = model.getProb(new ArrayList<Integer>(), 4);
		Assert.assertEquals(0.25, val);
		val = model.getProb(new ArrayList<Integer>(), 5);
		Assert.assertEquals(0.25, val);
		val = model.getProb(new ArrayList<Integer>(), -1);
		Assert.assertEquals(null, val);

		// bigrams
		val = model.getProb(Arrays.asList(2), 3);
		Assert.assertEquals(1.25, val);
		val = model.getProb(Arrays.asList(2), 5);
		Assert.assertEquals(1.25, val);
		val = model.getProb(Arrays.asList(3), 4);
		Assert.assertEquals(1.25, val);
		val = model.getProb(Arrays.asList(3), 6);
		Assert.assertEquals(1.25, val);
		val = model.getProb(Arrays.asList(3), -1);
		Assert.assertEquals(null, val);
		// it is going to be an unigram
		val = model.getProb(Arrays.asList(-1), 3);
		Assert.assertEquals(0.25, val);

		// trigrams
		val = model.getProb(Arrays.asList(1, 2), 3);
		Assert.assertEquals(3.25, val);
		val = model.getProb(Arrays.asList(1, 2), 5);
		Assert.assertEquals(3.25, val);
		val = model.getProb(Arrays.asList(22, 3), 6);
		Assert.assertEquals(5.25, val);
		val = model.getProb(Arrays.asList(2, 3), 4);
		Assert.assertEquals(5.25, val);
		val = model.getProb(Arrays.asList(2, 3), -1);
		Assert.assertEquals(null, val);

	}

	@Test
	public void getProbTest() {
		DoubleTrieNode<Integer> root = new DoubleTrieNode<Integer>(0);
		ProbModel<Integer> model = new ProbModel<Integer>(root);
		Double val = model.getProb(new ArrayList<Integer>(), 1);
		Assert.assertEquals(val, null);
		Double val2 = model.getWordProbs(new ArrayList<Integer>()).get(1);
		Assert.assertEquals(val2, null);

		root = new DoubleTrieNode<Integer>(0);
		root.addWord(1, 0.1);
		root.addWord(2, 0.2);
		model = new ProbModel<Integer>(root);
		val = model.getProb(new ArrayList<Integer>(), 1);
		val2 = model.getWordProbs(new ArrayList<Integer>()).get(1);
		Assert.assertEquals(val, 0.1);
		Assert.assertEquals(val, val2);
		val = model.getProb(new ArrayList<Integer>(), 2);
		val2 = model.getWordProbs(new ArrayList<Integer>()).get(2);
		Assert.assertEquals(val, 0.2);
		Assert.assertEquals(val, val2);

		root = new DoubleTrieNode<Integer>(0);
		root.addWord(1, 0.1);
		root.addWord(2, 0.2);
		DoubleTrieNode<Integer> c1 = new DoubleTrieNode<Integer>(1);
		c1.addWord(3, 0.3);
		c1.addWord(4, 0.4);
		DoubleTrieNode<Integer> c2 = new DoubleTrieNode<Integer>(2);
		c2.addWord(5, 0.5);
		c2.addWord(4, 0.4);
		DoubleTrieNode<Integer> c11 = new DoubleTrieNode<Integer>(11);
		c11.addWord(3, 0.33);
		DoubleTrieNode<Integer> c12 = new DoubleTrieNode<Integer>(12);
		c12.addWord(4, 0.44);
		c1.addChild(c12);
		c1.addChild(c11);
		root.addChild(c1);
		root.addChild(c2);
		model = new ProbModel<Integer>(root);
		val = model.getProb(Arrays.asList(11, 1), 3);
		Assert.assertEquals(0.33, val);
		val2 = model.getWordProbs(Arrays.asList(11, 1)).get(3);
		Assert.assertEquals(val, val2);

		val = model.getProb(Arrays.asList(12, 1), 4);
		Assert.assertEquals(0.44, val);
		val2 = model.getWordProbs(Arrays.asList(12, 1)).get(4);
		Assert.assertEquals(val, val2);

		val = model.getProb(Arrays.asList(2), 4);
		Assert.assertEquals(0.4, val);
		val2 = model.getWordProbs(Arrays.asList(2)).get(4);
		Assert.assertEquals(val, val2);

		val = model.getProb(Arrays.asList(2), 5);
		Assert.assertEquals(0.5, val);
		val2 = model.getWordProbs(Arrays.asList(2)).get(5);
		Assert.assertEquals(val, val2);

		// too big context
		val = model.getProb(Arrays.asList(1, 2), 4);
		Assert.assertEquals(0.4, val);
		val2 = model.getWordProbs(Arrays.asList(1, 2)).get(4);
		Assert.assertEquals(val, val2);

		val = model.getProb(Arrays.asList(1, 2, 3, 4, 12, 1), 4);
		Assert.assertEquals(0.44, val);
		val2 = model.getWordProbs(Arrays.asList(1, 2, 3, 4, 12, 1)).get(4);
		Assert.assertEquals(val, val2);

	}
}
