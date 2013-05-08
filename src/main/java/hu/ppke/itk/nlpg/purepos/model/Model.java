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
	@Deprecated
	protected Model(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency, ILexicon<W, T> standardTokensLexicon,
			ILexicon<W, T> specTokensLexicon,
			IVocabulary<String, T> tagVocabulary) {
		data = new ModelData<W, T>();
		this.data.taggingOrder = taggingOrder;
		this.data.emissionOrder = emissionOrder;
		this.data.suffixLength = suffixLength;
		this.data.rareFreqency = rareFrequency;

		this.data.standardTokensLexicon = standardTokensLexicon;
		this.data.specTokensLexicon = specTokensLexicon;
		this.data.tagVocabulary = tagVocabulary;
		this.data.eosIndex = tagVocabulary.addElement(ModelData.EOS_TAG);
		this.data.bosIndex = tagVocabulary.addElement(ModelData.BOS_TAG);
	}

	protected Model(ModelData<W, T> modelData) {
		this.data = modelData;
	}

	private static final long serialVersionUID = -8584335542969140286L;

	protected ModelData<W, T> data;

	// protected double theta;

	/**
	 * @return the eosTag
	 */
	public static String getEOSTag() {
		return ModelData.EOS_TAG;
	}

	/**
	 * @return the bosTag
	 */
	public static String getBOSTag() {
		return ModelData.BOS_TAG;
	}

	/**
	 * @return the eosToken
	 */
	public static String getEOSToken() {
		return ModelData.EOS_TOKEN;
	}

	/**
	 * @return the bosToken
	 */
	public static String getBOSToken() {
		return ModelData.BOS_TOKEN;
	}

	/**
	 * @return the taggingOrder
	 */
	public int getTaggingOrder() {
		return data.taggingOrder;
	}

	/**
	 * @return the emissionOrder
	 */
	public int getEmissionOrder() {
		return data.emissionOrder;
	}

	/**
	 * @return the suffixLength
	 */
	public int getSuffixLength() {
		return data.suffixLength;
	}

	/**
	 * @return the rareFreqency
	 */
	public int getRareFreqency() {
		return data.rareFreqency;
	}

	/**
	 * @return the standardTokensLexicon
	 */
	public ILexicon<W, T> getStandardTokensLexicon() {
		return data.standardTokensLexicon;
	}

	/**
	 * @return the specTokensLexicon
	 */
	public ILexicon<W, T> getSpecTokensLexicon() {
		return data.specTokensLexicon;
	}

	/**
	 * @return the tagVocabulary
	 */
	public IVocabulary<String, T> getTagVocabulary() {
		return data.tagVocabulary;
	}

	/**
	 * @return the eosIndex
	 */
	public T getEOSIndex() {
		return data.eosIndex;
	}

	public ModelData<W, T> getData() {
		return data;
	}

	/**
	 * @return the bosIndex
	 */
	public T getBOSIndex() {
		return data.bosIndex;
	}

}
