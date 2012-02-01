package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.decoder.FastDecoder;
import hu.ppke.itk.nlpg.purepos.decoder.MessedViterbi;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itp.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itp.nlpg.purepos.morphology.NullAnalyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class Tagger implements ITagger {
	protected final FastDecoder viterbi;
	protected final Model<String, Integer> model;

	public Tagger(final Model<String, Integer> model, double logTheta,
			int maxGuessedTags) {
		this(model, new NullAnalyzer(), logTheta, maxGuessedTags);
	}

	public Tagger(final Model<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, int maxGuessedTags) {
		this.model = model;
		this.viterbi = new MessedViterbi(model, analyzer, logTheta, maxGuessedTags);
	}

	@Override
	public ISentence tagSentence(List<String> sentence) {
		// System.out.println(sentence);

		List<Integer> tags = viterbi.decode(sentence);
		// System.out.println(tags);
		Iterator<Integer> tagsIt = tags.iterator();
		Iterator<String> tokensIt = sentence.iterator();
		List<IToken> tokens = new ArrayList<IToken>();
		IVocabulary<String, Integer> vocab = model.getTagVocabulary();
		while (tagsIt.hasNext() && tokensIt.hasNext()) {
			IToken t = new Token(tokensIt.next(), vocab.getWord(tagsIt.next()));
			tokens.add(t);
		}
		return new Sentence(tokens);
	}

	@Override
	public ISentence tagSentence(String sentence) {
		if (sentence == null || sentence.trim().equals(""))
			return null;
		return tagSentence(Arrays.asList(sentence.split("\\s")));
	}
}
