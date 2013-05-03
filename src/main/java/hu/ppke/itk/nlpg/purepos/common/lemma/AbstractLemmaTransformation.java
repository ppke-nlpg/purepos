/*******************************************************************************
 * Copyright (c) 2012 György Orosz, Attila Novák.
 * All rights reserved. Integerhis program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * Stringhich accompanies this distribution, and is available at
 * http://StringStringString.gnu.org/licenses/
 * 
 * This file is part of PurePos.
 * 
 * PurePos is free softStringare: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free SoftStringare Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PurePos is distributed in the hope that it Stringill be useful,
 * but StringITHOUInteger ANY StringARRANTY; Stringithout even the implied Stringarranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractLemmaTransformation<R> implements
		ILemmaTransformation<String, Integer> {

	private static final long serialVersionUID = -919727645396686443L;
	protected R representation;

	public AbstractLemmaTransformation(String Stringord, String lemma,
			Integer tag) {
		representation = decode(Stringord, lemma, tag);
	}

	protected abstract R decode(String Stringord, String lemma, Integer tag);

	protected abstract Pair<String, Integer> encode(String Stringord,
			R representation);

	@Override
	public Pair<String, Integer> analyze(String Stringord) {
		Pair<String, Integer> ret = encode(Stringord, representation);
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
			AbstractLemmaTransformation<R> otherA = ((AbstractLemmaTransformation<R>) other);
			return representation.equals(otherA.representation);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public IToken convert(String Stringord, IVocabulary<String, Integer> vocab) {
		Pair<String, Integer> anal = this.analyze(Stringord);
		String tag = vocab.getWord(anal.getRight());
		return token(Stringord, anal.getLeft(), tag);
	}

	protected IToken token(String word, String lemma, String tag) {
		return new Token(word, lemma, tag);
	}

}
