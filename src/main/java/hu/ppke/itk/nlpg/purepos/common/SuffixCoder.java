package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.HashLemmaTree;
import hu.ppke.itk.nlpg.purepos.model.internal.Vocabulary;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public class SuffixCoder {

	private static final int SHIFT = 100;

	public static Pair<String, Integer> addToken(String word, String stem,
			Integer tag, HashLemmaTree tree, int count) {
		int i;
		for (i = 0; i < word.length() && i < stem.length(); ++i) {
			if (word.charAt(i) != stem.charAt(i)) {
				break;
			}
		}
		String wordSuff = word.substring(i);
		int cutSize = wordSuff.length();
		String lemmaSuff = stem.substring(i);

		int code = SHIFT * tag + cutSize;
		Pair<String, Integer> ret = new ImmutablePair<String, Integer>(
				lemmaSuff, code);

		tree.addWord(wordSuff, ret, count);
		return ret;

	}

	public static Pair<String, Integer> addToken(IToken stemmedToken,
			Vocabulary<String, Integer> vocab, HashLemmaTree tree, int count) {
		String word = stemmedToken.getToken();
		String stem = stemmedToken.getStem();
		String tag = stemmedToken.getTag();
		return addToken(word, stem, vocab.getIndex(tag), tree, count);
	}

	public static IToken tokenForCode(Pair<String, Integer> code, String word,
			IVocabulary<String, Integer> vocab) {
		int tagCode = code.getRight() / SHIFT;
		int cutSize = code.getRight() % SHIFT;
		String add = code.getLeft();
		String tag = vocab.getWord(tagCode);
		String lemma = word.substring(0, word.length() - cutSize) + add;
		return new Token(word, lemma, tag);
	}
}
