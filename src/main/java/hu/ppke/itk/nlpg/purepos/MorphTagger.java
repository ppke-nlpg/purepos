package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itp.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MorphTagger extends Tagger implements ITagger {

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
		if (stems == null || stems.size() == 0) {
			// TODO: we need a guesser here
			return new Token(t.getToken(), t.getToken(), t.getTag());
		}

		List<IToken> possibleStems = new ArrayList<IToken>();
		for (IToken ct : stems) {
			if (t.getTag().equals(ct.getTag())) {
				possibleStems.add(new Token(ct.getToken(), ct.getStem(), ct
						.getTag()));
			}
		}
		if (possibleStems == null || possibleStems.size() == 0) {
			// error handling
			return new Token(t.getToken(), t.getToken(), t.getTag());
		}

		// most frequrent stem
		IToken best = Collections.max(possibleStems, new Comparator<IToken>() {

			@Override
			public int compare(IToken o1, IToken o2) {
				return model.getStandardTokensLexicon().getWordCount(
						o1.getStem())
						- model.getStandardTokensLexicon().getWordCount(
								o2.getStem());

			}
		});

		return best;
	}
}
