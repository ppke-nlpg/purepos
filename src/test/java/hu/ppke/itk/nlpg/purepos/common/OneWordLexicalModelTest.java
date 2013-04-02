package hu.ppke.itk.nlpg.purepos.common;

import java.util.Arrays;
import java.util.HashMap;

import junit.framework.Assert;

import org.junit.Test;

public class OneWordLexicalModelTest {

	@Test
	public void testGetProb() {
		HashMap<Integer, Double> probs = new HashMap<Integer, Double>();
		probs.put(1, -0.8);
		probs.put(0, -0.2);
		OneWordLexicalModel m = new OneWordLexicalModel(probs, "alma");
		Assert.assertEquals(-0.8, m.getLogProb(Arrays.asList(3, 2, 1), "alma"));
		Assert.assertEquals(-0.2, m.getLogProb(Arrays.asList(3, 2, 0), "alma"));
		Assert.assertEquals(Double.NEGATIVE_INFINITY,
				m.getLogProb(Arrays.asList(3, 2, 3), "alma"));

		Assert.assertEquals(Double.NEGATIVE_INFINITY,
				m.getLogProb(Arrays.asList(3, 2, 0), "semmi"));
		Assert.assertEquals(Double.NEGATIVE_INFINITY,
				m.getLogProb(Arrays.asList(3, 2, 10), "semmi"));
	}
}
