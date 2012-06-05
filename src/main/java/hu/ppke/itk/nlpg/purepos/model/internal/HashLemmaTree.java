package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.SuffixCoder;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class HashLemmaTree extends HashSuffixTree<Pair<String, Integer>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4680124440505966799L;

	private HashLemmaTree() {
		this(10);
	}

	public HashLemmaTree(int maxSuffixLength) {
		super(maxSuffixLength);
	}

	@Override
	public void addWord(String suffString, Pair<String, Integer> tag, int count) {
		increment(suffString, tag, count);
	}

	// TODO: the relative probabilities just left out, it should be calculated
	// in the model
	public List<IToken> getLemmas(String word,
			IVocabulary<String, Integer> vocab) {
		List<IToken> ret = new ArrayList<IToken>();
		String wordSuffix;
		for (int i = 0; i < word.length(); ++i) {
			wordSuffix = word.substring(word.length() - i);
			if (representation.containsKey(wordSuffix)) {
				Set<Pair<String, Integer>> codes = representation
						.get(wordSuffix).getLeft().keySet();
				for (Pair<String, Integer> code : codes) {
					ret.add(SuffixCoder.tokenForCode(code, word, vocab));
				}
			}
		}
		return ret;
	}
}
