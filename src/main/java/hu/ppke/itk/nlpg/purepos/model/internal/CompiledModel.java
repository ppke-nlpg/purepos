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

import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.ModelData;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class CompiledModel<W, T extends Comparable<T>> extends Model<W, T> {

	// @Deprecated
	// private CompiledModel(int taggingOrder, int emissionOrder,
	// int suffixLength, int rareFrequency,
	// IProbabilityModel<T, T> tagTransitionModel,
	// IProbabilityModel<T, W> standardEmissionModel,
	// IProbabilityModel<T, W> specTokensEmissionModel,
	// ISuffixGuesser<W, T> lowerCaseSuffixGuesser,
	// ISuffixGuesser<W, T> upperCaseSuffixGuesser,
	// ISuffixGuesser<W, Pair<W, Integer>> lemmaTree,
	// LemmaUnigramModel<W> unigramLemmaModel,
	// ILexicon<W, T> standardTokensLexicon,
	// ILexicon<W, T> specTokensLexicon,
	// IVocabulary<String, T> tagVocabulary,
	// Map<T, Double> aprioriTagProbs, List<Double> lemmaLambdas) {
	// super(taggingOrder, emissionOrder, suffixLength, rareFrequency,
	// standardTokensLexicon, specTokensLexicon, tagVocabulary);
	//
	// this.compiledData = new CompiledModelData<W, T>();
	// this.compiledData.tagTransitionModel = tagTransitionModel;
	// this.compiledData.standardEmissionModel = standardEmissionModel;
	// this.compiledData.specTokensEmissionModel = specTokensEmissionModel;
	// this.compiledData.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
	// this.compiledData.upperCaseSuffixGuesser = upperCaseSuffixGuesser;
	// this.compiledData.lemmaTree = lemmaTree;
	// this.compiledData.unigramLemmaModel = unigramLemmaModel;
	//
	// this.compiledData.aprioriTagProbs = aprioriTagProbs;
	// this.compiledData.lemmaLambdas = lemmaLambdas;
	//
	// }

	public CompiledModel(CompiledModelData<W, T> compiledData,
			ModelData<W, T> modelData) {
		super(modelData);
		this.compiledData = compiledData;
	}

	private static final long serialVersionUID = -3426883448646064198L;

	protected CompiledModelData<W, T> compiledData;

	public ISuffixGuesser<W, Pair<W, Integer>> getLemmaGuesser() {
		return compiledData.lemmaTree;
	}

	public LemmaUnigramModel<W> getUnigramLemmaModel() {
		return compiledData.unigramLemmaModel;
	}

	/**
	 * @return the tagTransitionModel
	 */
	public IProbabilityModel<T, T> getTagTransitionModel() {
		return compiledData.tagTransitionModel;
	}

	public List<Double> getLemmaLambdas() {
		return compiledData.lemmaLambdas;
	}

	/**
	 * @return the standardEmissionModel
	 */
	public IProbabilityModel<T, W> getStandardEmissionModel() {
		return compiledData.standardEmissionModel;
	}

	/**
	 * @return the specTokensEmissionModel
	 */
	public IProbabilityModel<T, W> getSpecTokensEmissionModel() {
		return compiledData.specTokensEmissionModel;
	}

	/**
	 * @return the lowerCaseSuffixGuesser
	 */
	public ISuffixGuesser<W, T> getLowerCaseSuffixGuesser() {
		return compiledData.lowerCaseSuffixGuesser;
	}

	/**
	 * @return the upperCaseSuffixGuesser
	 */
	public ISuffixGuesser<W, T> getUpperCaseSuffixGuesser() {
		return compiledData.upperCaseSuffixGuesser;
	}

	/**
	 * @return the aprioriTagProbs
	 */
	public Map<T, Double> getAprioriTagProbs() {
		return compiledData.aprioriTagProbs;
	}
}
