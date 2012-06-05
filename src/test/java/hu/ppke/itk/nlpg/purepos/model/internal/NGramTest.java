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

import junit.framework.TestCase;

import org.junit.Test;

public class NGramTest extends TestCase {

	@Test
	public void testNGram() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		NGram<Integer> g = new NGram<Integer>(list, 2);
		list.add(1);
		assertNotSame(list.toString(), g.toString());

	}

	@Test
	public void testEqualsObject() {
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		NGram<Integer> g1 = new NGram<Integer>(list1, 2);
		NGram<Integer> g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.equals(g2));
		assertTrue(g2.equals(g1));
		list2.add(3);
		g2 = new NGram<Integer>(list2, 2);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
	}

	@Test
	public void testCompareTo() {
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);

		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(2);

		NGram<Integer> g1 = new NGram<Integer>(list1, 2);
		NGram<Integer> g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) < 0);

		list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(2);
		list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) > 0);

		list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) == 0);

		list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) > 0);

		list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) < 0);

		list1 = new ArrayList<Integer>();
		list1.add(2);
		list1.add(1);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(1);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertEquals(0, g1.compareTo(g2));

		list1 = new ArrayList<Integer>();
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) < 0);

		list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) > 0);
	}
}
