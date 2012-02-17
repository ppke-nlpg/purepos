/*******************************************************************************
 * Copyright (c) 2011 György Orosz, Attila Novák, Balázs Indig
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
