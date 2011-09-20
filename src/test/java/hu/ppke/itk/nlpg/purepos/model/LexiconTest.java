package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.Lexicon;
import junit.framework.TestCase;

public class LexiconTest extends TestCase {

	public void testLexicon() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		assertEquals(lex.size(), 0);
	}

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

	public void testGetTags() {
		Lexicon<String, String> lex = new Lexicon<String, String>();
		assertNull(lex.getTags("alma"));
		lex.addToken("alma", "FN");
		assertNotNull(lex.getTags("alma"));
		assertTrue(lex.getTags("alma").contains("FN"));
	}

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
