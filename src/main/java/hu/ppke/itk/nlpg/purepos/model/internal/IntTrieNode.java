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

/**
 * TrieNode class which holds Integer values.
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            word type
 */
public class IntTrieNode<W> extends TrieNode<Integer, Integer, W> implements
		Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	IntTrieNode(Integer id) {
		super(id);
	}

	public IntTrieNode(Integer id, W word) {
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
	protected TrieNode<Integer, Integer, W> createNode(Integer id) {
		return new IntTrieNode<W>(id);
	}

	public Double getAprioriProb(W word) {
		if (hasWord(word)) {
			return (double) getWord(word) / (double) getNum();
		} else {
			return 0.0;
		}
	}

}
