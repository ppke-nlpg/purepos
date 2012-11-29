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
package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.HashLemmaTree;
import hu.ppke.itk.nlpg.purepos.model.internal.Vocabulary;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Makes a compact representation for POS-tag transformation pairs.
 * 
 * @author György Orosz
 * 
 */
public class SuffixCoder {

	private static final int SHIFT = 100;

	public static Pair<String, Integer> addToken(String word, String stem,
			Integer tag, HashLemmaTree tree, int count) {
		int i;
		for (i = 0; i < word.length() && i < stem.length(); ++i) {
			if (word.charAt(i) != stem.charAt(i)) {
				break;
			}
		}
		String wordSuff = word.substring(i);
		int cutSize = wordSuff.length();
		String lemmaSuff = stem.substring(i);

		int code = SHIFT * tag + cutSize;
		Pair<String, Integer> ret = new ImmutablePair<String, Integer>(
				lemmaSuff, code);

		tree.addWord(wordSuff, ret, count);
		return ret;

	}

	public static Pair<String, Integer> addToken(IToken stemmedToken,
			Vocabulary<String, Integer> vocab, HashLemmaTree tree, int count) {
		String word = stemmedToken.getToken();
		String stem = stemmedToken.getStem();
		String tag = stemmedToken.getTag();
		return addToken(word, stem, vocab.getIndex(tag), tree, count);
	}

	public static IToken tokenForCode(Pair<String, Integer> code, String word,
			IVocabulary<String, Integer> vocab) {
		int tagCode = code.getRight() / SHIFT;
		int cutSize = code.getRight() % SHIFT;
		String add = code.getLeft();
		String tag = vocab.getWord(tagCode);
		String lemma = postprocess(word.substring(0, word.length() - cutSize)
				+ add);
		return new Token(word, lemma, tag);
	}

	protected static String postprocess(String string) {
		int length = string.length();
		if (length > 1 && string.charAt(length - 1) == '-') {
			return string.substring(0, length - 1);
		}
		return string;
	}
}
