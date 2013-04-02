package hu.ppke.itk.nlpg.purepos.common;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class SuffixCoderTest {

	@Test
	public void testCoding() {
		Pair<String, Pair<String, Integer>> x = SuffixCoder.decode("alma",
				"alom", 1);
		Assert.assertEquals("ma", x.getLeft());
		Assert.assertEquals("om", x.getRight().getLeft());
		Assert.assertEquals(new Integer(102), x.getRight().getRight());

		Assert.assertEquals("alom", SuffixCoder.encode("alma", 2, "om"));

		x = SuffixCoder.decode("tűnt", "tűnik", 1);
		System.out.println(x);
		Assert.assertEquals("t", x.getLeft());
		Assert.assertEquals("ik", x.getRight().getLeft());
		Assert.assertEquals(new Integer(101), x.getRight().getRight());

	}

	@Test
	public void conversionTest() {
		Assert.assertEquals("alma", SuffixCoder.postprocess("alma"));
		Assert.assertEquals("alma", SuffixCoder.postprocess("alma-"));
		Assert.assertEquals("-", SuffixCoder.postprocess("-"));
	}
}
