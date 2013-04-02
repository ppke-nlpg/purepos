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
package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.model.internal.TagMapper;

import java.io.Serializable;

/**
 * An object of this class is representing the model of a POS tagger.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type parameter for representing tags
 * 
 * @param <W>
 *            type parameter for representing words
 * 
 */
public abstract class Model<W, T extends Comparable<T>> implements Serializable {
	protected Model(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency, ILexicon<W, T> standardTokensLexicon,
			ILexicon<W, T> specTokensLexicon,
			IVocabulary<String, T> tagVocabulary) {
		this.taggingOrder = taggingOrder;
		this.emissionOrder = emissionOrder;
		this.suffixLength = suffixLength;
		this.rareFreqency = rareFrequency;

		this.standardTokensLexicon = standardTokensLexicon;
		this.specTokensLexicon = specTokensLexicon;
		this.tagVocabulary = tagVocabulary;
		eosIndex = tagVocabulary.addElement(EOS_TAG);
		bosIndex = tagVocabulary.addElement(BOS_TAG);
		stat = new Statistics();
	}

	private static final long serialVersionUID = -8584335542969140286L;

	protected static final String EOS_TAG = "</S>";

	protected static final String BOS_TAG = "<S>";

	protected static final String EOS_TOKEN = "<SE>";

	protected static final String BOS_TOKEN = "<SB>";

	protected Statistics stat;

	protected int taggingOrder;

	protected int emissionOrder;

	protected int suffixLength;

	protected int rareFreqency;

	protected ILexicon<W, T> standardTokensLexicon;

	protected ILexicon<W, T> specTokensLexicon;

	protected IVocabulary<String, T> tagVocabulary;

	protected T eosIndex;

	protected T bosIndex;

	// protected double theta;

	/**
	 * @return the eosTag
	 */
	public static String getEOSTag() {
		return EOS_TAG;
	}

	/**
	 * @return the bosTag
	 */
	public static String getBOSTag() {
		return BOS_TAG;
	}

	/**
	 * @return the eosToken
	 */
	public static String getEOSToken() {
		return EOS_TOKEN;
	}

	/**
	 * @return the bosToken
	 */
	public static String getBOSToken() {
		return BOS_TOKEN;
	}

	public Statistics getLastStat() {
		return stat;
	}

	/**
	 * @return the taggingOrder
	 */
	public int getTaggingOrder() {
		return taggingOrder;
	}

	/**
	 * @return the emissionOrder
	 */
	public int getEmissionOrder() {
		return emissionOrder;
	}

	/**
	 * @return the suffixLength
	 */
	public int getSuffixLength() {
		return suffixLength;
	}

	/**
	 * @return the rareFreqency
	 */
	public int getRareFreqency() {
		return rareFreqency;
	}

	/**
	 * @return the standardTokensLexicon
	 */
	public ILexicon<W, T> getStandardTokensLexicon() {
		return standardTokensLexicon;
	}

	/**
	 * @return the specTokensLexicon
	 */
	public ILexicon<W, T> getSpecTokensLexicon() {
		return specTokensLexicon;
	}

	/**
	 * @return the tagVocabulary
	 */
	public IVocabulary<String, T> getTagVocabulary() {
		return tagVocabulary;
	}

	/**
	 * @return the eosIndex
	 */
	public T getEOSIndex() {
		return eosIndex;
	}

	/**
	 * @return the bosIndex
	 */
	public T getBOSIndex() {
		return bosIndex;
	}

	protected static void addMappings(
			IProbabilityModel<Integer, String> standardEmissionModel,
			IProbabilityModel<Integer, String> specTokensEmissionModel,
			IProbabilityModel<Integer, Integer> tagTransitionModel,
			ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser,
			ISuffixGuesser<String, Integer> upperCaseSuffixGuesser,
			IVocabulary<String, Integer> tagVocabulary) {
		TagMapper mapper = new TagMapper(tagVocabulary);
		standardEmissionModel.setContextMapper(mapper);
		specTokensEmissionModel.setContextMapper(mapper);

		tagTransitionModel.setContextMapper(mapper);
		tagTransitionModel.setElementMapper(mapper);

		lowerCaseSuffixGuesser.setMapper(mapper);
		upperCaseSuffixGuesser.setMapper(mapper);

	}

}
