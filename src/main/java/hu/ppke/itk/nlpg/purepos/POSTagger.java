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
package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.AnalysisQueue;
import hu.ppke.itk.nlpg.purepos.common.TAnalysisItem;
import hu.ppke.itk.nlpg.purepos.decoder.AbstractDecoder;
import hu.ppke.itk.nlpg.purepos.decoder.BeamSearch;
import hu.ppke.itk.nlpg.purepos.decoder.BeamedViterbi;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;


import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.base.Joiner;

import static hu.ppke.itk.nlpg.corpusreader.CorpusReader.*;

/**
 * Standard POS tagger implementation
 * 
 * @author György Orosz
 * 
 */
public class POSTagger implements ITagger {
	IMorphologicalAnalyzer analyzer;
	protected final AbstractDecoder decoder;
	protected final CompiledModel<String, Integer> model;
	protected String outputFormat;

	public POSTagger(final CompiledModel<String, Integer> model,
			double logTheta, double sufTheta, int maxGuessedTags) {
		this(model, new NullAnalyzer(), logTheta, sufTheta, maxGuessedTags,
				false);
	}

	public POSTagger(final CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, double sufTheta,
			int maxGuessedTags, boolean useBeamSearch) {
		this.model = model;
		this.analyzer = analyzer;
		if (useBeamSearch)
			this.decoder = new BeamSearch(model, analyzer, logTheta, sufTheta,
					maxGuessedTags);
		else
			this.decoder = new BeamedViterbi(model, analyzer, logTheta,
					sufTheta, maxGuessedTags);
	}

	protected static List<String> preprocessSentence(List<String> sentence, AnalysisQueue analysisQueue) {
		analysisQueue.init(sentence.size());
		int i = 0;
		for (String word : sentence) {
			analysisQueue.addWord(word, i);
			++i;
		}
		return analysisQueue.getAllWords();
	}

	protected static List<String> preprocessSentenceEx(ArrayList<Pair<String, ArrayList<TAnalysisItem>>> sentence,
													   AnalysisQueue analysisQueue) {
		analysisQueue.init(sentence.size());
		int i = 0;
		for (Pair<String, ArrayList<TAnalysisItem>> word : sentence) {
			analysisQueue.addWord(word.getLeft(), word.getRight(), i);
			++i;
		}

		return analysisQueue.getAllWords();
	}

	@Override
	public ISentence tagSentence(List<String> sentence) {
		return tagSentence(sentence, 1).get(0);
	}

	@Override
	public List<ISentence> tagSentence(List<String> sentence, int maxRes) {
		List<String> plain_sent = preprocessSentence(sentence, decoder.getUserAnals());
		List<Pair<List<Integer>, Double>> tagList = decoder.decode(plain_sent, maxRes);
		List<ISentence> ret = new ArrayList<ISentence>();
		for (Pair<List<Integer>, Double> tags : tagList) {
			List<IToken> tokens = merge(plain_sent, tags.getKey());
			Sentence sent = new Sentence(tokens);
			sent.setScore(tags.getValue());
			ret.add(sent);
		}

		return ret;
	}

	protected List<IToken> merge(List<String> sentence, List<Integer> tags) {
		Iterator<Integer> tagsIt = tags.iterator();
		Iterator<String> tokensIt = sentence.iterator();
		List<IToken> tokens = new ArrayList<IToken>();
		IVocabulary<String, Integer> vocab = model.getTagVocabulary();
		while (tagsIt.hasNext() && tokensIt.hasNext()) {
			String nextToken = tokensIt.next();
			Integer nextTag = tagsIt.next();
			String nextTagS = vocab.getWord(nextTag);
			IToken t = new Token(nextToken, nextTagS);
			tokens.add(t);
		}
		return tokens;
	}

	@Override
	public ISentence tagSentence(String sentence) {
		return tagSentence(sentence, 1).get(0);
	}

	@Override
	public List<ISentence> tagSentence(String sentence, int maxRes) {
		if (sentence == null || sentence.trim().equals(""))
			return null;
		List<ISentence> sents = tagSentence(
				Arrays.asList(sentence.split("\\s")), maxRes);
		return sents;
	}

	@Override
	public ISentence tagSentenceEx(ArrayList<Pair<String, ArrayList<TAnalysisItem>>> sentence) {
		return tagSentenceEx(sentence, 1).get(0);
	}

	@Override
	public List<ISentence> tagSentenceEx(ArrayList<Pair<String, ArrayList<TAnalysisItem>>> sentence, int maxRes) {
		List<String> plain_sent = preprocessSentenceEx(sentence, decoder.getUserAnals());
		List<Pair<List<Integer>, Double>> tagList = decoder.decode(plain_sent, maxRes);
		List<ISentence> ret = new ArrayList<ISentence>();
		for (Pair<List<Integer>, Double> tags : tagList) {
			List<IToken> tokens = merge(plain_sent, tags.getKey());
			Sentence sent = new Sentence(tokens);
			sent.setScore(tags.getValue());
			ret.add(sent);
		}
		return ret;
	}

	@Override
	public void tag(Scanner scanner, String inputFormat, PrintStream ps, String outputFormat) {
		tag(scanner, inputFormat, ps, outputFormat, 1);
	}

	protected String tagAndFormat(String line, int maxResNum) {
		String sentString = "";
		boolean showProb = maxResNum > 1;
		if (!line.trim().equals("")) {
			List<ISentence> s = tagSentence(line, maxResNum);
			sentString = sentences2string(s, showProb);
		}
		return sentString;
	}

	@Override
	public void tag(Scanner scanner, String inputFormat, PrintStream ps, String outputFormat, int maxResultsNumber) {
		this.outputFormat = outputFormat;
		if(inputFormat.equals(ORDINARY) ){
			tagOrdinaryFile(scanner, ps, maxResultsNumber);
		} else if (inputFormat.equals(VERT)){
			tagVerticalFile(scanner, ps, maxResultsNumber);
		}
	}

	/*
	 * Function to get the data from the input. Inside calls the tagAndFormat() function for tagging.
	 * If the desired output format is not vertical, then it prints too. If it's vertical, for the further actions see
	 * the createVerticalFle() function.
	 */
	protected void tagOrdinaryFile(Scanner scanner, PrintStream ps, int maxResultsNumber){
		String line;
		List<String> sentences = new ArrayList<String>();
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			if (line.length() > 0) {
				// removing the comments
				String[] origWords = line.split("\\s");
				String parsedLine = "";
				for (String word : origWords){
					if(!(word.startsWith(CorpusReader.XML_TAG_OPENER) && word.endsWith(CorpusReader.XML_TAG_CLOSER))){
						parsedLine += word+" ";
					}
				}
				String sentence = tagAndFormat(parsedLine.substring(0,parsedLine.length()-1), maxResultsNumber);
				if (this.outputFormat.equals(VERT)) {
					sentences.add(sentence);
				} else {
					String[] taggedWords = sentence.split("\\s");
					String newline = "";
					int j = 0;
					for (int i = 0;i < origWords.length;i++){
						if (origWords[i].startsWith(CorpusReader.XML_TAG_OPENER) &&
								origWords[i].endsWith(CorpusReader.XML_TAG_CLOSER)){
							newline += origWords[i]+" ";
						} else {
							newline += taggedWords[j]+" ";
							j++;
						}
					}
					ps.println(newline.substring(0,newline.length()-1));
				}
			} else {
				if (this.outputFormat.equals(VERT)) {
					sentences.add("");
				} else {
					ps.println();
				}
			}
		}
		if(this.outputFormat.equals(VERT)){
			createVerticalFile(ps,sentences);
		}
	}

	/*
	 * Function to get the data from a vertical input. Inside calls the tagAndFormat() function for tagging.
	 * If the desired output format is not vertical, then it prints too. If it's vertical,
	 * for the further actions see the writeVerticalFle() function.
	 */
	protected void tagVerticalFile(Scanner scanner, PrintStream ps, int maxResultsNumber){
		List<String> sentences = new ArrayList<String>();
		List<String> lines = new ArrayList<String>();

		String line;
		String sentence = "";
		int emptylinecounter = 0;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			if (line.length() > 0 && !(line.startsWith(XML_TAG_OPENER) && line.endsWith(XML_TAG_CLOSER))){
				if (AnalysisQueue.isPreanalysed(line)){
					line = line.replace("\t",""); // transforming the line to match the ordinary pre-analyzed input
				} else {
					line = line.replace('\t', '#'); // transforming the line to look like an ordinary input
				}
				sentence += line + " ";
				emptylinecounter = 0;
			}
			if ((line.length() == 0 || !scanner.hasNext())) {
				if (!sentence.equals("")) {
					String taggedSentence = tagAndFormat(sentence.substring(0, sentence.length() - 1), maxResultsNumber);
					if (this.outputFormat.equals(VERT)) {
						sentences.add(taggedSentence);
					} else {
						ps.println(taggedSentence);
					}
				}
				if(this.outputFormat.equals(ORDINARY)) {
					if (line.length() == 0) {
						emptylinecounter++;
					}
					if (emptylinecounter == 2) {
						ps.println();
						emptylinecounter = 0;
					}
				}
				sentence = "";
			}
			if(this.outputFormat.equals(VERT)){
				lines.add(line);
			}
		}
		if(this.outputFormat.equals(VERT)){
			writeVerticalFile(ps,sentences,lines);
		}
	}

	/*
	 * Function to write the analyzed data to vertical formatted output.
	 * Usable when the input was in vertical format too to keep the original tags, attributes and structure.
	 * ps: the desired output stream
	 * sentences: list of Strings, each object contains one already tagged sentence
	 * lines: list of Strings, each object contains one line of the input
	 */
	protected void writeVerticalFile(PrintStream ps, List<String> sentences, List<String> lines){
		List<String> words = sentences2words(sentences);
		Iterator<String> word = words.iterator();
		for (String line: lines) {
			if (line.length() > 0 && !(line.startsWith(XML_TAG_OPENER) && line.endsWith(XML_TAG_CLOSER))) {
				ps.println(word.next());
			} else {
				ps.println(line);
			}
		}
	}

	/*
	 * Function to create an vertical formatted output.
	 * Usable when the input was in other non-xml like format.
	 */
	protected void createVerticalFile(PrintStream ps, List<String> sentences){
		int sentence_size = sentences.size();
		for(int i = 0; i < sentence_size; i++){
			String sentence = sentences.get(i);
			if(sentence.length() > 0){
				List<String> words = Arrays.asList(sentence.split("\\s"));
				int words_size = words.size();
				for (int j = 0; j< words_size; j++) {
					ps.println(words.get(j).replace('#', '\t'));
					if (j == words_size-1 && i != sentence_size-1){
						ps.println();
					}
				}
			} else {
				ps.println();
			}
		}
	}

	/*
	 * Function to split the sentences into words and replace the separator character with tabulators if the output format is vertical.
	 */
	protected List<String> sentences2words (List<String> sentences){
		List<String> splitWords = new ArrayList<String>();
		String[] words;

		for(String sentence:sentences){
			words = sentence.split("\\s");
			for (String word: words){
				splitWords.add(word.replace('#', '\t'));
			}
		}
		return splitWords;
	}

	protected String sentences2string(List<ISentence> sents) {
		return sentences2string(sents, false);
	}

	protected String sentences2string(List<ISentence> sents, boolean showProb) {
		LinkedList<String> sentStrings = new LinkedList<String>();
		for (ISentence s : sents) {
			sentStrings.add(sentence2string(s, showProb));
		}
		return Joiner.on("\t").join(sentStrings);
	}

	protected String sentence2string(ISentence s, boolean showProb) {
		String ret = Joiner.on(" ").join(s);
		if (showProb) {
			ret = ret + "$$" + s.getScore() + "$$";
		}
		return ret;
	}
}
