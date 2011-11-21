package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.model.ILexicon;
import hu.ppke.itk.nlpg.purepos.model.INGramModel;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class Model extends
		hu.ppke.itk.nlpg.purepos.model.Model<String, Integer> {

	protected Model(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency,
			IProbabilityModel<Integer, Integer> tagTransitionModel,
			IProbabilityModel<Integer, String> standardEmissionModel,
			IProbabilityModel<Integer, String> specTokensEmissionModel,
			ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser,
			ISuffixGuesser<String, Integer> upperCaseSuffixGuesser,
			ILexicon<String, Integer> standardTokensLexicon,
			ILexicon<String, Integer> specTokensLexicon,
			IVocabulary<String, Integer> tagVocabulary) {
		this.taggingOrder = taggingOrder;
		this.emissionOrder = emissionOrder;
		this.suffixLength = suffixLength;
		this.rareFreqency = rareFrequency;
		this.tagTransitionModel = tagTransitionModel;
		this.standardEmissionModel = standardEmissionModel;
		this.specTokensEmissionModel = specTokensEmissionModel;
		this.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
		this.upperCaseSuffixGuesser = upperCaseSuffixGuesser;

		this.standardTokensLexicon = standardTokensLexicon;
		this.specTokensLexicon = specTokensLexicon;
		this.tagVocabulary = tagVocabulary;

		// tagVocabulary.addElement(EOS_TAG);
		// tagVocabulary.addElement(BOS_TAG);
		eosIndex = tagVocabulary.getIndex(EOS_TAG);
		eosIndex = tagVocabulary.getIndex(BOS_TAG);
	}

	public static hu.ppke.itk.nlpg.purepos.model.Model<String, Integer> train(
			IDocument document, int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency) {
		IProbabilityModel<Integer, Integer> tagTransitionModel;
		IProbabilityModel<Integer, String> standardEmissionModel;
		IProbabilityModel<Integer, String> specTokensEmissionModel;
		ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser;
		ISuffixGuesser<String, Integer> upperCaseSuffixGuesser;

		INGramModel<Integer, Integer> tagNGramModel = new NGramModel<Integer>(
				tagOrder);
		INGramModel<Integer, String> stdEmissionNGramModel = new NGramModel<String>(
				emissionOrder);
		// TODO: in HunPOS the order of spec emission model is always 2
		INGramModel<Integer, String> specEmissionNGramModel = new NGramModel<String>(
				emissionOrder);
		HashSuffixTree<Integer> lowerSuffixTree = new HashSuffixTree<Integer>(
				maxSuffixLength);
		HashSuffixTree<Integer> upperSuffixTree = new HashSuffixTree<Integer>(
				maxSuffixLength);

		ILexicon<String, Integer> standardTokensLexicon = new Lexicon<String, Integer>();
		ILexicon<String, Integer> specTokensLexicon = new Lexicon<String, Integer>();
		IVocabulary<String, Integer> tagVocabulary = new IntVocabulary<String>();

		for (ISentence sentence : document.getSentences()) {
			ISentence mySentence = new Sentence(sentence);
			mySentence.add(new Token(EOS_TOKEN, EOS_TAG));
			mySentence.add(0, new Token(BOS_TOKEN, BOS_TAG));
			addSentence(mySentence, tagNGramModel, stdEmissionNGramModel,
					specEmissionNGramModel, standardTokensLexicon,
					specTokensLexicon, tagVocabulary);
		}

		tagTransitionModel = tagNGramModel.createProbabilityModel();
		standardEmissionModel = stdEmissionNGramModel.createProbabilityModel();
		specTokensEmissionModel = specEmissionNGramModel
				.createProbabilityModel();
		Map<Integer, Double> aprioriProbs = tagTransitionModel
				.getWordAprioriProbs();

		buildSuffixTrees(standardTokensLexicon, rareFrequency, lowerSuffixTree,
				upperSuffixTree);

		lowerCaseSuffixGuesser = lowerSuffixTree.createGuesser(lowerSuffixTree
				.calculateTheta(aprioriProbs));

		upperCaseSuffixGuesser = upperSuffixTree.createGuesser(upperSuffixTree
				.calculateTheta(aprioriProbs));
		hu.ppke.itk.nlpg.purepos.model.Model<String, Integer> model = new Model(
				tagOrder, emissionOrder, maxSuffixLength, rareFrequency,
				tagTransitionModel, standardEmissionModel,
				specTokensEmissionModel, lowerCaseSuffixGuesser,
				upperCaseSuffixGuesser, standardTokensLexicon,
				specTokensLexicon, tagVocabulary);
		return model;
	}

	protected static void buildSuffixTrees(
			ILexicon<String, Integer> standardTokensLexicon, int rareFreq,
			HashSuffixTree<Integer> lowerSuffixTree,
			HashSuffixTree<Integer> upperSuffixTree) {
		// TODO: test
		for (Entry<String, HashMap<Integer, Integer>> entry : standardTokensLexicon) {
			String word = entry.getKey();
			for (Entry<Integer, Integer> tagFreq : entry.getValue().entrySet()) {
				if (tagFreq.getValue() <= rareFreq) {
					String lowerWord = word.toLowerCase();
					boolean isLower = word.equals(lowerWord);
					if (isLower) {
						lowerSuffixTree.addWord(lowerWord, tagFreq.getKey(),
								rareFreq);
					} else {
						upperSuffixTree.addWord(lowerWord, tagFreq.getKey(),
								rareFreq);
					}

				}
			}
		}

	}

	protected static void addSentence(ISentence sentence,
			INGramModel<Integer, Integer> tagNGramModel,
			INGramModel<Integer, String> stdEmissionNGramModel,
			INGramModel<Integer, String> specEmissionNGramModel,
			ILexicon<String, Integer> standardTokensLexicon,
			ILexicon<String, Integer> specTokensLexicon,
			IVocabulary<String, Integer> tagVocabulary) {
		// sentence is random accessible
		ISpecTokenMatcher specMatcher = new SpecTokenMatcher();
		Vector<Integer> tags = new Vector<Integer>();
		for (int j = 0; j < sentence.size(); ++j) {
			Integer tagID = tagVocabulary.addElement(sentence.get(j).getTag());
			tags.add(tagID);
		}
		for (int i = sentence.size() - 1; i >= 0; --i) {
			String word = sentence.get(i).getToken();
			Integer tag = tags.get(i);

			standardTokensLexicon.addToken(word, tag);
			List<Integer> context = tags.subList(0, i + 1);
			tagNGramModel.addWord(context.subList(0, context.size() - 1), tag);
			stdEmissionNGramModel.addWord(context, word);
			if (specMatcher.matchLexicalElement(word) != null) {
				specEmissionNGramModel.addWord(context, word);
				specTokensLexicon.addToken(word, tag);
			}
		}

	}
}
