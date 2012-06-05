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

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class IntVocabularyTest extends TestCase {

	@Test
	public void testAddElement() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(0, v.size());
		Integer key = v.addElement("alma");
		assertEquals(1, v.size());
		Assert.assertEquals(key, v.addElement("alma"));
		assertEquals(1, v.size());

	}

	// @Test
	// public void testSize() {
	// IntVocabulary<String> v = new IntVocabulary<String>();
	// assertEquals(0, v.size());
	// v.addElement("alma");
	// assertEquals(1, v.size());
	// }

	@Test
	public void testGetIndex() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(null, v.getIndex("alma"));
		v.addElement("alma");
		assertEquals(new Integer(0), v.getIndex("alma"));
	}

	@Test
	public void testGetWord() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(null, v.getWord(0));
		assertEquals(null, v.getWord(1));
		v.addElement("alma");
		assertEquals("alma", v.getWord(0));
	}

	@Test
	public void testGetIndeces() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		ArrayList<String> strs = new ArrayList<String>();
		strs.add("alma");
		strs.add("körte");
		assertEquals(null, v.getIndeces(strs));
		v.addElement("alma");
		assertEquals(null, v.getIndeces(strs));
		v.addElement("körte");
		ArrayList<Integer> idcs = new ArrayList<Integer>();
		idcs.add(0);
		idcs.add(1);
		assertEquals(v.getIndeces(strs), new NGram<Integer>(idcs));
	}
}
