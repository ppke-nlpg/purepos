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

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Document;
import hu.ppke.itk.nlpg.docmodel.internal.Paragraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading corpuses, which is the output of the HunPos tagger.
 * 
 * @author György Orosz
 * 
 */
public class HunPosCorpusReader extends AbstractDocElementReader<IDocument> {
	private final AbstractDocElementReader<IToken> wordParser;

	public HunPosCorpusReader() {
		this(new TaggedTokenReader("\t"));
		this.fileEncoding = "ISO-8859-2";
	}

	protected HunPosCorpusReader(AbstractDocElementReader<IToken> wordParser) {
		this.wordParser = wordParser;
	}

	@Override
	public IDocument read(String text) throws ParsingException {
		String[] sents = text.split(lineSeparator + lineSeparator);
		List<ISentence> sentences = new ArrayList<ISentence>();
		AbstractDocElementReader<ISentence> sentenceParser = new SentenceReader(
				wordParser);
		sentenceParser.separator = lineSeparator;
		for (int i = 0; i < sents.length; ++i) {
			if (sents[i].length() - 1 > 0) {
				ISentence sentence = sentenceParser.read(sents[i]);
				sentences.add(sentence);
			}
		}
		IDocument doc = new Document(new Paragraph(sentences));
		return doc;
	}
}
