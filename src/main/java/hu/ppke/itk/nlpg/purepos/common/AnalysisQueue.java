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

import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Global holder for tags and stems in a pretagged input.
 * 
 * @author György Orosz
 * 
 */
public class AnalysisQueue {
	// (position, (tag+stem, prob)
	protected ArrayList<Map<String, Double>> anals;
	// (tag, (stem, prob))
	// protected ArrayList<Map<String, Double>> stems;
	// if the input contains probability information
	protected ArrayList<Boolean> useProb;
	protected ArrayList<String> words;

	protected static String alnumPat = "\\p{L}\\p{N}";
	protected static String punctPat = "\\p{P}";

	protected static String stringPat = "([" + alnumPat + punctPat + "]+)";
	protected static String analPat = "((" + stringPat + "(\\[[" + alnumPat
			+ "|]+\\])+)(\\$\\$-?[0-9]+(\\.[0-9]+)?)?)";

	protected static String analSplitRe = "\\|\\|";
	protected static String weightSplitPat = "\\$\\$";

	protected static Pattern analFormPat = Pattern.compile(stringPat
			+ "\\{\\{(" + analPat + "(\\|\\|" + analPat + ")*" + ")\\}\\}");

	public void init(int capacity) {
		anals = new ArrayList<Map<String, Double>>(capacity);
		// stems = new ArrayList<Map<String, Double>>(capacity);
		useProb = new ArrayList<Boolean>(capacity);
		words = new ArrayList<String>(capacity);
		for (int i = 0; i < capacity; ++i) {
			anals.add(null);
			// stems.add(null);
			useProb.add(null);
			words.add(null);
		}
	}

	public void addWord(String input, Integer position) {
		Matcher m = analFormPat.matcher(input);
		String word = m.replaceAll("$1");
		String analsStrings = m.replaceAll("$2");

		words.set(position, word);
		anals.set(position, new HashMap<String, Double>());

		List<String> analsList = Arrays.asList(analsStrings.split(analSplitRe));
		for (String anal : analsList) {
			int indexOfValSep = anal.indexOf("$$");
			String lemmaTag = anal;
			double prob = 1;
			if (indexOfValSep > -1) {
				useProb.set(position, true);
				prob = Double.parseDouble(anal.substring(indexOfValSep + 2));
				lemmaTag = anal.substring(0, indexOfValSep);
			}
			anals.get(position).put(lemmaTag, prob);

		}

	}

	public boolean hasAnal(Integer position) {
		return anals.size() > position && anals.get(position) != null;
	}

	public Map<String, Double> getAnals(Integer position) {
		return anals.get(position);
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
		for (Map.Entry<String, Double> entry : this.anals.get(position)
				.entrySet()) {
			String tagStr = anal2tag(entry.getKey());
			Integer tag = tagVocabulary.getIndex(tagStr);
			if (tag == null) {
				tag = tagVocabulary.addElement(tagStr);
			}
			retMap.put(tag, entry.getValue());

		}
		return retMap;
	}

	public Set<Integer> getAnalsAsSet(Integer position,
			IVocabulary<String, Integer> tagVocabulary) {
		Map<Integer, Double> retMap = transformTags(position, tagVocabulary);
		return retMap.keySet();

	}

	public static boolean isPreanalysed(String word) {
		return analFormPat.matcher(word).matches();
	}

	public static String clean(String word) {
		return analFormPat.matcher(word).replaceAll("$1");
	}

	public static String anal2tag(String anal) {
		return anal.substring(anal.indexOf("["));
	}

	public static String anal2lemma(String anal) {
		return anal.substring(0, anal.indexOf("["));
	}
}
