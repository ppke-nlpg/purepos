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

import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * Standard implementation of the IVocabulary interface which uses BidiMap for
 * efficient storing of key value pairs.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            the type which is used to map words
 * 
 */
public abstract class Vocabulary<W, T extends Comparable<T>> implements
		IVocabulary<W, T> {

	public Vocabulary() {
		vocabulary = HashBiMap.create();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5641962775445714546L;

	@Override
	public Set<T> getTagIndeces() {
		return vocabulary.values();
	}

	protected BiMap<W, T> vocabulary;
	protected T maxKnownIndex;

	@Override
	public int size() {
		return vocabulary.size();
	}

	@Override
	public T getIndex(W word) {
		return vocabulary.get(word);
	}

	@Override
	public W getWord(T index) {
		return vocabulary.inverse().get(index);
	}

	@Override
	public NGram<T> getIndeces(List<W> words) {
		ArrayList<T> tmp = new ArrayList<T>();
		for (W w : words) {
			T val = vocabulary.get(w);
			if (val == null)
				return null;
			tmp.add(val);
		}

		return new NGram<T>(tmp);

	}

	@Override
	public T addElement(W element) {
		T key = vocabulary.get(element);
		if (key == null)
			return addVocabularyElement(element);
		else
			return key;
	}

	@Override
	public String toString() {
		return vocabulary.toString();
	}

	@Override
	public T getMaximalIndex() {
		return maxKnownIndex;
	}

	protected abstract T addVocabularyElement(W element);

}
