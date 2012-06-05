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
import java.util.List;
import java.util.Map;

/**
 * Implementors should implement a model which stores N grams, and their
 * frequency / probability. The last element of the N-gram is called word, and
 * rest is context.
 * 
 * @author György Orosz
 * 
 * @param <C>
 *            context type
 * @param <W>
 *            word type
 */
public abstract class INGramModel<C, W> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7673887850100038882L;
	protected final int n;

	public INGramModel(int n) {
		this.n = n;
	}

	/**
	 * Adds a word to the frequency model.
	 * 
	 * Context must not be null.
	 * 
	 * @param context
	 *            context which is used for the n-gram
	 * @param word
	 *            the word which is added
	 */
	public abstract void addWord(List<C> context, W word);

	/**
	 * Returns the frequency of n-grams for a word, starting with the unigram,
	 * bigram and etc.
	 * 
	 * Context must not be null.
	 * 
	 * @param context
	 *            the context part of the n-gram
	 * @param word
	 *            the word part of the n-gram
	 */
	public abstract List<Double> getWordFrequency(List<C> context, W word);

	/**
	 * Calculating lambdas (see Brants(2000) Figure 1.)
	 */
	protected abstract void calculateNGramLamdas();

	/**
	 * Returns total number of n-grams
	 * 
	 * @return
	 */
	public abstract int getTotalFrequency();

	/**
	 * Creates a probability model for calculating n-gram probabilities
	 * 
	 * @return
	 */
	public abstract IProbabilityModel<C, W> createProbabilityModel();

	/**
	 * Returns the words which are added to the model, with their frequency.
	 * 
	 * @return
	 */
	public abstract Map<W, Integer> getWords();

	/**
	 * Return a map with the apriori probabilities of the words.
	 * 
	 * @return
	 */
	public abstract Map<W, Double> getWordAprioriProbs();

}
