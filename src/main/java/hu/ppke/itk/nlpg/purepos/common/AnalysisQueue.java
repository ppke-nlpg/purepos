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
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * Global holder for tags and stems in a pretagged input.
 * 
 * @author György Orosz
 * 
 */
public class AnalysisQueue {
	// (position, (tag+stem, prob)
	protected List<List<TAnalysisItem>> anals;
	// (tag, (stem, prob))
	// protected ArrayList<Map<String, Double>> stems;
	// if the input contains probability information
	protected List<Boolean> useProb;
	protected List<String> words, allWords;

	// protected static String alnumPat = "\\p{L}\\p{N}";
	// protected static String punctPat = "\\p{P}";
	// protected static String tagPat = "[^\\]]";
	//
	// protected static String stringPat = "([" + alnumPat + punctPat + "]+)";
	// protected static String analPat = "((" + stringPat + "(\\[[" + tagPat
	// + "|]+\\])+)(\\$\\$-?[0-9]+(\\.[0-9]+)?)?)";
	//
	protected static String analSplitRe = "\\|\\|";

	// protected static String weightSplitPat = "\\$\\$";
	//
	// protected static Pattern analFormPat = Pattern.compile(stringPat
	// + "\\{\\{(" + analPat + "(\\|\\|" + analPat + ")*" + ")\\}\\}");

	public void init(int capacity) {
		anals = new ArrayList<List<TAnalysisItem>>(capacity);
		// stems = new ArrayList<Map<String, Double>>(capacity);
		useProb = new ArrayList<Boolean>(capacity);
		words = new ArrayList<String>(capacity);
		allWords = new ArrayList<String>(capacity);
		for (int i = 0; i < capacity; ++i) {
			anals.add(null);
			// stems.add(null);
			useProb.add(null);
			words.add(null);
		}
	}

	public void addWord(String input, Integer position) {
		if (!isPreanalysed(input)) {
			allWords.add(input);
			return;
		}

		Pair<String, List<String>> res = parse(input);
		words.set(position, res.getLeft());
		allWords.add(res.getLeft());

		List<TAnalysisItem> anals1 = new ArrayList<TAnalysisItem>();
		anals.set(position, anals1);

		for (String anal : res.getRight()) {
			int indexOfValSep = anal.indexOf("$$");
			String lemmaTag = anal;
			double prob = 1;
			if (indexOfValSep > -1) {
				useProb.set(position, true);
				prob = Double.parseDouble(anal.substring(indexOfValSep + 2));
				lemmaTag = anal.substring(0, indexOfValSep);
			}
			anals1.add(new TAnalysisItem(anal2lemma(lemmaTag), anal2tag(lemmaTag), prob));
		}

	}

	public void addWord(String word, ArrayList<TAnalysisItem> analsList, Integer position) {
		allWords.add(word);
		if (analsList.isEmpty()) return;
		words.set(position, word);
		anals.set(position, analsList);
	}

	public boolean hasAnal(Integer position) {
		return anals.size() > position && anals.get(position) != null;
	}

	public Map<String, Double> getAnals(Integer position) {
		Map<String, Double> ret = new HashMap<String, Double>();
		for (TAnalysisItem entry : anals.get(position)) {
			ret.put(entry.getLemma()+entry.getTag(), entry.getProb());
		}
		return ret;
	}

	public boolean useProbabilties(Integer position) {
		if (useProb.size() > position)
			return useProb.get(position) != null;
		return false;

	}

	public IProbabilityModel<Integer, String> getLexicalModelForWord(
			Integer position, IVocabulary<String, Integer> tagVocabulary) {
		Map<Integer, Double> retMap = transformTags(position, tagVocabulary);
		return new OneWordLexicalModel(retMap, this.words.get(position));
	}

	protected Map<Integer, Double> transformTags(Integer position,
			IVocabulary<String, Integer> tagVocabulary) {
		Map<Integer, Double> retMap = new HashMap<Integer, Double>();
		for (TAnalysisItem entry : anals.get(position)) {
			Integer tag = tagVocabulary.getIndex(entry.getTag());
			if (tag == null) {
				tag = tagVocabulary.addElement(entry.getTag());
			}
			retMap.put(tag, entry.getProb());

		}
		return retMap;
	}

	public List<String> getAllWords() {
		return allWords;
	}

	public Set<Integer> getTags(Integer position,
			IVocabulary<String, Integer> tagVocabulary) {
		Map<Integer, Double> retMap = transformTags(position, tagVocabulary);
		return retMap.keySet();

	}

	public Set<IToken> getAnalysises(Integer position) {
		Set<IToken> ret = new HashSet<IToken>();
		for (TAnalysisItem entry : anals.get(position)) {
			ret.add(new Token(words.get(position), entry.getLemma(), entry.getTag()));
		}
		return ret;
	}

	protected static Pair<String, List<String>> parse(String token) {
		int wordRB = token.indexOf("{{");
		int analRB = token.indexOf("}}");
		String word = token.substring(0, wordRB);
		String analsStrings = token.substring(wordRB + 2, analRB);
		List<String> analsList = Arrays.asList(analsStrings.split(analSplitRe));
		return ImmutablePair.of(word, analsList);
	}

	public static boolean isPreanalysed(String word) {
		return word.indexOf("{{") > 0 && word.lastIndexOf("}}") > 0;
	}

	protected static String clean(String word) {
		return word.substring(0, word.indexOf("{{"));
	}

	protected static String anal2tag(String anal) {
		return anal.substring(anal.indexOf("["));
	}

	protected static String anal2lemma(String anal) {
		return anal.substring(0, anal.indexOf("["));
	}
}
