package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import junit.framework.Assert;

import org.junit.Test;

public class SpecTokenMatcherTest {

	@Test
	public void testMatch() {
		SpecTokenMatcher matcher = new SpecTokenMatcher();
		Assert.assertEquals(null, matcher.matchLexicalElement("alma"));
		Assert.assertEquals("@CARD", matcher.matchLexicalElement("0087"));
		Assert.assertEquals(null, matcher.matchLexicalElement("a0087"));
		Assert.assertEquals("@CARDPUNCT", matcher.matchLexicalElement("0087."));
		Assert.assertEquals("@CARDSEPS",
				matcher.matchLexicalElement("0087.435"));
		Assert.assertEquals("@CARDSEPS",
				matcher.matchLexicalElement("0,0.8-7-4:35"));
		Assert.assertEquals("@CARDSUFFIX",
				matcher.matchLexicalElement("123abc"));
		Assert.assertEquals(null, matcher.matchLexicalElement("123abcd"));
		Assert.assertEquals("@HTMLENTITY",
				matcher.matchLexicalElement("&sfdf;"));
		Assert.assertEquals("@HTMLENTITY", matcher.matchLexicalElement("&sfdf"));
		Assert.assertEquals(null, matcher.matchLexicalElement("&;"));
	}
}
