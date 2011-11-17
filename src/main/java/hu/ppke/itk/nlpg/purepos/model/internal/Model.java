package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.model.INGramFrequencyModel;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;

import java.util.Map;

public class Model extends
		hu.ppke.itk.nlpg.purepos.model.Model<String, Integer> {

	protected Model(int taggingOrder, int emissionOrder,
			IProbabilityModel<Integer, Integer> tagTransitionModel,
			IProbabilityModel<Integer, String> standardEmissionModel,
			IProbabilityModel<Integer, String> specTokensEmissionModel,
			ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser,
			ISuffixGuesser<String, Integer> upperCaseSuffixGuesser) {
		this.taggingOrder = taggingOrder;
		this.emissionOrder = emissionOrder;
		this.tagTransitionModel = tagTransitionModel;
		this.standardEmissionModel = standardEmissionModel;
		this.specTokensEmissionModel = specTokensEmissionModel;
		this.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
		this.upperCaseSuffixGuesser = upperCaseSuffixGuesser;

		standardTokensLexicon = new Lexicon<String, Integer>();
		specTokensLexicon = new Lexicon<String, Integer>();
		tagVocabulary = new IntVocabulary<String>();

		tagVocabulary.addElement(EOS_TAG);
		tagVocabulary.addElement(BOS_TAG);
		eosIndex = tagVocabulary.getIndex(EOS_TAG);
		eosIndex = tagVocabulary.getIndex(BOS_TAG);
	}

	public static Model train(IDocument document, int tagOrder,
			int emissionOrder, int maxSuffixLength) {
		IProbabilityModel<Integer, Integer> tagTransitionModel;
		IProbabilityModel<Integer, String> standardEmissionModel;
		IProbabilityModel<Integer, String> specTokensEmissionModel;
		ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser;
		ISuffixGuesser<String, Integer> upperCaseSuffixGuesser;

		INGramFrequencyModel<Integer, Integer> tagNGramModel = new NGramModel<Integer>(
				tagOrder);
		INGramFrequencyModel<Integer, String> stdEmissionNGramModel = new NGramModel<String>(
				emissionOrder);
		INGramFrequencyModel<Integer, String> specEmissionNGramModel = new NGramModel<String>(
				emissionOrder);
		HashSuffixTree<Integer> lowerSuffixTree = new HashSuffixTree<Integer>(
				maxSuffixLength);
		HashSuffixTree<Integer> upperSuffixTree = new HashSuffixTree<Integer>(
				maxSuffixLength);

		for (ISentence sentence : document.getSentences()) {
			// add BOS
			for (IToken token : sentence) {

			}
			// add EOS
		}

		tagTransitionModel = tagNGramModel.createProbabilityModel();
		standardEmissionModel = stdEmissionNGramModel.createProbabilityModel();
		specTokensEmissionModel = specEmissionNGramModel
				.createProbabilityModel();
		Map<Integer, Double> aprioriProbs = tagTransitionModel
				.getWordAprioriProbs();

		lowerCaseSuffixGuesser = lowerSuffixTree.createGuesser(lowerSuffixTree
				.calculateTheta(aprioriProbs));

		upperCaseSuffixGuesser = upperSuffixTree.createGuesser(upperSuffixTree
				.calculateTheta(aprioriProbs));
		Model model = new Model(tagOrder, emissionOrder, tagTransitionModel,
				standardEmissionModel, specTokensEmissionModel,
				lowerCaseSuffixGuesser, upperCaseSuffixGuesser);
		return null;// model;
	}
	// public static
}
