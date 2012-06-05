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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Implementors should represent a POS tag lexicon: storing which tags are
 * possible for a token during a POS tagging.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type representing a tag
 * @param <W>
 *            type representing a word
 * 
 */
public interface ILexicon<W, T> extends
		Iterable<Map.Entry<W, HashMap<T, Integer>>> {

	/**
	 * Adds a token, tag pair to the lexicon.
	 * 
	 * @param token
	 *            token string
	 * @param tag
	 *            tag id
	 */
	public void addToken(W token, T tag);

	/**
	 * Returns the size of the lexicon.
	 * 
	 * @return
	 */
	public int size();

	/**
	 * Get possible tags for a word.
	 * 
	 * @param word
	 *            word which is looked up
	 * @return possible tags
	 */
	public Set<T> getTags(W word);

	/**
	 * Returns total occurrences of a word
	 * 
	 * @param word
	 * @return
	 */
	public int getWordCount(W word);

	/**
	 * Return total count of a word with a tag
	 * 
	 * @param word
	 * @param tag
	 * @return
	 */
	public int getWordCountForTag(W word, T tag);

}
