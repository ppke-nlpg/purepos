package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

public class NGramModelTest {
	NGramModel<Integer> model;
	Logger logger = Logger.getLogger(this.getClass());
	{
		BasicConfigurator.configure();
	}

	@Before
	public void initialize() {
		model = new NGramModel<Integer>(3);
	}

	@Test
	public void testAgainstHunPos() {
		NGramModel<String> mymodel = new NGramModel<String>(3);
		// BOS[1] alma[4] körte[3] 1[2] EOS[0]
		// mymodel.addWord(Arrays.asList(2, 1), "EOS");
		mymodel.addWord(Arrays.asList(3, 2), "1");
		mymodel.addWord(Arrays.asList(4, 3), "körte");
		mymodel.addWord(Arrays.asList(1, 4), "alma");
		// System.out.println(mymodel.root.getReprString());
		Assert.assertTrue(mymodel.root.hasChild(2));
		Assert.assertTrue(mymodel.root.hasChild(4));
		Assert.assertTrue(mymodel.root.hasChild(3));
		Assert.assertTrue(mymodel.root.getChild(2).hasChild(3));
		Assert.assertTrue(mymodel.root.getChild(4).hasChild(1));
		Assert.assertTrue(mymodel.root.getChild(3).hasChild(4));

		mymodel.calculateNGramLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 0.0), mymodel.lambdas);

		// + BOS[1], a[3], 1[2], EOS[1]
		mymodel.addWord(Arrays.asList(3, 2), "1");
		mymodel.addWord(Arrays.asList(1, 3), "a");
		mymodel.calculateNGramLamdas();
		Assert.assertTrue(mymodel.root.hasChild(2));
		Assert.assertTrue(mymodel.root.hasChild(4));
		Assert.assertTrue(mymodel.root.hasChild(3));
		Assert.assertTrue(mymodel.root.getChild(2).hasChild(3));
		Assert.assertTrue(mymodel.root.getChild(4).hasChild(1));
		Assert.assertTrue(mymodel.root.getChild(3).hasChild(4));
		Assert.assertTrue(mymodel.root.getChild(3).hasChild(1));
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 1.0), mymodel.lambdas);
		logger.debug("\n" + mymodel.root.getReprString());
		logger.debug(mymodel.lambdas.toString());
	}

	@Test
	public void testCalculateLambdas() {
		model = new NGramModel<Integer>(3);
		model.calculateNGramLamdas();
		for (Double e : model.lambdas) {
			Assert.assertEquals(0.0, e);
		}
		List<Integer> c1 = Arrays.asList(1, 2);

		model.addWord(c1, 3);
		model.adjustLamdas();
		Assert.assertEquals(Arrays.asList(1.0, 0.0, 0.0, 0.0), model.lambdas);
		model.calculateNGramLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 0.0), model.lambdas);

		model.addWord(c1, 3);
		model.adjustLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 2.0), model.lambdas);
		model.calculateNGramLamdas();
		Assert.assertEquals(Arrays.asList(0.0, 0.0, 0.0, 1.0), model.lambdas);
	}

	@Test
	public void testMaxFinder() {
		model = new NGramModel<Integer>(3);
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
		Assert.assertEquals((Integer) (-1), ret.getLeft());
		Assert.assertEquals(0.0, ret.getRight());

		l.add(n2);
		ret = model.findMax(l, f);
		Assert.assertEquals((Integer) 1, ret.getLeft());
		Assert.assertEquals(0.5, ret.getRight());

		l.add(n1);
		ret = model.findMax(l, f);
		Assert.assertEquals((Integer) 2, ret.getLeft());
		Assert.assertEquals(1.0, ret.getRight());
	}

	@Test
	public void testIntegerTrieNGramModel() {
		model = new NGramModel<Integer>(3);
		Assert.assertEquals(0, model.getTotalFrequency());
		Assert.assertEquals(1,
				model.getWordFrequency(new ArrayList<Integer>(), 0).size());

		Assert.assertEquals(0, model.getTotalFrequency());
	}

	@Test
	public void testAddWordListOfIntegerW() {
		model = new NGramModel<Integer>(3);
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

}
