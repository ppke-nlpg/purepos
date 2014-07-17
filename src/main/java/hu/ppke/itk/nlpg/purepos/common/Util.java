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

import hu.ppke.itk.nlpg.purepos.decoder.StemFilter;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModelData;
import hu.ppke.itk.nlpg.purepos.model.internal.StringMapper;
import hu.ppke.itk.nlpg.purepos.model.internal.TagMapper;
import hu.ppke.itk.nlpg.purepos.model.internal.StringMapping;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class Util {
	protected static final String STEM_FILTER_FILE = "purepos_stems.txt";
	protected static final String STEM_FILTER_PROPERTY = System
			.getProperty("stems.path");

	public static boolean isUpper(String lword, String word) {
		return !lword.equals(word);
	}

	// @Deprecated
	public static boolean isUpper(String word) {
		return !toLower(word).equals(word);
	}

	public static String toLower(String word) {
		return word.toLowerCase();
	}

	public static <E> boolean isNotEmpty(Collection<E> c) {
		return c != null && c.size() > 0;
	}

	public static <E> boolean isEmpty(Collection<E> c) {
		return !isNotEmpty(c);
	}

	public static StemFilter createStemFilter() {
		File localFile = new File(STEM_FILTER_FILE);
		String path = null;
		if (STEM_FILTER_PROPERTY != null) {
			File propFile = new File(STEM_FILTER_PROPERTY);
			if (propFile.exists()) {
				path = propFile.getAbsolutePath();
			}
		}

		if (localFile.exists()) {
			path = localFile.getAbsolutePath();
		}

		if (path == null)
			return null;
		try {
			return new StemFilter(new File(path));
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public static <K> Map.Entry<K, Double> findMax(Map<K, Double> map) {
		Map.Entry<K, Double> ret = null;
		for (Map.Entry<K, Double> e : map.entrySet()) {
			if (ret == null || e.getValue() > ret.getValue()) {
				ret = e;
			}
		}
		return ret;
	}

	public static <K, L> Pair<K, Double> findMax2(Map<K, Pair<L, Double>> map) {
		Pair<K, Double> ret = null;
		for (Map.Entry<K, Pair<L, Double>> e : map.entrySet()) {
			if (ret == null || e.getValue().getValue() > ret.getValue()) {
				ret = Pair.of(e.getKey(), e.getValue().getValue());
			}
		}
		return ret;
	}

	public static final double UNKOWN_VALUE = -99;// Double.NEGATIVE_INFINITY;

	public static void addMappings(
			CompiledModelData<String, Integer> compiledModelData,
			IVocabulary<String, Integer> tagVocabulary,
			List<StringMapping> tagMappings) {
		TagMapper tagMapper = new TagMapper(tagVocabulary, tagMappings);
		compiledModelData.standardEmissionModel.setContextMapper(tagMapper);
		compiledModelData.specTokensEmissionModel.setContextMapper(tagMapper);

		compiledModelData.tagTransitionModel.setContextMapper(tagMapper);
		compiledModelData.tagTransitionModel.setElementMapper(tagMapper);

		compiledModelData.lowerCaseSuffixGuesser.setTagMapper(tagMapper);
		compiledModelData.upperCaseSuffixGuesser.setTagMapper(tagMapper);
		
	}

	public static StringMapper LEMMA_MAPPER = null;
	
	public static AnalysisQueue analysisQueue = new AnalysisQueue();
}
