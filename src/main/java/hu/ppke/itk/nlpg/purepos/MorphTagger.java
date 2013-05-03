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

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.Globals;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemnmaTransformationUtil;
import hu.ppke.itk.nlpg.purepos.decoder.StemFilter;
import hu.ppke.itk.nlpg.purepos.model.ICombiner;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.LemmaUnigramModel;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * Tagger that performs full morphological disambiguation.
 * 
 * @author György Orosz
 * 
 */
public class MorphTagger extends POSTagger implements ITagger {
	protected final class LemmaComparator implements Comparator<IToken> {
		private final Map<IToken, Double> lemmaSuffixProbs;

		// List<Double> lambdas = model.getLemsmaLambdas();

		protected LemmaComparator(Map<IToken, Double> lemmaSuffixProbs) {
			this.lemmaSuffixProbs = lemmaSuffixProbs;
		}

		public Double suffixLogProb(IToken o1) {
			Double ret = this.lemmaSuffixProbs.get(o1);
			if (ret == null) {
				return Util.UNKOWN_VALUE;
			}
			return ret;
		}

		@Override
		public int compare(IToken t1, IToken t2) {
			LemmaUnigramModel<String> unigramLemmaModel = model
					.getUnigramLemmaModel();
			Double uniScore1 = unigramLemmaModel.getLogProb(t1.getStem());
			Double uniScore2 = unigramLemmaModel.getLogProb(t2.getStem());

			Double suffixScore1 = suffixLogProb(t1);
			Double suffixScore2 = suffixLogProb(t2);

			ICombiner combiner = model.getCombiner();
			Double finalScore1 = combiner.combine(uniScore1, suffixScore1);
			Double finalScore2 = combiner.combine(uniScore2, suffixScore2);

			return Double.compare(finalScore1, finalScore2);
		}

		// private Double combine(Double uniScore, Double suffixScore) {
		//
		// Double lambda1 = lambdas.get(0), lambda2 = lambdas.get(1);
		// return uniScore * lambda1 + suffixScore * lambda2;
		// }

	}

	// public int compare1(IToken o1, IToken o2) {
	// int c1 = unigramProb(o1);
	// int c2 = unigramProb(o2);
	// if (c1 > 0 || c2 > 0)
	// return c1 - c2;
	// else {
	// Double prob1 = suffixLogProb(o1);
	// Double prob2 = suffixLogProb(o2);
	// if (prob1 == null)
	// prob1 = UNKOWN_VALUE;
	// if (prob2 == null)
	// prob2 = UNKOWN_VALUE;
	// return Double.compare(prob1, prob2);
	// }
	//
	// }
	// }

	StemFilter stemFilter;

	public MorphTagger(CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, double sufTheta,
			int maxGuessedTags, boolean useBeamSearch) {
		super(model, analyzer, logTheta, sufTheta, maxGuessedTags,
				useBeamSearch);
		stemFilter = Util.createStemFilter();
	}

	@Override
	protected List<IToken> merge(List<String> sentence, List<Integer> tags) {
		List<IToken> res = super.merge(sentence, tags);
		// ISentence taggedSentence = super.tagSentence(sentence);
		List<IToken> tmp = new ArrayList<IToken>();
		int pos = 0;
		for (IToken t : res) {
			IToken bestStemmedToken = findBestLemma(t, pos);
			bestStemmedToken = new Token(bestStemmedToken.getToken(),
					bestStemmedToken.getStem().replace(" ", "_"),
					bestStemmedToken.getTag());
			tmp.add(bestStemmedToken);
			pos++;
		}
		return new Sentence(tmp);
	}

	private IToken findBestLemma(IToken t, int position) {
		Collection<IToken> stems;
		if (Globals.analysisQueue.hasAnal(position)) {
			stems = Globals.analysisQueue.getAnalysises(position);
		} else {
			stems = analyzer.analyze(t);
		}

		final Map<IToken, Double> lemmaSuffixProbs = LemnmaTransformationUtil
				.batchConvert(
						model.getLemmaGuesser().getTagLogProbabilities(
								t.getToken()), t.getToken(),
						model.getTagVocabulary());
		LemmaComparator lemmaComparator = new LemmaComparator(lemmaSuffixProbs);

		if (Util.isEmpty(stems)) {
			// the guesser is used

			stems = lemmaSuffixProbs.keySet();
		}
		// matching tags
		List<IToken> possibleStems = new ArrayList<IToken>();
		for (IToken ct : stems) {
			if (t.getTag().equals(ct.getTag())) {
				possibleStems.add(new Token(ct.getToken(), ct.getStem(), ct
						.getTag()));
				possibleStems.add(new Token(ct.getToken(), Util.toLower(ct
						.getStem()), ct.getTag()));
			}
		}

		if (Util.isEmpty(possibleStems)) {
			// error handling
			return new Token(t.getToken(), t.getToken(), t.getTag());
		}

		// most frequrent stem
		IToken best;
		if (possibleStems.size() == 1) {
			best = possibleStems.get(0);
		} else {
			if (stemFilter != null) {
				possibleStems = stemFilter.filterStem(possibleStems);
			}

			best = Collections.max(possibleStems, lemmaComparator);
		}
		return best;
	}
}
