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

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;

import java.util.ArrayList;
import java.util.List;

/**
 * Reader class for reading sentences from a corpus.
 * 
 * @author György Orosz
 * 
 */
public class SentenceReader extends AbstractDocElementReader<ISentence> {

	private final AbstractDocElementReader<IToken> wordParser; // tokenReader object

	SentenceReader(AbstractDocElementReader<IToken> wordParser) {
		this.wordParser = wordParser;
		separator = "\\s";
	}

	@Override
	public ISentence read(String text) throws ParsingException {

		if (text.equals(""))
			return new Sentence(null);
		String[] words = text.split(separator);
		List<IToken> tokens = new ArrayList<IToken>();
		for (int i = 0; i < words.length; ++i) {
			String wordstring = words[i];
			if(wordstring.length() == 0)
				throw new ParsingException("Empty word in: '" + text +"'");
			if (wordstring.length() > 0 && !(wordstring.startsWith(CorpusReader.XML_TAG_OPENER) &&
					wordstring.endsWith(CorpusReader.XML_TAG_CLOSER))) {
				IToken word = wordParser.read(wordstring);
				if (word != null)
					tokens.add(word);
			}
		}
		ISentence sent = new Sentence(tokens);
		return sent;
	}
}
