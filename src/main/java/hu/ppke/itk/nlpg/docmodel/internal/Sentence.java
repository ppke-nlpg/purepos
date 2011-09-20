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

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;

/**
 * Represents a POS-tagged stemmed sentence.
 * 
 * @author oroszgy
 * 
 */
public class Sentence extends AbstractDocElementContainer<IToken> implements
		ISentence {
	protected final String SEP = " ";

	public Sentence(Iterable<IToken> elements) {
		super(elements);
	}

	@Override
	public String toString(String separator) {
		StringBuffer ret = new StringBuffer();
		for (IToken t : this) {
			ret.append(t.toString()).append(separator);
		}
		return ret.toString();
	}

	@Override
	public String toString() {
		return toString(SEP);
	}

	@Override
	public boolean equals(Object object) {
		if (object instanceof Sentence) {
			Sentence s = (Sentence) object;
			return this.getTokensString().equals(s.getTokensString())
					&& this.size() == s.size();
		} else {
			return false;
		}

	}

	@Override
	public int hashCode() {
		return getTokensString().hashCode();
	}

	protected String getTokensString() {
		StringBuffer buf = new StringBuffer();
		for (IToken t : this)
			buf.append(t.getToken());
		return buf.toString();
	}

}
