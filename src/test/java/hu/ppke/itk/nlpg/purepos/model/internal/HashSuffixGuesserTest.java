/*******************************************************************************
 * Copyright (c) 2012 György Orosz, Attila Novák.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/
 * 
 * This file is part of PurePos.
 * 
 * PurePos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PurePos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@SuppressWarnings("deprecation")
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
		// guesser = (HashSuffixGuesser<Integer>)
		// suffixTree.createGuesser(theta);
	}

	@Test
	@Ignore
	// TODO: TEST: write new test cases
	public void testGetTagProb() {
		Double zProb = guesser.getTagProbTnT("z", 1, 3);
		Double zEst = (8.0 / 15.0 + theta * (1.0 / thetaPlusOne))
				/ thetaPlusOne;
		Assert.assertEquals(zEst, zProb, zEst * 0.01);

		Double z2Prob = guesser.getTagProbTnT("z", 1, 2);
		Double z2Est = (4.0 / 15.0) / thetaPlusOne;
		Assert.assertEquals(z2Est, z2Prob, z2Est * 0.01);

		Double emptyProb = guesser.getTagProbTnT("", 0, 1);
		Double emptyEst = ((1.0 + 2.0) / 15.0) / thetaPlusOne;
		Assert.assertEquals(emptyEst, emptyProb, emptyEst * 0.01);

		Double kProb = guesser.getTagProbTnT("k", 1, 2);
		Double kEst = ((4.0 / 15.0)) / thetaPlusOne;
		Assert.assertEquals(kEst, kProb, kEst * 0.01);

	}

	//
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

	@SuppressWarnings("static-access")
	@Test
	@Ignore
	public void testProbs() {
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
		for (String word : Arrays.asList("alma", "körte", "ajtóhoz", "1"))
			for (Map.Entry<Integer, Double> tagEntry : guesser
					.getTagLogProbabilities(word).entrySet()) {
				Assert.assertEquals(
						tagEntry.getValue(),
						(Double) guesser.getTagLogProbability(word,
								tagEntry.getKey()));
			}
	}

	@Test
	@SuppressWarnings("static-access")
	public void testBoostedProb() {
		HashSuffixTree<Integer> suffixTree = new HashSuffixTree<Integer>(3);
		Map<Integer, Double> m = new HashMap<Integer, Double>();
		suffixTree.addWord("bementem", 1, 1);
		suffixTree.addWord("bartomhoz", 4, 1);

		m.put(1, 0.2);
		m.put(2, 0.4);
		m.put(3, 0.2);
		m.put(4, 0.2);

		guesser = (HashSuffixGuesser<Integer>) suffixTree
				.createGuesser(suffixTree.calculateTheta(m));

		Assert.assertEquals(0.0, guesser.getTagProbBoosted("alma", 1, 1), 0.0);
		Assert.assertEquals(0.0446,
				guesser.getTagProbBoosted("asztaloz", 1, 0), 0.01);
		Assert.assertEquals(0.1705,
				guesser.getTagProbBoosted("asztaloz", 4, 1), 0.01);
		Assert.assertEquals(0.0, guesser.getTagProbBoosted("z", 4, 2), 0.01);

	}
}
