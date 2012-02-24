package hu.ppke.itk.nlpg.purepos.model.internal;

import static org.junit.Assert.fail;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.SuffixCoder;
import junit.framework.Assert;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class HashLemmaTreeTest {
	HashLemmaTree tree = new HashLemmaTree(100);
	IntVocabulary<String> vocabulary = new IntVocabulary<String>();

	@Before
	public void init() {
		tree = new HashLemmaTree(100);
		vocabulary = new IntVocabulary<String>();
	}

	private Pair<String, Integer> create(String s, Integer i) {
		return new ImmutablePair<String, Integer>(s, i);
	}

	@Test
	public void testAddWordStringTInt() {
		Token t = new Token("almája", "alma", "1");
		int curTag = vocabulary.addElement("1");
		Pair<String, Integer> res = SuffixCoder
				.addToken(t, vocabulary, tree, 1);
		Assert.assertEquals("a", res.getLeft());
		Integer val = 100 * curTag + 3;
		Assert.assertEquals(val, res.getRight());

		t = new Token("házat", "ház", "2");
		curTag = vocabulary.addElement("2");
		res = SuffixCoder.addToken(t, vocabulary, tree, 1);
		Assert.assertEquals("", res.getLeft());
		val = 100 * curTag + 2;
		Assert.assertEquals(val, res.getRight());

		t = new Token("ház", "ház", "2");
		curTag = vocabulary.addElement("2");
		res = SuffixCoder.addToken(t, vocabulary, tree, 1);
		Assert.assertEquals("", res.getLeft());
		val = 100 * curTag + 0;
		Assert.assertEquals(val, res.getRight());

	}

	@Test
	@Ignore
	public void testCreateGuesserDoubleMapOfTDouble() {
		fail("Not yet implemented");
	}

}
