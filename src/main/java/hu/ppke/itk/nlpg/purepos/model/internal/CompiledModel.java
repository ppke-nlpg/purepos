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

import hu.ppke.itk.nlpg.purepos.model.ILexicon;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;

import java.util.Map;

public class CompiledModel<W, T extends Comparable<T>> extends Model<W, T> {

	private Counter<W> lemmaCounter;

	CompiledModel(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency, IProbabilityModel<T, T> tagTransitionModel,
			IProbabilityModel<T, W> standardEmissionModel,
			IProbabilityModel<T, W> specTokensEmissionModel,
			ISuffixGuesser<W, T> lowerCaseSuffixGuesser,
			ISuffixGuesser<W, T> upperCaseSuffixGuesser,
			HashLemmaTree lemmaTree, Counter<W> lemmaCounter,
			ILexicon<W, T> standardTokensLexicon,
			ILexicon<W, T> specTokensLexicon,
			IVocabulary<String, T> tagVocabulary, Map<T, Double> aprioriTagProbs) {
		super(taggingOrder, emissionOrder, suffixLength, rareFrequency,
				standardTokensLexicon, specTokensLexicon, tagVocabulary);

		this.tagTransitionModel = tagTransitionModel;
		this.standardEmissionModel = standardEmissionModel;
		this.specTokensEmissionModel = specTokensEmissionModel;
		this.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
		this.upperCaseSuffixGuesser = upperCaseSuffixGuesser;
		this.lemmaTree = lemmaTree;
		this.lemmaCounter = lemmaCounter;

		this.aprioriTagProbs = aprioriTagProbs;
	}

	public Counter<W> getLemmaCounter() {
		return lemmaCounter;
	}

	private static final long serialVersionUID = -3426883448646064198L;

	protected IProbabilityModel<T, T> tagTransitionModel;

	protected IProbabilityModel<T, W> standardEmissionModel;

	protected IProbabilityModel<T, W> specTokensEmissionModel;

	protected ISuffixGuesser<W, T> lowerCaseSuffixGuesser;

	protected ISuffixGuesser<W, T> upperCaseSuffixGuesser;

	protected Map<T, Double> aprioriTagProbs;

	protected HashLemmaTree lemmaTree;

	public HashLemmaTree getLemmaTree() {
		return lemmaTree;
	}

	/**
	 * @return the tagTransitionModel
	 */
	public IProbabilityModel<T, T> getTagTransitionModel() {
		return tagTransitionModel;
	}

	/**
	 * @return the standardEmissionModel
	 */
	public IProbabilityModel<T, W> getStandardEmissionModel() {
		return standardEmissionModel;
	}

	/**
	 * @return the specTokensEmissionModel
	 */
	public IProbabilityModel<T, W> getSpecTokensEmissionModel() {
		return specTokensEmissionModel;
	}

	/**
	 * @return the lowerCaseSuffixGuesser
	 */
	public ISuffixGuesser<W, T> getLowerCaseSuffixGuesser() {
		return lowerCaseSuffixGuesser;
	}

	/**
	 * @return the upperCaseSuffixGuesser
	 */
	public ISuffixGuesser<W, T> getUpperCaseSuffixGuesser() {
		return upperCaseSuffixGuesser;
	}

	/**
	 * @return the aprioriTagProbs
	 */
	public Map<T, Double> getAprioriTagProbs() {
		return aprioriTagProbs;
	}
}
