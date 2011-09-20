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

import hu.ppke.itk.nlpg.docmodel.IParagraph;
import hu.ppke.itk.nlpg.docmodel.ISentence;

/**
 * Represents a parapraph of tagged, stemmed sentence.
 * 
 * @author György Orosz
 * 
 */
public class Paragraph extends AbstractDocElementContainer<ISentence> implements
		IParagraph {

	public Paragraph(Iterable<ISentence> elements) {
		super(elements);
	}

	@Override
	public String toString() {
		return toString(NL);
	}

	@Override
	public String toString(String separator) {
		StringBuffer ret = new StringBuffer();
		for (ISentence t : this) {
			ret.append(t.toString()).append(separator);
		}
		return ret.toString();
	}
}
