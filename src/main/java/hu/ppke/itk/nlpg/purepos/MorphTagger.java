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
	IMorphologicalAnalyzer analyzer;

	public MorphTagger(Model<String, Integer> model, double logTheta,
			double sufTheta, int maxGuessedTags, IMorphologicalAnalyzer analyzer) {
		super(model, analyzer, logTheta, sufTheta, maxGuessedTags);
		this.analyzer = analyzer;
	}

	@Override
	public ISentence tagSentence(List<String> sentence) {
		ISentence taggedSentence = super.tagSentence(sentence);
		List<IToken> tmp = new ArrayList<IToken>();
		for (IToken t : taggedSentence) {
			IToken bestStemmedToken = findBest(t);
			tmp.add(bestStemmedToken);
		}
		return new Sentence(tmp);
	}

	private IToken findBest(IToken t) {
		List<IToken> stemmedTokens = analyzer.analyze(t);
		if (stemmedTokens == null || stemmedTokens.size() == 0)
			return new Token(t.getToken(), t.getToken(), t.getTag());

		List<IToken> filteredStemmed = new ArrayList<IToken>();
		for (IToken ct : stemmedTokens) {
			if (t.getTag().equals(ct.getTag())) {
				filteredStemmed.add(ct);
			}
		}
		if (filteredStemmed == null || filteredStemmed.size() == 0)
			return t;

		IToken best = Collections.max(filteredStemmed,
				new Comparator<IToken>() {

					@Override
					public int compare(IToken o1, IToken o2) {
						return model.getStandardTokensLexicon().getWordCount(
								o1.getStem())
								- model.getStandardTokensLexicon()
										.getWordCount(o2.getStem());

					}
				});

		return best;
	}
}
