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

import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * Vocabulary mapping between W type elements and Indexing type elements
 * 
 * @author György Orosz
 * 
 * @param <I>
 *            Indexing type
 * @param <W>
 *            Mapped type
 */
public interface IVocabulary<W, I extends Comparable<I>> extends Serializable {

	/**
	 * Returns the size of the dictionary
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Returns the index for a specific word
	 * 
	 * @param word
	 * @return
	 */
	public I getIndex(W word);

	/**
	 * Returns the word for a specific index
	 * 
	 * @param index
	 * @return
	 */
	public W getWord(I index);

	@Deprecated
	public NGram<I> getIndeces(List<W> words);

	public I addElement(W element);

	public Set<I> getTagIndeces();

	// public I getExtremalElement();

	public void storeMaximalElement();

	public I getMaximalIndex();
}
