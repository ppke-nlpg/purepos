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
 * Reader class for reading a tagged, unstemmed token.
 * 
 * @author György Orosz
 * 
 */
public class StemmedTaggedTokenReader extends AbstractDocElementReader<IToken> {

	public StemmedTaggedTokenReader() {
		separator = "#";
	}

	public StemmedTaggedTokenReader(String separator) {
		this.separator = separator;
	}

	@Override
	public IToken read(String text) {
		try {
			String[] wordParts = text.split(separator);
			if (wordParts.length == 0)
				return null;
			IToken word = new Token(wordParts[0],
					wordParts[1].replace('_', ' '), wordParts[2]);
			return word;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println(text);
			return null;
		}
	}
}
