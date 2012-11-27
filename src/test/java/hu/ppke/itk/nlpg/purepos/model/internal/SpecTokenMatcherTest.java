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
		Assert.assertEquals("@PUNCT", matcher.matchLexicalElement("-"));
		Assert.assertEquals("@PUNCT", matcher.matchLexicalElement("—"));
		Assert.assertEquals("@PUNCT", matcher.matchLexicalElement("&;"));
	}
}
