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

import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

public class HashSuffixtreeTest {

	@Test
	public void testCalculateTheta() {
		// HashSuffixTree<Integer> t = new HashSuffixTree<Integer>(0);
		HashMap<Integer, Double> hm = new HashMap<Integer, Double>();
		Double theta;
		theta = SuffixTree.calculateTheta(hm);
		Assert.assertEquals(0.0, theta);

		hm.put(1, 0.5);
		hm.put(2, 0.25);
		hm.put(3, 0.25);
		theta = SuffixTree.calculateTheta(hm);
		Assert.assertEquals(0.125, theta);
	}

	@Test
	public void testAddWordStringTInt() {
		String[] words = { "", "a", "bc", "def", "ghij", "klmno", "pqrstu" };
		Integer suffixLength;
		for (suffixLength = 0; suffixLength < 7; ++suffixLength)
			for (String word : words) {
				HashSuffixTree<Integer> rep1 = new HashSuffixTree<Integer>(
						suffixLength);
				HashSuffixTree<Integer> rep2 = new HashSuffixTree<Integer>(
						suffixLength);
				Integer tag = 1;
				Integer count = 2;
				Integer tCount = rep1.totalTagCount;
				rep1.addWord(word, tag, count);
				Assert.assertEquals((Integer) (tCount + count),
						rep1.totalTagCount);
				for (int i = 0; i <= Math.min(suffixLength, word.length()); i++) {
					rep2.increment(word.substring(word.length() - i), tag,
							count);
				}

				Assert.assertEquals(rep1.representation, rep2.representation);
			}
	}

	@Test
	public void testIncrement() {
		Integer suffixlength = 3;
		HashSuffixTree<Integer> st = new HashSuffixTree<Integer>(suffixlength);
		String suffix1 = "ban";
		String suffix2 = "ben";
		Integer count1 = 2;
		Integer count2 = 3;
		Integer count3 = 4;
		Integer tag1 = 1;
		Integer tag2 = 2;

		st.increment(suffix1, tag1, count1);
		Assert.assertFalse(st.representation.containsKey(suffix2));
		Assert.assertTrue(st.representation.containsKey(suffix1));
		Assert.assertEquals(st.representation.get(suffix1).getRight(), count1);
		Assert.assertEquals(st.representation.get(suffix1).getLeft().get(tag1),
				count1);
		// Assert.assertEquals(st.totalTagCount, count1);

		st.increment(suffix1, tag1, count2);
		Assert.assertFalse(st.representation.containsKey(suffix2));
		Assert.assertTrue(st.representation.containsKey(suffix1));
		Assert.assertEquals(st.representation.get(suffix1).getRight(),
				(Integer) (count1 + count2));
		Assert.assertEquals(st.representation.get(suffix1).getLeft().get(tag1),
				(Integer) (count1 + count2));
		// Assert.assertEquals(st.totalTagCount, (Integer) (count1 + count2));

		st.increment(suffix1, tag2, count3);
		Assert.assertFalse(st.representation.containsKey(suffix2));
		Assert.assertTrue(st.representation.containsKey(suffix1));
		Assert.assertEquals(st.representation.get(suffix1).getRight(),
				(Integer) (count1 + count2 + count3));
		Assert.assertEquals(st.representation.get(suffix1).getLeft().get(tag2),
				(count3));
		// Assert.assertEquals(st.totalTagCount,
		// (Integer) (count1 + count2 + count3));
	}

}
