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
import java.util.Map;

/**
 * Implementors should implement a representation of a tree with word suffixes
 * combined with suffox counts.
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            Word type
 * @param <T>
 *            Tag type
 */
public abstract class SuffixTree<W, T> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -684858638817631397L;
	/*
	 * maximum length of suffixes which are stored
	 */
	// protected static Logger logger = Logger.getLogger(SuffixTree.class);
	protected final int maxSuffixLength;

	public SuffixTree(int maxSuffixLength) {
		this.maxSuffixLength = maxSuffixLength;
	}

	/**
	 * Adds a word with a specific and count to the representation.
	 * 
	 * @param word
	 *            word added
	 * @param tag
	 *            tag added
	 * 
	 * @param count
	 *            tag count added
	 */
	public abstract void addWord(W word, T tag, int count);

	/**
	 * Adds a word with a specific and count and minimum length to the
	 * representation.
	 * 
	 * @param word
	 *            word added
	 * @param tag
	 *            tag added
	 * 
	 * @param minLen
	 *            minimum number of suffixes stored in the tree
	 * 
	 * @param count
	 *            tag count added
	 */
	public abstract void addWord(W word, T tag, int count, int minLen);

	/**
	 * Using theta, it creates the guesser object.
	 * 
	 * @return a suffix guesser
	 */
	public abstract ISuffixGuesser<W, T> createGuesser(double theta// ,
	// Map<T, Double> apriori
	);

	/**
	 * Calculate theta from the apriori probabilities.
	 * 
	 * Using weighted average for standard deviation: E_{P_t}(P_t()). For
	 * details see libmoot.
	 * 
	 * @param aprioriProbs
	 * @return the value of theta
	 */
	public static <T> double calculateTheta(Map<T, Double> aprioriProbs) {
		// TODO: RESEARCH: understand how it really works -> weighted average of
		// stddev
		// TODO: it can be moved to some util class as a static method
		// logger.trace("AprioriProbs: " + aprioriProbs);
		double pAv = 0;
		for (Double val : aprioriProbs.values()) {
			pAv += Math.pow(val, 2);
		}
		double theta = 0;
		for (Double aProb : aprioriProbs.values()) {
			theta += aProb * Math.pow(aProb - pAv, 2);
		}

		return Math.sqrt(theta);
	}
}
