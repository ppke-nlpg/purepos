package hu.ppke.itk.nlpg.purepos.model.internal;

import junit.framework.Assert;

import org.junit.Test;

public class CounterTest {

	@Test
	public void testCounter() {
		LemmaUnigramModel<String> c = new LemmaUnigramModel<String>();
		Assert.assertEquals(0, c.getCount("alma"));
		c.increment("alma");
		Assert.assertEquals(1, c.getCount("alma"));
		c.increment("alma");
		Assert.assertEquals(2, c.getCount("alma"));
		c.increment("alma");
		Assert.assertEquals(3, c.getCount("alma"));
	}

}
