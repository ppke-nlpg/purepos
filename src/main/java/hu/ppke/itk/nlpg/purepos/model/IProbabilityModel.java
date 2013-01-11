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

import java.util.List;

/**
 * Implementors should implement a n-gram probability model upon an n-rgam
 * model.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            tag type
 * @param <W>
 *            word type
 */
public interface IProbabilityModel<T, W> {

	/**
	 * Get probability for a word and its context according to the model.
	 * 
	 * If the given context is too large -- larger then we have in the model --
	 * the adequate part is used.
	 * 
	 * @param context
	 *            list that must nut be null
	 * @param word
	 *            word
	 * @return probability
	 */
	public Double getProb(List<T> context, W word);

	/**
	 * Get log probability for a word and its context according to the model.
	 * 
	 * If the given context is too large -- larger then we have in the model --
	 * the adequate part is used.
	 * 
	 * @param context
	 *            list that must nut be null
	 * @param word
	 *            word
	 * @return probability
	 */
	public Double getLogProb(List<T> context, W word);

	/**
	 * Get probability word pairs for a given context
	 * 
	 * If the given context is too large -- larger then we have in the model --
	 * the adequate part is used.
	 * 
	 * @param context
	 *            list of tags, must not be null
	 */
	// @Deprecated
	// public Map<W, Double> getWordProbs(List<T> context);
	public abstract void setContextMapper(IMapper<Integer> mapper);

	public abstract void setElementMapper(IMapper<W> mapper);

	public abstract IMapper<Integer> getContextMapper();

}
