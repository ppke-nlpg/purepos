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

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.AnalysisQueue;
import hu.ppke.itk.nlpg.purepos.common.Global;
import hu.ppke.itk.nlpg.purepos.decoder.AbstractDecoder;
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

	public POSTagger(final CompiledModel<String, Integer> model,
			double logTheta, double sufTheta, int maxGuessedTags) {
		this(model, new NullAnalyzer(), logTheta, sufTheta, maxGuessedTags);
	}

	public POSTagger(final CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, double sufTheta,
			int maxGuessedTags) {
		this.model = model;
		this.analyzer = analyzer;
		this.decoder = new BeamedViterbi(model, analyzer, logTheta, sufTheta,
				maxGuessedTags);
		// this.decoder = new BeamSearch(model, analyzer, 10, sufTheta,
		// maxGuessedTags);
	}

	protected static List<String> preprocessSentence(List<String> sentence) {
		Global.analysisQueue.init(sentence.size());
		ArrayList<String> ret = new ArrayList<String>(sentence.size());
		int i = 0;
		for (String word : sentence) {
			if (AnalysisQueue.isPreanalysed(word)) {
				Global.analysisQueue.addWord(word, i);
				ret.add(AnalysisQueue.clean(word));
			} else {
				ret.add(word);
			}
			++i;
		}

		return ret;
	}

	@Override
	public ISentence tagSentence(List<String> sentence) {
		return tagSentence(sentence, 1).get(0);
	}

	@Override
	public List<ISentence> tagSentence(List<String> sentence, int maxRes) {
		sentence = preprocessSentence(sentence);
		List<Pair<List<Integer>, Double>> tagList = decoder.decode(sentence,
				maxRes);
		List<ISentence> ret = new ArrayList<ISentence>();
		for (Pair<List<Integer>, Double> tags : tagList) {
			List<IToken> tokens = merge(sentence, tags.getKey());
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
	public void tag(Scanner scanner, PrintStream ps) {
		tag(scanner, ps, 1);
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
	public void tag(Scanner scanner, PrintStream ps, int maxResultsNumber) {
		String line;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			String sentString = tagAndFormat(line, maxResultsNumber);
			ps.println(sentString);

		}

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
