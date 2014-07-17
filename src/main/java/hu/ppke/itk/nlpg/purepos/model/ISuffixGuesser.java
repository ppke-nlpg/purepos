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

import hu.ppke.itk.nlpg.purepos.model.internal.TagMapper;

import java.util.Map;

/**
 * Suffix guesser interface.
 * 
 * @author György Orosz
 * 
 * @param <W>
 *            word type
 * @param <T>
 *            tag type
 */
public interface ISuffixGuesser<W, T> {

	/**
	 * Returns the probability of word and tag pair according to the words
	 * suffixes.
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public double getTagProbability(W word, T tag);

	/**
	 * Returns the logprobability of word and tag pair according to the words
	 * suffixes.
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public double getTagLogProbability(W word, T tag);

	/**
	 * Returns the probabilities of a word with all previously seen tag pair
	 * according to the words suffixes.
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public Map<T, Double> getTagLogProbabilities(W word);

	/**
	 * Returns the tag which has the highest probability in the suffix guesser.
	 * 
	 * @param word
	 * @return
	 */
	public T getMaxProbabilityTag(W word);

	public Map<T, Double> getSmoothedTagLogProbabilities(W word);

	public void setTagMapper(ITagMapper<T> mapper);

	public ITagMapper<T> getMapper();


}
