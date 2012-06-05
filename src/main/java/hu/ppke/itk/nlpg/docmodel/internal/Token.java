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
package hu.ppke.itk.nlpg.docmodel.internal;

import hu.ppke.itk.nlpg.docmodel.IToken;

/**
 * Class representing a stemmed tagged token in a sentence.
 * 
 * @author György Orosz
 * 
 */
public class Token extends AbstractDocElement implements IToken {

	private static String SEP = "#";// + '\u001E';

	Token(IToken token) {
		this(token.getToken(), token.getStem(), token.getTag());
	}

	public Token(String token, String stem, String tag) {
		this.token = token;
		this.tag = tag;
		this.stem = stem;
	}

	public Token(String token, String tag) {
		this(token, null, tag);
	}

	public Token(String token) {
		this(token, null, null);
	}

	protected String tag;
	protected String token;
	protected String stem;

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public String getTag() {
		return tag;
	}

	@Override
	public String getStem() {
		return stem;
	}

	@Override
	public String toString(String separator) {
		if (getToken() == null && getStem() == null) {
			return getToken();
		} else if (getTag() != null && getStem() == null) {
			return getToken() + separator + getTag();
		} else {
			return getToken() + separator + getStem() + separator + getTag();
		}
	}

	@Override
	public boolean equals(Object token) {

		if (token != null && token instanceof IToken) {

			IToken t = (IToken) token;
			// return ((t.getStem() == null && getStem() == null) || t.getStem()
			// .equals(getStem()))
			// &&
			boolean tokenE = t.getToken().equals(getToken());
			boolean nullTag = t.getTag() == null && getTag() == null;
			boolean tagE = t.getTag().equals(getTag());
			return tokenE && (nullTag || tagE);

		} else
			return false;

	}

	@Override
	public int hashCode() {
		return this.getStem().hashCode() * 10 ^ 2 + this.getToken().hashCode()
				* 10 + this.getTag().hashCode();
	}

	@Override
	public String toString() {
		return toString(SEP);
	}
}
