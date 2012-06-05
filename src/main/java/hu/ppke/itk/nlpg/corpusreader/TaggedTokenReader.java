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
package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

/**
 * Reader class for reading a tagged token.
 * 
 * @author György Orosz
 * 
 */
public class TaggedTokenReader extends AbstractDocElementReader<IToken> {

	public TaggedTokenReader() {
		separator = "#";
	}

	public TaggedTokenReader(String sep) {
		this.separator = sep;
	}

	@Override
	public IToken read(String text) {

		int pos = text.indexOf(separator);
		if (pos < 0 || text.equals("_"))
			return null;
		String tag = text.substring(pos + 1).trim();
		String word = text.substring(0, pos).trim();

		IToken t = new Token(word, tag);
		return t;
	}
}
