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
package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.cli.configuration.Configuration;
import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemmaUtil;
import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.ModelData;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Vector;

public class RawModel extends Model<String, Integer> {
	@SuppressWarnings("unused")
	private RawModel() {
		this(2, 2, 10, 10);
	}

	private static final long serialVersionUID = 8860320542881381547L;

	protected RawModelData rawModeldata;

	public Statistics getLastStat() {
		return rawModeldata.stat;
	}

	public RawModelData getRawData() {
		return rawModeldata;
	}

	// public List<Double> getLemmaLambdas() {
	// return rawModeldata.lemmaLambdas;
	// }

	// @Deprecated
	// private RawModel(int taggingOrder, int emissionOrder, int suffixLength,
	// int rareFrequency, ILexicon<String, Integer> standardTokensLexicon,
	// ILexicon<String, Integer> specTokensLexicon,
	// IVocabulary<String, Integer> tagVocabulary) {
	// super(taggingOrder, emissionOrder, suffixLength, rareFrequency,
	// standardTokensLexicon, specTokensLexicon, tagVocabulary);
	//
	// rawModeldata = new RawModelData();
	// rawModeldata.stat = new Statistics();
	//
	// rawModeldata.tagNGramModel = new NGramModel<Integer>(taggingOrder + 1);
	// rawModeldata.stdEmissionNGramModel = new NGramModel<String>(
	// emissionOrder + 1);
	// rawModeldata.specEmissionNGramModel = new NGramModel<String>(2);
	// rawModeldata.lemmaTree = new HashLemmaTree(100);
	// rawModeldata.lemmaUnigramModel = new LemmaUnigramModel<String>();
	// rawModeldata.combiner = new LogLinearCombiner();
	//
	// }

	public RawModel(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency) {
		this(ModelData.create(taggingOrder, emissionOrder, suffixLength,
				rareFrequency));
	}

	public RawModel(ModelData<String, Integer> modelData) {
		super(modelData);
		this.rawModeldata = new RawModelData(modelData.taggingOrder,
				modelData.emissionOrder);
	}

	public void train(IDocument document) {
		this.rawModeldata.eosTag = data.tagVocabulary.addElement(getEOSTag());
		for (ISentence sentence : document.getSentences()) {
			ISentence mySentence = new Sentence(sentence);
			addSentenceMarkers(mySentence);
			addSentence(mySentence);
		}
		buildSuffixTrees();
		rawModeldata.combiner.calculateParameters(document, rawModeldata, data);
	}

	protected void addSentenceMarkers(ISentence mySentence) {
		mySentence.add(0, new Token(ModelData.BOS_TOKEN, ModelData.BOS_TAG));
	}

	protected void addSentence(ISentence sentence) {
		rawModeldata.stat.incrementSentenceCount();

		ISpecTokenMatcher specMatcher = new SpecTokenMatcher();
		Vector<Integer> tags = new Vector<Integer>();

		for (int j = sentence.size() - 1; j >= 0; --j) {
			Integer tagID = data.tagVocabulary.addElement(sentence.get(j)
					.getTag());
			tags.add(tagID);
		}
		Collections.reverse(tags);
		// add EOS tag to the model
		rawModeldata.tagNGramModel.addWord(tags, rawModeldata.eosTag);

		for (int i = sentence.size() - 1; i >= 0; --i) {
			IToken token = sentence.get(i);
			if (!token.getToken().equals(ModelData.BOS_TOKEN)) {
				token = Util.simplifyLemma(token);
			}
			String word = token.getToken();
			String lemma = token.getStem();
			String tagStr = token.getTag();
			Integer tag = tags.get(i);
			// TEST: creating a trie from lemmas
			List<Integer> context = tags.subList(0, i + 1);
			List<Integer> prevTags = context.subList(0, context.size() - 1);

			if (!(word.equals(Model.getBOSToken()) || word.equals(Model
					.getEOSToken()))) {
				LemmaUtil.storeLemma(word, lemma, tag, tagStr, rawModeldata, data);

				rawModeldata.tagNGramModel.addWord(prevTags, tag); // tagTransitionModel

				rawModeldata.stat.incrementTokenCount();

				data.standardTokensLexicon.addToken(word, tag);
				rawModeldata.stdEmissionNGramModel.addWord(context, word); // standardEmissionModel

				String specName;
				if ((specName = specMatcher.matchLexicalElement(word)) != null) {
					rawModeldata.specEmissionNGramModel.addWord(context,
							specName); // specTokensEmissionModel
					// this is how it should have been used:
					data.specTokensLexicon.addToken(specName, tag);
					// this is how it is used in HunPOS:
					// specTokensLexicon.addToken(word, tag);
				}
				// else {
				// standardTokensLexicon.addToken(word, tag);
				// stdEmissionNGramModel.addWord(context, word);
				// }
			}
		}

	}

	protected void buildSuffixTrees() {
		// if the model is changed suffix trees need to be rebuilt
		rawModeldata.lowerSuffixTree = new HashSuffixTree<Integer>(
				data.suffixLength);
		rawModeldata.upperSuffixTree = new HashSuffixTree<Integer>(
				data.suffixLength);

		for (Entry<String, HashMap<Integer, Integer>> entry : data.standardTokensLexicon) {

			String word = entry.getKey();
			int wordFreq = data.standardTokensLexicon.getWordCount(word);
			if (wordFreq <= data.rareFreqency) {
				String lowerWord = Util.toLower(word);
				boolean isLower = !Util.isUpper(lowerWord, word);
				for (Integer tag : entry.getValue().keySet()) {
					int wordTagFreq = data.standardTokensLexicon
							.getWordCountForTag(word, tag);
					if (isLower) {
						rawModeldata.lowerSuffixTree.addWord(lowerWord, tag,
								wordTagFreq);
						rawModeldata.stat
								.incrementLowerGuesserItems(wordTagFreq);
					} else {
						rawModeldata.upperSuffixTree.addWord(lowerWord, tag,
								wordTagFreq);
						rawModeldata.stat
								.incrementUpperGuesserItems(wordTagFreq);
					}

				}
			}
		}

	}

	protected Double smooth(Double val) {
		if (val == null)
			return Util.UNKOWN_VALUE;
		return val;
	}

	// public CompiledModel<String, Integer> compile(Configuration conf) {
	// data.tagVocabulary.storeMaximalElement();
	// IProbabilityModel<Integer, Integer> tagTransitionModel =
	// rawModeldata.tagNGramModel
	// .createProbabilityModel();
	// IProbabilityModel<Integer, String> standardEmissionModel =
	// rawModeldata.stdEmissionNGramModel
	// .createProbabilityModel();
	// IProbabilityModel<Integer, String> specTokensEmissionModel =
	// rawModeldata.specEmissionNGramModel
	// .createProbabilityModel();
	// Map<Integer, Double> aprioriProbs = rawModeldata.tagNGramModel
	// .getWordAprioriProbs();
	// Double theta = SuffixTree.calculateTheta(aprioriProbs);
	// ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser =
	// rawModeldata.lowerSuffixTree
	// .createGuesser(theta);
	// ISuffixGuesser<String, Integer> upperCaseSuffixGuesser =
	// rawModeldata.upperSuffixTree
	// .createGuesser(theta);
	// ISuffixGuesser<String, Pair<String, Integer>> lemmaSuffixGuesser =
	// rawModeldata.lemmaTree
	// .createGuesser(theta);
	//
	// addMappings(standardEmissionModel, specTokensEmissionModel,
	// tagTransitionModel, lowerCaseSuffixGuesser,
	// upperCaseSuffixGuesser, data.tagVocabulary,
	// conf.getTagMappings());
	//
	// CompiledModel<String, Integer> model = new CompiledModel<String,
	// Integer>(
	// data.taggingOrder, data.emissionOrder, data.suffixLength,
	// data.rareFreqency, tagTransitionModel, standardEmissionModel,
	// specTokensEmissionModel, lowerCaseSuffixGuesser,
	// upperCaseSuffixGuesser, lemmaSuffixGuesser,
	// rawModeldata.lemmaUnigramModel, data.standardTokensLexicon,
	// data.specTokensLexicon, data.tagVocabulary, aprioriProbs,
	// rawModeldata.lemmaLambdas);
	// return model;
	//
	// }

	public void setLemmaVariables(String lemmaTransformationType, int lemmaThreshold){
		if(data.lemmaTransformationType == null) {

			data.lemmaTransformationType = lemmaTransformationType;

			if (lemmaTransformationType.equals(LemmaUtil.generalized)) {
				// relevant only when GeneralizedLemmaTransformation class used
				data.lemmaThreshold = lemmaThreshold;
			}
		} else {
			if(!lemmaTransformationType.equals(data.lemmaTransformationType)){
				System.err.println("The requested lemmaTransformationType doesn't match the default from the model file. "
						+ data.lemmaTransformationType + " will used." );
			}
			if((lemmaThreshold != data.lemmaThreshold) && lemmaTransformationType.equals(LemmaUtil.generalized)){
				// relevant only when GeneralizedLemmaTransformation class used
				System.err.println("The requested lemmaThreshold doesn't match the default from the model file. "+
				data.lemmaThreshold + " will used.");
			}
		}
	}

	public CompiledModel<String, Integer> compile(Configuration conf, String lemmaTransformationType, int lemmaThreshold) {
		setLemmaVariables(lemmaTransformationType,lemmaThreshold);
		return compile(conf);
	}

	public CompiledModel<String, Integer> compile(Configuration conf) {
		data.tagVocabulary.storeMaximalElement();

		CompiledModelData<String, Integer> compiledModelData = RawModelData
				.compile(this.rawModeldata);

		Util.addMappings(compiledModelData, data.tagVocabulary,
				conf.getTagMappings());

		return new CompiledModel<String, Integer>(compiledModelData, this.data);
	}

}
