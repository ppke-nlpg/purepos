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
 * Corpus reader class. An instance is used the read a corpus.
 * 
 * @author György Orosz
 * 
 */
public class CorpusReader extends AbstractDocElementReader<IDocument> {
	/**
	 * Object which is used for reading tokens inside the corpus.
	 */

	public static String XML_TAG_OPENER = "<";
	public static String XML_TAG_CLOSER = ">";
	public static String VERT = "vert";
	public static String ORDINARY = "ord";


	protected AbstractDocElementReader<IToken> tokenReader = new StemmedTaggedTokenReader();

	public CorpusReader(){ this.inputFormat = ORDINARY; }

	public CorpusReader(String inputFormat) {
		this.inputFormat = inputFormat;
	}

	public CorpusReader(AbstractDocElementReader<IToken> tokenReader, String inputFormat) {
		this.tokenReader = tokenReader;
		this.inputFormat = inputFormat;
	}

	@Override
	public IDocument read(String text) throws ParsingException {
		String[] sents = text.split(lineSeparator);
		List<ISentence> sentences;
		if(inputFormat.equals(VERT)) {
			sentences = readVerticalFile(sents);
		} else{
			sentences = readOrdinaryFile(sents);
		}
		return new Document(new Paragraph(sentences));
	}

	private List<ISentence> readVerticalFile(String[] text) throws ParsingException{
		AbstractDocElementReader<ISentence> sentenceParser = new SentenceReader(
				tokenReader);
		List<ISentence> sentences = new ArrayList<ISentence>();

		String rawSentence = "";
		for (int i=0; i < text.length;i++){
			if (text[i].length() > 0) {
					if (!(text[i].startsWith(XML_TAG_OPENER) && text[i].endsWith(XML_TAG_CLOSER))){
						rawSentence+=text[i]+" ";
					}
			}
			if ((text[i].length() == 0 || i == text.length-1)&& !rawSentence.equals("")){
					sentences.add(sentenceParser.read(rawSentence.substring(0,rawSentence.length()-1).replace("\t","#")));
					rawSentence = "";
			}
		}
		return sentences;
	}

	private List<ISentence> readOrdinaryFile(String[] sents) throws ParsingException {
		List<ISentence> sentences = new ArrayList<ISentence>();
		AbstractDocElementReader<ISentence> sentenceParser = new SentenceReader(
				tokenReader);
		for (int i = 0; i < sents.length; ++i) {
			if (sents[i].length() > 0) {
				ISentence sentence = sentenceParser.read(sents[i]);
				sentences.add(sentence);
			}
		}
		return sentences;
	}

}
