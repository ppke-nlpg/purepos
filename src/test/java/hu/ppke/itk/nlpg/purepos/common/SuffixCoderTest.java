package hu.ppke.itk.nlpg.purepos.common;

import junit.framework.Assert;

import org.junit.Test;

public class SuffixCoderTest {

	@Test
	public void conversionTest() {
		Assert.assertEquals("alma", SuffixCoder.postprocess("alma"));
		Assert.assertEquals("alma", SuffixCoder.postprocess("alma-"));
		Assert.assertEquals("-", SuffixCoder.postprocess("-"));
	}
}
