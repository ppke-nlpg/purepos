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
import hu.ppke.itk.nlpg.purepos.common.Global;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.decoder.StemFilter;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
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
	StemFilter stemFilter;

	public MorphTagger(CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, double sufTheta,
			int maxGuessedTags) {
		super(model, analyzer, logTheta, sufTheta, maxGuessedTags);
		stemFilter = Util.crateStemFilter();
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
		if (Global.analysisQueue.hasAnal(position)) {
			stems = Global.analysisQueue.getAnalysises(position);
		} else {
			stems = analyzer.analyze(t);
		}

		if (Util.isEmpty(stems)) {
			// the guesser is used
			Map<IToken, Integer> lemmaFreqs = model.getLemmaTree().getLemmas(
					t.getToken(), model.getTagVocabulary());
			stems = lemmaFreqs.keySet();
		}
		// matching tags
		List<IToken> possibleStems = new ArrayList<IToken>();
		for (IToken ct : stems) {
			if (t.getTag().equals(ct.getTag())) {
				possibleStems.add(new Token(ct.getToken(), ct.getStem(), ct
						.getTag()));
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
			best = Collections.max(possibleStems, new Comparator<IToken>() {
				public int count(IToken t) {
					// TODO: RESEARCH: cheat! - investigate
					int plus = 0;
					plus = t.getStem() == t.getToken() ? 1 : 0;
					return model.getStandardTokensLexicon().getWordCount(
							t.getStem())
							+ plus;
				}

				@Override
				public int compare(IToken o1, IToken o2) {
					return count(o1) - count(o2);

				}
			});
		}
		return best;
	}
}
