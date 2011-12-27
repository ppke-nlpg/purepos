package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.model.ILexicon;
import hu.ppke.itk.nlpg.purepos.model.INGramModel;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Model represneting a cropus wiht pos tags.
 * 
 * Containing n-gram based language models, and suffixguessers as well.
 * 
 * @author György Orosz
 * 
 */
public class POSTaggerModel extends Model<String, Integer> {
	protected static Logger logger = Logger.getLogger(POSTaggerModel.class);

	protected static Statistics stat;

	public static Statistics getLastStat() {
		return stat;
	}

	protected POSTaggerModel(int taggingOrder, int emissionOrder,
			int suffixLength, int rareFrequency,
			IProbabilityModel<Integer, Integer> tagTransitionModel,
			IProbabilityModel<Integer, String> standardEmissionModel,
			IProbabilityModel<Integer, String> specTokensEmissionModel,
			ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser,
			ISuffixGuesser<String, Integer> upperCaseSuffixGuesser,
			ILexicon<String, Integer> standardTokensLexicon,
			ILexicon<String, Integer> specTokensLexicon,
			IVocabulary<String, Integer> tagVocabulary,
			Map<Integer, Double> aprioriTagProbs) {
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
		this.aprioriTagProbs = aprioriTagProbs;

		// tagVocabulary.addElement(EOS_TAG);
		// tagVocabulary.addElement(BOS_TAG);
		eosIndex = tagVocabulary.addElement(EOS_TAG);
		bosIndex = tagVocabulary.addElement(BOS_TAG);
	}

	/**
	 * Trains a POS tagger on the givel corpus with the parameters
	 * 
	 * @param document
	 *            training corpus
	 * @param tagOrder
	 *            order of the tag Markov model
	 * @param emissionOrder
	 *            order of the emission Markov model
	 * @param maxSuffixLength
	 *            max length for building suffixguesser
	 * @param rareFrequency
	 *            words used for building the guesser having frequency below
	 *            this amount
	 * @return
	 */
	public static POSTaggerModel train(IDocument document, int tagOrder,
			int emissionOrder, int maxSuffixLength, int rareFrequency) {
		stat = new Statistics();
		// build n-gram models
		INGramModel<Integer, Integer> tagNGramModel = new NGramModel<Integer>(
				tagOrder + 1);
		INGramModel<Integer, String> stdEmissionNGramModel = new NGramModel<String>(
				emissionOrder + 1);
		// TODO: in HunPOS the order of spec emission model is always 2
		INGramModel<Integer, String> specEmissionNGramModel = new NGramModel<String>(
				emissionOrder + 1);
		ILexicon<String, Integer> standardTokensLexicon = new Lexicon<String, Integer>();
		ILexicon<String, Integer> specTokensLexicon = new Lexicon<String, Integer>();
		IVocabulary<String, Integer> tagVocabulary = new IntVocabulary<String>();
		for (ISentence sentence : document.getSentences()) {
			ISentence mySentence = new Sentence(sentence);
			addSentenceMarkers(mySentence, tagOrder);
			// adding a sentence to the model
			addSentence(mySentence, tagNGramModel, stdEmissionNGramModel,
					specEmissionNGramModel, standardTokensLexicon,
					specTokensLexicon, tagVocabulary);
		}
		logger.debug("tagTransitionLamda:");
		IProbabilityModel<Integer, Integer> tagTransitionModel = tagNGramModel
				.createProbabilityModel();
		logger.debug("stdEmissionLamda:");
		IProbabilityModel<Integer, String> standardEmissionModel = stdEmissionNGramModel
				.createProbabilityModel();
		logger.debug("specEmissionLamda:");
		IProbabilityModel<Integer, String> specTokensEmissionModel = specEmissionNGramModel
				.createProbabilityModel();

		// build suffix guessers
		HashSuffixTree<Integer> lowerSuffixTree = new HashSuffixTree<Integer>(
				maxSuffixLength);
		HashSuffixTree<Integer> upperSuffixTree = new HashSuffixTree<Integer>(
				maxSuffixLength);
		buildSuffixTrees(standardTokensLexicon, rareFrequency, lowerSuffixTree,
				upperSuffixTree);
		Map<Integer, Double> aprioriProbs = tagNGramModel.getWordAprioriProbs();
		Double theta = SuffixTree.calculateTheta(aprioriProbs);
		stat.setTheta(theta);
		ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser = lowerSuffixTree
				.createGuesser(theta);
		ISuffixGuesser<String, Integer> upperCaseSuffixGuesser = upperSuffixTree
				.createGuesser(theta);

		// System.out.println(((NGramModel<String>) stdEmissionNGramModel)
		// .getReprString());
		// create the model
		if (logger.isTraceEnabled()) {
			logger.trace("tag vocabulary: " + tagVocabulary);
			logger.trace("upper guesser: " + upperCaseSuffixGuesser);
			logger.trace("lower guesser: " + lowerCaseSuffixGuesser);
			logger.trace("tagTransModel:\n"
					+ ((ProbModel<Integer>) tagTransitionModel).getReprString());
			logger.trace("seenTokModel:\n"
					+ ((ProbModel<String>) standardEmissionModel)
							.getReprString());
			logger.trace("specTokModel:\n"
					+ ((ProbModel<String>) specTokensEmissionModel)
							.getReprString());
		}
		POSTaggerModel model = new POSTaggerModel(tagOrder, emissionOrder,
				maxSuffixLength, rareFrequency, tagTransitionModel,
				standardEmissionModel, specTokensEmissionModel,
				lowerCaseSuffixGuesser, upperCaseSuffixGuesser,
				standardTokensLexicon, specTokensLexicon, tagVocabulary,
				aprioriProbs);
		return model;
	}

	protected static void addSentenceMarkers(ISentence mySentence, int tagOrder) {
		// TODO: its interesting that despite of using n-gram models we only add
		// one BOS
		// TODO: check how does training works in Hunpos with EOS
		// mySentence.add(new Token(EOS_TOKEN, EOS_TAG));
		// for (int i = 0; i < tagOrder; ++i) {
		mySentence.add(0, new Token(BOS_TOKEN, BOS_TAG));
		// }
	}

	protected static void buildSuffixTrees(
			ILexicon<String, Integer> standardTokensLexicon, int rareFreq,
			HashSuffixTree<Integer> lowerSuffixTree,
			HashSuffixTree<Integer> upperSuffixTree) {
		// TODO is integers are uppercase? - HunPOS: yes
		for (Entry<String, HashMap<Integer, Integer>> entry : standardTokensLexicon) {

			String word = entry.getKey();
			int wordFreq = standardTokensLexicon.getWordCount(word);
			if (wordFreq <= rareFreq) {
				// TODO: it is not really efficient
				String lowerWord = Util.toLower(word);
				boolean isLower = !Util.isUpper(word);
				for (Integer tag : entry.getValue().keySet()) {
					int wordTagFreq = standardTokensLexicon.getWordCountForTag(
							word, tag);
					if (isLower) {
						logger.debug("Lower: " + word + " " + wordTagFreq);
						lowerSuffixTree.addWord(lowerWord, tag, wordTagFreq);
						stat.incrementLowerGuesserItems(wordTagFreq);
					} else {
						logger.debug("Upper: " + word + " " + wordTagFreq);
						upperSuffixTree.addWord(lowerWord, tag, wordTagFreq);
						stat.incrementUpperGuesserItems(wordTagFreq);
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
		stat.incrementSentenceCount();
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
			List<Integer> context = tags.subList(0, i + 1);
			tagNGramModel.addWord(context.subList(0, context.size() - 1), tag);

			if (!(word.equals(Model.getBOSToken()) || word.equals(Model
					.getEOSToken()))) {
				stat.incrementTokenCount();
				// logger.trace("token is added:" + word);
				standardTokensLexicon.addToken(word, tag);
				stdEmissionNGramModel.addWord(context, word);

				String specName;
				if ((specName = specMatcher.matchLexicalElement(word)) != null) {
					specEmissionNGramModel.addWord(context, specName);
					specTokensLexicon.addToken(specName, tag);
				}
			}
		}

	}
}
