package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MorphTagger extends POSTagger implements ITagger {

	public MorphTagger(Model<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, double sufTheta,
			int maxGuessedTags) {
		super(model, analyzer, logTheta, sufTheta, maxGuessedTags);

	}

	@Override
	public ISentence tagSentence(List<String> sentence) {
		ISentence taggedSentence = super.tagSentence(sentence);
		List<IToken> tmp = new ArrayList<IToken>();
		for (IToken t : taggedSentence) {
			IToken bestStemmedToken = findBestLemma(t);
			tmp.add(bestStemmedToken);
		}
		return new Sentence(tmp);
	}

	private IToken findBestLemma(IToken t) {
		List<IToken> stems = analyzer.analyze(t);

		if (Util.isEmpty(stems)) {
			// the guesser is used
			stems = model.getLemmaTree().getLemmas(t.getToken(),
					model.getTagVocabulary());
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
		IToken best = Collections.max(possibleStems, new Comparator<IToken>() {
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

		return best;
	}
}
