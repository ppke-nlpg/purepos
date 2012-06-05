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

import java.io.Serializable;
import java.util.HashMap;

/**
 * Trie node storing values as Double
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            word type
 */
public class DoubleTrieNode<W> extends TrieNode<Integer, Double, W> implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6085522644927269375L;

	protected DoubleTrieNode(Integer id) {
		super(id);
	}

	public DoubleTrieNode(Integer id, W word) {
		super(id, word);
	}

	@Override
	protected Double zero() {
		return 0.0;
	}

	@Override
	protected Double increment(Double n) {
		return n + 1.0;
	}

	@Override
	protected TrieNode<Integer, Double, W> createNode(Integer id) {
		return new DoubleTrieNode<W>(id);
	}

	public void addWord(W word, Double prob) {
		this.words.put(word, prob);
	}

	public void addChild(DoubleTrieNode<W> child) {
		if (childNodes == null) {
			childNodes = new HashMap<Integer, TrieNode<Integer, Double, W>>();
		}
		childNodes.put(child.getId(), child);
	}
}
