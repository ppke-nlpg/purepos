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

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.SuffixCoder;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class HashLemmaTree extends HashSuffixTree<Pair<String, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4680124440505966799L;

	@SuppressWarnings("unused")
	private HashLemmaTree() {
		this(10);
	}

	public HashLemmaTree(int maxSuffixLength) {
		super(maxSuffixLength);
	}

	@Override
	public void addWord(String suffString, Pair<String, Integer> tag, int count) {
		increment(suffString, tag, count);
	}

	// TODO: the relative probabilities just left out, it should be calculated
	// in the model
	public Map<IToken, Integer> getLemmas(String word,
			IVocabulary<String, Integer> vocab) {
		Map<IToken, Integer> ret = new HashMap<IToken, Integer>();
		String wordSuffix;
		for (int i = 0; i < word.length(); ++i) {
			wordSuffix = word.substring(word.length() - i);
			if (representation.containsKey(wordSuffix)) {
				HashMap<Pair<String, Integer>, Integer> anals = representation
						.get(wordSuffix).getLeft();
				Set<Pair<String, Integer>> codes = anals.keySet();
				for (Pair<String, Integer> code : codes) {
					ret.put(SuffixCoder.tokenForCode(code, word, vocab),
							anals.get(code));
				}
			}
		}
		return ret;
	}
}
