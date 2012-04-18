package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.decoder.AbstractDecoder;
import hu.ppke.itk.nlpg.purepos.decoder.BeamSearch;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import com.google.common.base.Joiner;

public class POSTagger implements ITagger {
	IMorphologicalAnalyzer analyzer;
	protected final AbstractDecoder decoder;
	protected final Model<String, Integer> model;

	public POSTagger(final Model<String, Integer> model, double logTheta,
			double sufTheta, int maxGuessedTags) {
		this(model, new NullAnalyzer(), logTheta, sufTheta, maxGuessedTags);
	}

	public POSTagger(final Model<String, Integer> model,
			IMorphologicalAnalyzer analyzer, double logTheta, double sufTheta,
			int maxGuessedTags) {
		this.model = model;
		this.analyzer = analyzer;
		this.decoder = new BeamSearch(model, analyzer, logTheta, sufTheta,
				maxGuessedTags);
	}

	@Override
	public ISentence tagSentence(List<String> sentence) {
		List<Integer> tags = decoder.decode(sentence);

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
		return new Sentence(tokens);
	}

	@Override
	public ISentence tagSentence(String sentence) {
		if (sentence == null || sentence.trim().equals(""))
			return null;
		ISentence s = tagSentence(Arrays.asList(sentence.split("\\s")));
		return s;
	}

	@Override
	public void tag(Scanner scanner, PrintStream ps) {
		String line;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			ISentence s = tagSentence(line);
			ps.println(sentence2string(s));
		}
	}

	protected String sentence2string(ISentence s) {
		return Joiner.on(" ").join(s);
	}

}
