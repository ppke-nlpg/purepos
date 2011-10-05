package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.internal.Lexicon;
import junit.framework.TestCase;

import org.junit.Test;

public class LexiconTest extends TestCase {

	@Test
	public void testLexicon() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		assertEquals(lex.size(), 0);
	}

	@Test
	public void testAddToken() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		int size = lex.size();
		lex.addToken("alma", "FN");
		assertEquals(size + 1, lex.size());
		lex.addToken("alma", "FN");
		assertEquals(size + 2, lex.size());
		lex.addToken("alma", "FN1");
		assertEquals(size + 3, lex.size());
	}

	@Test
	public void testSize() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		assertEquals(lex.size(), 0);
		lex.addToken("alma", "FN");
		assertEquals(1, lex.size());
		lex.addToken("alma", "FN");
		assertEquals(2, lex.size());
		lex.addToken("alma", "FN1");
		assertEquals(3, lex.size());
		lex.addToken("körte", "FN1");
		assertEquals(4, lex.size());
	}

	@Test
	public void testGetTags() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		assertNull(lex.getTags("alma"));
		lex.addToken("alma", "FN");
		assertNotNull(lex.getTags("alma"));
		assertTrue(lex.getTags("alma").contains("FN"));
	}

	@Test
	public void testGetWordCount() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		assertEquals(lex.getWordCount("alma"), 0);
		lex.addToken("alma", "FN");
		assertEquals(lex.getWordCount("alma"), 1);
		lex.addToken("alma", "FN");
		assertEquals(lex.getWordCount("alma"), 2);
		lex.addToken("alma", "FN1");
		assertEquals(lex.getWordCount("alma"), 3);
		assertEquals(lex.getWordCount("körte"), 0);
	}

}
