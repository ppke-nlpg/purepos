package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.HashMap;
import java.util.Map;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class HashSuffixGuesserTest {
	HashSuffixGuesser<Integer> guesser;
	Double theta = 0.5;
	Double thetaPlusOne = theta + 1;

	@Before
	public void initialize() {
		HashSuffixTree<Integer> suffixTree = new HashSuffixTree<Integer>(3);
		suffixTree.addWord("alma", 1, 2);
		suffixTree.addWord("alma", 2, 4);
		suffixTree.addWord("az", 3, 8);
		suffixTree.addWord("körte", 1, 1);
		guesser = (HashSuffixGuesser<Integer>) suffixTree.createGuesser(theta);
	}

	@Test
	@Ignore
	// TODO: write new test cases
	public void testGetTagProb() {
		Double zProb = guesser.getTagProb("z", 1, 3);
		Double zEst = (8.0 / 15.0 + theta * (1.0 / thetaPlusOne))
				/ thetaPlusOne;
		Assert.assertEquals(zEst, zProb, zEst * 0.01);

		Double z2Prob = guesser.getTagProb("z", 1, 2);
		Double z2Est = (4.0 / 15.0) / thetaPlusOne;
		Assert.assertEquals(z2Est, z2Prob, z2Est * 0.01);

		Double emptyProb = guesser.getTagProb("", 0, 1);
		Double emptyEst = ((1.0 + 2.0) / 15.0) / thetaPlusOne;
		Assert.assertEquals(emptyEst, emptyProb, emptyEst * 0.01);

		Double kProb = guesser.getTagProb("k", 1, 2);
		Double kEst = ((4.0 / 15.0)) / thetaPlusOne;
		Assert.assertEquals(kEst, kProb, kEst * 0.01);

	}

	@Test
	@Ignore
	public void testGetMaxProbabilityTagMapOfTDouble() {
		Integer max1 = guesser.getMaxProbabilityTag("alma");
		Assert.assertEquals(max1.intValue(), 2);
		int max2 = guesser.getMaxProbabilityTag("kalma");
		Assert.assertEquals(max2, 2);
		int max3 = guesser.getMaxProbabilityTag("nnn");
		Assert.assertEquals(max3, 3);

	}

	@Test
	@Ignore
	public void testAgainstHunPos() {
		HashSuffixTree<Integer> suffixTree = new HashSuffixTree<Integer>(3);
		Map<Integer, Double> m = new HashMap<Integer, Double>();
		suffixTree.addWord("bementem", 1, 1);
		suffixTree.addWord("1", 2, 1);
		suffixTree.addWord("a", 3, 1);
		suffixTree.addWord("bartomhoz", 4, 1);
		suffixTree.addWord("1", 2, 1);

		m.put(1, 0.2);
		m.put(2, 0.4);
		m.put(3, 0.2);
		m.put(4, 0.2);

		guesser = (HashSuffixGuesser<Integer>) suffixTree
				.createGuesser(suffixTree.calculateTheta(m));
		// Assert.assertEquals(3, (int) guesser.getMaxProbabilityTag("alma"));
	}

}
