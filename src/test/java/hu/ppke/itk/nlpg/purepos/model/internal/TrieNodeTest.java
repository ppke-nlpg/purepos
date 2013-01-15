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

import junit.framework.Assert;

import org.junit.Test;

public class TrieNodeTest {
	@SuppressWarnings("serial")
	class Node extends TrieNode<Integer, Integer, String> {
		public Node(Integer id) {
			super(id);
		}

		public Node(Integer id, String word) {
			super(id, word);
		}

		@Override
		protected Integer zero() {
			return 0;
		}

		@Override
		protected Integer increment(Integer n) {
			return n + 1;
		}

		@Override
		protected TrieNode<Integer, Integer, String> createNode(Integer id) {
			return new Node(id);
		}

	}

	@Test
	public void testTrieNode() {
		Node n0 = new Node(0);
		Assert.assertNull(n0.getChildNodes());
		Assert.assertNotNull(n0.getWords());
		Node n = new Node(1, "alma");
		Assert.assertEquals(n.getId(), (Integer) 1);
		Assert.assertEquals(n.getWords().get("alma"), (Integer) 1);
		Assert.assertEquals(n.getNum(), (Integer) 1);
		Assert.assertNull(n.getChildNodes());
	}

	@Test
	public void testAddWord() {
		Node n = new Node(1, "alma");
		n.addWord("alma");
		Assert.assertEquals(n.getWords().get("alma"), (Integer) 2);
		Assert.assertEquals(n.getNum(), (Integer) 2);
		n.addWord("körte");
		Assert.assertEquals(n.getWords().get("körte"), (Integer) 1);
		Assert.assertEquals(n.getNum(), (Integer) 3);
		n.addWord("");
		Assert.assertEquals(n.getWords().get(""), (Integer) 1);
		Assert.assertEquals(n.getNum(), (Integer) 4);
		n.addWord(null);
		Assert.assertEquals(n.getWords().get(null), (Integer) 1);
		Assert.assertEquals(n.getNum(), (Integer) 5);

	}

	@Test
	public void testAddChild() {
		Node n = new Node(1, "alma");
		n.addChild(2);
		Assert.assertTrue(n.getChildNodes().containsKey(2));
		Assert.assertEquals(n.getChild(2), n.addChild(2));
	}

}
