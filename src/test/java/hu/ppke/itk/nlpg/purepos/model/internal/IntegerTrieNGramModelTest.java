package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.internal.IntegerTrieNGramModel.IntTrieNode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Test;

public class IntegerTrieNGramModelTest {
	IntegerTrieNGramModel<Integer> model;

	@Before
	public void initialize() {
		model = new IntegerTrieNGramModel<Integer>(3);
	}

	@Test
	public void testMaxFinder() {

		Integer f = 0;
		IntTrieNode<Integer> n0 = new IntTrieNode<Integer>(-1, 0);
		IntTrieNode<Integer> n1 = new IntTrieNode<Integer>(-1, 0);
		n1.addWord(0);
		IntTrieNode<Integer> n2 = new IntTrieNode<Integer>(-1, 0);
		n2.addWord(5);
		n2.addWord(0);
		ArrayList<TrieNode<Integer, Integer, Integer>> l = new ArrayList<TrieNode<Integer, Integer, Integer>>();
		// MutablePair<ArrayList<IntTrieNode<Integer>>, Integer> arg = new
		// MutablePair<ArrayList<IntTrieNode<Integer>>, Integer>(
		// l, f);
		Pair<Integer, Double> ret = model.findMax(l, f);
		Assert.assertEquals(ret.getLeft(), null);
		Assert.assertEquals(ret.getRight(), null);

		l.add(n0);
		ret = model.findMax(l, f);
		Assert.assertEquals((Integer) 0, ret.getLeft());
		Assert.assertEquals(-1.0, ret.getRight());

		l.add(n2);
		ret = model.findMax(l, f);
		Assert.assertEquals((Integer) 1, ret.getLeft());
		Assert.assertEquals(1.0 / 2.0, ret.getRight());

		l.add(n1);
		ret = model.findMax(l, f);
		Assert.assertEquals((Integer) 2, ret.getLeft());
		Assert.assertEquals(1.0, ret.getRight());
	}

	@Test
	public void testIntegerTrieNGramModel() {
		Assert.assertEquals(0, model.getTotalFrequency());
		Assert.assertEquals(1,
				model.getWordFrequency(new ArrayList<Integer>(), 0).size());

		Assert.assertEquals(0, model.getTotalFrequency());
	}

	@Test
	public void testAddWordListOfIntegerW() {
		List<Integer> l1 = new ArrayList<Integer>();
		l1.add(1);
		l1.add(2);

		model.addWord(l1, 3);
		Assert.assertEquals(3, model.getWordFrequency(l1, 3).size());
		Assert.assertEquals(1.0, model.getWordFrequency(l1, 3).get(0));
		Assert.assertEquals(1.0, model.getWordFrequency(l1, 3).get(1));
		Assert.assertEquals(1.0, model.getWordFrequency(l1, 3).get(2));
		Assert.assertEquals(3, model.getWordFrequency(l1, -1).size());
		Assert.assertEquals(0.0, model.getWordFrequency(l1, -1).get(0));
		Assert.assertEquals(0.0, model.getWordFrequency(l1, -1).get(1));
		Assert.assertEquals(0.0, model.getWordFrequency(l1, -1).get(2));
		Assert.assertEquals(2, model.getWordFrequency(l1.subList(1, 2), 3)
				.size());
		Assert.assertEquals(1.0, model.getWordFrequency(l1.subList(1, 2), 3)
				.get(0));
		Assert.assertEquals(1.0, model.getWordFrequency(l1.subList(1, 2), 3)
				.get(1));
		Assert.assertEquals(1, model.getWordFrequency(l1.subList(2, 2), 3)
				.size());
		Assert.assertEquals(1, model.getWordFrequency(null, 3).size());
		Assert.assertEquals(1.0, model.getWordFrequency(l1.subList(2, 2), 3)
				.get(0));

		model.addWord(l1, 4);
		Assert.assertEquals(3, model.getWordFrequency(l1, 3).size());
		Assert.assertEquals(0.5, model.getWordFrequency(l1, 3).get(0));
		Assert.assertEquals(0.5, model.getWordFrequency(l1, 3).get(1));
		Assert.assertEquals(0.5, model.getWordFrequency(l1, 3).get(2));
		Assert.assertEquals(3, model.getWordFrequency(l1, -1).size());
		Assert.assertEquals(0.0, model.getWordFrequency(l1, -1).get(0));
		Assert.assertEquals(0.0, model.getWordFrequency(l1, -1).get(1));
		Assert.assertEquals(0.0, model.getWordFrequency(l1, -1).get(2));
		Assert.assertEquals(2, model.getWordFrequency(l1.subList(1, 2), 3)
				.size());
		Assert.assertEquals(0.5, model.getWordFrequency(l1.subList(1, 2), 3)
				.get(0));
		Assert.assertEquals(0.5, model.getWordFrequency(l1.subList(1, 2), 3)
				.get(1));
		Assert.assertEquals(1, model.getWordFrequency(l1.subList(2, 2), 3)
				.size());
		Assert.assertEquals(1, model.getWordFrequency(null, 3).size());
		Assert.assertEquals(0.5, model.getWordFrequency(l1.subList(2, 2), 3)
				.get(0));

		List<Integer> l2 = new ArrayList<Integer>();
		l2.add(11);
		l2.add(22);

		model.addWord(l2, 33);
		Assert.assertEquals(3, model.getWordFrequency(l2, 33).size());
		Assert.assertEquals(1.0 / 3.0, model.getWordFrequency(l2, 33).get(0));
		Assert.assertEquals(1.0, model.getWordFrequency(l2, 33).get(1));
		Assert.assertEquals(1.0, model.getWordFrequency(l2, 33).get(2));
		Assert.assertEquals(3, model.getWordFrequency(l2, -1).size());
		Assert.assertEquals(0.0, model.getWordFrequency(l2, -1).get(0));
		Assert.assertEquals(0.0, model.getWordFrequency(l2, -1).get(1));
		Assert.assertEquals(0.0, model.getWordFrequency(l2, -1).get(2));
		Assert.assertEquals(2, model.getWordFrequency(l2.subList(1, 2), 33)
				.size());
		Assert.assertEquals(1.0 / 3.0,
				model.getWordFrequency(l2.subList(1, 2), 33).get(0));
		Assert.assertEquals(1.0, model.getWordFrequency(l2.subList(1, 2), 33)
				.get(1));
		Assert.assertEquals(1, model.getWordFrequency(l2.subList(2, 2), 33)
				.size());
		Assert.assertEquals(1, model.getWordFrequency(null, 33).size());
		Assert.assertEquals(1.0 / 3.0,
				model.getWordFrequency(l2.subList(2, 2), 33).get(0));

		List<Integer> l3 = new ArrayList<Integer>();
		l3.add(111);
		l3.add(2);

		model.addWord(l3, 333);
		Assert.assertEquals(3, model.getWordFrequency(l3, 333).size());
		Assert.assertEquals(1.0 / 4.0, model.getWordFrequency(l3, 333).get(0));
		Assert.assertEquals(1.0 / 3.0, model.getWordFrequency(l3, 333).get(1));
		Assert.assertEquals(1.0, model.getWordFrequency(l3, 333).get(2));
		Assert.assertEquals(3, model.getWordFrequency(l3, -1).size());
		Assert.assertEquals(0.0, model.getWordFrequency(l3, -1).get(0));
		Assert.assertEquals(0.0, model.getWordFrequency(l3, -1).get(1));
		Assert.assertEquals(0.0, model.getWordFrequency(l3, -1).get(2));
		Assert.assertEquals(2, model.getWordFrequency(l3.subList(1, 2), 333)
				.size());
		Assert.assertEquals(1.0 / 4.0,
				model.getWordFrequency(l3.subList(1, 2), 333).get(0));
		Assert.assertEquals(1.0 / 3.0,
				model.getWordFrequency(l3.subList(1, 2), 333).get(1));
		Assert.assertEquals(1, model.getWordFrequency(l3.subList(2, 2), 333)
				.size());
		Assert.assertEquals(1, model.getWordFrequency(null, 333).size());
		Assert.assertEquals(1.0 / 4.0,
				model.getWordFrequency(l3.subList(2, 2), 333).get(0));
	}

	@Test
	public void testCalculateLambdas() {
		model.calculateNGramLamdas();
		for (Double e : model.lambdas) {
			Assert.assertEquals(0.0, e);
		}
		List<Integer> c1 = Arrays.asList(1, 2);

		model.addWord(c1, 3);
		model.adjustLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 0.0), model.lambdas);
		model.calculateNGramLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 0.0), model.lambdas);

		model.addWord(c1, 3);
		model.adjustLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 2.0), model.lambdas);
		model.calculateNGramLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 1.0), model.lambdas);
	}
}
