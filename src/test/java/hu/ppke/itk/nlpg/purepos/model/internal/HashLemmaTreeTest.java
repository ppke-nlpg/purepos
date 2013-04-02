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

	@SuppressWarnings("unused")
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

		t = new Token("arca", "arc", "2");
		curTag = vocabulary.addElement("2");
		res = SuffixCoder.addToken(t, vocabulary, tree, 1);
		Assert.assertEquals("", res.getLeft());
		val = 100 * curTag + 1;
		Assert.assertEquals(val, res.getRight());

	}

	@Test
	@Ignore
	public void testCreateGuesserDoubleMapOfTDouble() {
		fail("Not yet implemented");
	}

}
