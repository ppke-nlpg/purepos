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

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.IParagraph;
import hu.ppke.itk.nlpg.docmodel.ISentence;

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
