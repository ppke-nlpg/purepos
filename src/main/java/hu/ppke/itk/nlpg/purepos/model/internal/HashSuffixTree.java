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

import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Suffix tree implementation, representing nodes in a hash table. Edges are not
 * stored, they can be easily constant time calculated from the strings.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            Stored tags type
 */
public class HashSuffixTree<T> extends SuffixTree<String, T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 3763884096384725634L;
	/**
	 * A node is: (suffix, ((tag, tag count), suffix count))
	 */
	protected HashMap<String, MutablePair<HashMap<T, Integer>, Integer>> representation = new HashMap<String, MutablePair<HashMap<T, Integer>, Integer>>();
	protected Integer totalTagCount = 0;

	public HashSuffixTree(int maxSuffixLength) {
		super(maxSuffixLength);
	}

	@Override
	public void addWord(String word, T tag, int count) {
		int end = word.length();
		int start = Math.max(0, end - maxSuffixLength);
		for (int pointer = start; pointer <= end; pointer++) {
			String suffix = word.substring(pointer);
			increment(suffix, tag, count);
		}
		totalTagCount += count;
	}

	protected void increment(String suffix, T tag, int count) {
		if (representation.containsKey(suffix)) {
			MutablePair<HashMap<T, Integer>, Integer> value = representation
					.get(suffix);
			HashMap<T, Integer> tagCounts = value.getLeft();
			if (tagCounts.containsKey(tag)) {
				tagCounts.put(tag, tagCounts.get(tag) + count);
			} else {
				tagCounts.put(tag, count);
			}
			value.setRight(value.getRight() + count);
		} else {
			HashMap<T, Integer> tagCounts = new HashMap<T, Integer>();
			tagCounts.put(tag, count);
			MutablePair<HashMap<T, Integer>, Integer> value = new MutablePair<HashMap<T, Integer>, Integer>(
					tagCounts, count);
			representation.put(suffix, value);
		}
	}

	@Override
	public ISuffixGuesser<String, T> createGuesser(double theta,
			Map<T, Double> aprioriProbs) {
		return new HashSuffixGuesser<T>(representation, aprioriProbs, theta);
	}

}
