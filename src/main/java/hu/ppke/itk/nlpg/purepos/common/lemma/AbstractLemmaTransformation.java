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
package hu.ppke.itk.nlpg.purepos.common.lemma;

import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractLemmaTransformation<W, T extends Comparable<T>, R>
		implements ILemmaTransformation<W, T> {

	private static final long serialVersionUID = -919727645396686443L;
	protected R representation;

	public AbstractLemmaTransformation(W word, W lemma, T tag) {
		representation = decode(word, lemma, tag);
	}

	protected abstract R decode(W word, W lemma, T tag);

	protected abstract Pair<String, Integer> encode(W word, R representation);

	public Pair<String, Integer> toAnalysis(W word) {
		Pair<String, Integer> ret = encode(word, representation);
		return Pair.of(postprocess(ret.getLeft()), ret.getRight());

	}

	protected String postprocess(String lemma) {
		int length = lemma.length();
		if (length > 1 && lemma.charAt(length - 1) == '-') {
			return lemma.substring(0, length - 1);
		}
		return lemma;

	}

	@Override
	public String toString() {
		return representation.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this.getClass().isInstance(other)) {
			@SuppressWarnings("unchecked")
			AbstractLemmaTransformation<W, T, R> otherA = ((AbstractLemmaTransformation<W, T, R>) other);
			return representation.equals(otherA.representation);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

}
