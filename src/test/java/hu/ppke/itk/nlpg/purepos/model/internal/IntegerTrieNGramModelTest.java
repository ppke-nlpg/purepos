package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.ArrayList;
import java.util.List;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

public class IntegerTrieNGramModelTest {
	IntegerTrieNGramModel<Integer> model;

	@Before
	public void initialize() {
		model = new IntegerTrieNGramModel<Integer>(3);
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
}
