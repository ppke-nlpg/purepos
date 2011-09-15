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
package hu.ppke.itk.nlpg.docmodel;

import hu.ppke.itk.nlpg.docmodel.api.IDocument;
import hu.ppke.itk.nlpg.docmodel.api.IParagraph;
import hu.ppke.itk.nlpg.docmodel.api.ISentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a document object which are built of paragraphes.
 * 
 * @author György Orosz
 * 
 */
public class Document extends AbstractDocElementContainer<IParagraph> implements
		IDocument {

	public Document(Iterable<IParagraph> elements) {
		super(elements);
	}

	public Document(IParagraph par) {
		List<IParagraph> pars = new ArrayList<IParagraph>();
		pars.add(par);
		init(pars);
	}

	@Override
	public List<ISentence> getSentences() {
		List<ISentence> ret = new ArrayList<ISentence>();
		for (IParagraph p : this) {
			for (ISentence s : p) {
				ret.add(s);
			}

		}
		return ret;
	}

	@Override
	public String toString() {
		return toString(NL);
	}

	@Override
	public String toString(String separator) {
		StringBuffer ret = new StringBuffer();
		for (IParagraph t : this) {
			ret.append(t.toString()).append(separator);
		}
		return ret.toString();
	}

}
