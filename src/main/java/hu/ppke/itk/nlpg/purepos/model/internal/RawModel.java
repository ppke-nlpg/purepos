package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.internal.Sentence;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.common.SuffixCoder;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.model.ILexicon;
import hu.ppke.itk.nlpg.purepos.model.INGramModel;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

public class RawModel extends Model<String, Integer> {

	private static final long serialVersionUID = 8860320542881381547L;

	protected INGramModel<Integer, Integer> tagNGramModel;
	protected INGramModel<Integer, String> stdEmissionNGramModel;
	protected INGramModel<Integer, String> specEmissionNGramModel;
	protected HashLemmaTree lemmaTree;
	protected Integer eosTag;

	HashSuffixTree<Integer> lowerSuffixTree;
	HashSuffixTree<Integer> upperSuffixTree;

	protected RawModel(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency, ILexicon<String, Integer> standardTokensLexicon,
			ILexicon<String, Integer> specTokensLexicon,
			IVocabulary<String, Integer> tagVocabulary) {
		super(taggingOrder, emissionOrder, suffixLength, rareFrequency,
				standardTokensLexicon, specTokensLexicon, tagVocabulary);

		tagNGramModel = new NGramModel<Integer>(taggingOrder + 1);
		stdEmissionNGramModel = new NGramModel<String>(emissionOrder + 1);
		specEmissionNGramModel = new NGramModel<String>(2);
		lemmaTree = new HashLemmaTree(100);

	}

	public RawModel(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency) {
		this(taggingOrder, emissionOrder, suffixLength, rareFrequency,
				new Lexicon<String, Integer>(), new Lexicon<String, Integer>(),
				new IntVocabulary<String>());
	}

	public void train(IDocument document) {
		this.eosTag = tagVocabulary.addElement(getEOSTag());
		for (ISentence sentence : document.getSentences()) {
			ISentence mySentence = new Sentence(sentence);
			addSentenceMarkers(mySentence);
			addSentence(mySentence);
		}
		buildSuffixTrees();
	}

	protected void addSentenceMarkers(ISentence mySentence) {
		mySentence.add(0, new Token(BOS_TOKEN, BOS_TAG));
	}

	protected void addSentence(ISentence sentence) {
		stat.incrementSentenceCount();

		ISpecTokenMatcher specMatcher = new SpecTokenMatcher();
		Vector<Integer> tags = new Vector<Integer>();

		for (int j = sentence.size() - 1; j >= 0; --j) {
			Integer tagID = tagVocabulary.addElement(sentence.get(j).getTag());
			tags.add(tagID);
		}
		Collections.reverse(tags);
		// add EOS tag to the model
		tagNGramModel.addWord(tags, eosTag);

		for (int i = sentence.size() - 1; i >= 0; --i) {
			String word = sentence.get(i).getToken();
			Integer tag = tags.get(i);
			// TEST: creating a trie from lemmas
			List<Integer> context = tags.subList(0, i + 1);
			List<Integer> prevTags = context.subList(0, context.size() - 1);
			if (!(word.equals(Model.getBOSToken()) || word.equals(Model
					.getEOSToken()))) {
				SuffixCoder.addToken(word, sentence.get(i).getStem(), tag,
						lemmaTree, 1);
				tagNGramModel.addWord(prevTags, tag);

				stat.incrementTokenCount();

				standardTokensLexicon.addToken(word, tag);
				stdEmissionNGramModel.addWord(context, word);

				String specName;
				if ((specName = specMatcher.matchLexicalElement(word)) != null) {
					specEmissionNGramModel.addWord(context, specName);
					// this is how it should have been used:
					specTokensLexicon.addToken(specName, tag);
					// this is how it is used in HunPOS:
					// specTokensLexicon.addToken(word, tag);
				}
			}
		}

	}

	protected void buildSuffixTrees() {
		// if the model is changed suffix trees need to be rebuilt
		lowerSuffixTree = new HashSuffixTree<Integer>(suffixLength);
		upperSuffixTree = new HashSuffixTree<Integer>(suffixLength);

		for (Entry<String, HashMap<Integer, Integer>> entry : standardTokensLexicon) {

			String word = entry.getKey();
			int wordFreq = standardTokensLexicon.getWordCount(word);
			if (wordFreq <= rareFreqency) {
				String lowerWord = Util.toLower(word);
				boolean isLower = !Util.isUpper(lowerWord, word);
				for (Integer tag : entry.getValue().keySet()) {
					int wordTagFreq = standardTokensLexicon.getWordCountForTag(
							word, tag);
					if (isLower) {
						lowerSuffixTree.addWord(lowerWord, tag, wordTagFreq);
						stat.incrementLowerGuesserItems(wordTagFreq);
					} else {
						upperSuffixTree.addWord(lowerWord, tag, wordTagFreq);
						stat.incrementUpperGuesserItems(wordTagFreq);
					}

				}
			}
		}

	}

	public CompiledModel<String, Integer> compile() {
		tagVocabulary.storeMaximalElement();
		IProbabilityModel<Integer, Integer> tagTransitionModel = tagNGramModel
				.createProbabilityModel();
		IProbabilityModel<Integer, String> standardEmissionModel = stdEmissionNGramModel
				.createProbabilityModel();
		IProbabilityModel<Integer, String> specTokensEmissionModel = specEmissionNGramModel
				.createProbabilityModel();
		Map<Integer, Double> aprioriProbs = tagNGramModel.getWordAprioriProbs();
		Double theta = SuffixTree.calculateTheta(aprioriProbs);
		ISuffixGuesser<String, Integer> lowerCaseSuffixGuesser = lowerSuffixTree
				.createGuesser(theta, aprioriProbs);
		ISuffixGuesser<String, Integer> upperCaseSuffixGuesser = upperSuffixTree
				.createGuesser(theta, aprioriProbs);

		CompiledModel<String, Integer> model = new CompiledModel<String, Integer>(
				taggingOrder, emissionOrder, suffixLength, rareFreqency,
				tagTransitionModel, standardEmissionModel,
				specTokensEmissionModel, lowerCaseSuffixGuesser,
				upperCaseSuffixGuesser, lemmaTree, standardTokensLexicon,
				specTokensLexicon, tagVocabulary, aprioriProbs);
		return model;

	}
}
