/*******************************************************************************
 * Copyright (c) 2012 György Orosz, Attila Novák.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/
 * 
 * This file is part of PurePos.
 * 
 * PurePos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PurePos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
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
