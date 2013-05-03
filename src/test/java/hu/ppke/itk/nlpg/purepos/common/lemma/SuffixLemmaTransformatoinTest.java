package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;

import org.junit.Test;

public class SuffixLemmaTransformatoinTest {
	@Test
	public void testCoding() {

		SuffixLemmaTransformation t = new SuffixLemmaTransformation("alma",
				"alom", 1);

		Assert.assertEquals("om", t.representation.getLeft());
		Assert.assertEquals(new Integer(102), t.representation.getRight());

		Assert.assertEquals("alom", t.analyze("alma").getLeft());
		Assert.assertEquals(new Integer(1), t.analyze("alma").getRight());

		SuffixLemmaTransformation t1 = new SuffixLemmaTransformation("tűnt",
				"tűnik", 1);
		Assert.assertEquals("ik", t1.representation.getLeft());
		Assert.assertEquals(new Integer(101), t1.representation.getRight());

	}

	@Test
	public void conversionTest() {
		SuffixLemmaTransformation t = new SuffixLemmaTransformation("alma",
				"alom", 1);
		Assert.assertEquals("alma", t.postprocess("alma"));
		Assert.assertEquals("alma", t.postprocess("alma-"));
		Assert.assertEquals("-", t.postprocess("-"));
	}

}
