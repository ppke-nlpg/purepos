package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

public class HashSuffixtree {

	@Test
	public void testCalculateTheta() {
		HashSuffixTree<Integer> t = new HashSuffixTree<Integer>(0);
		HashMap<Integer, Double> hm = new HashMap<Integer, Double>();
		Double theta;
		theta = t.calculateTheta(hm);
		Assert.assertEquals(0.0, theta);

		hm.put(1, 0.5);
		hm.put(2, 0.25);
		hm.put(3, 0.25);
		theta = t.calculateTheta(hm);
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
				rep1.addWord(word, tag, count);
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
		Assert.assertEquals(st.totalTagCount, count1);

		st.increment(suffix1, tag1, count2);
		Assert.assertFalse(st.representation.containsKey(suffix2));
		Assert.assertTrue(st.representation.containsKey(suffix1));
		Assert.assertEquals(st.representation.get(suffix1).getRight(),
				(Integer) (count1 + count2));
		Assert.assertEquals(st.representation.get(suffix1).getLeft().get(tag1),
				(Integer) (count1 + count2));
		Assert.assertEquals(st.totalTagCount, (Integer) (count1 + count2));

		st.increment(suffix1, tag2, count3);
		Assert.assertFalse(st.representation.containsKey(suffix2));
		Assert.assertTrue(st.representation.containsKey(suffix1));
		Assert.assertEquals(st.representation.get(suffix1).getRight(),
				(Integer) (count1 + count2 + count3));
		Assert.assertEquals(st.representation.get(suffix1).getLeft().get(tag2),
				(count3));
		Assert.assertEquals(st.totalTagCount,
				(Integer) (count1 + count2 + count3));
	}

}
