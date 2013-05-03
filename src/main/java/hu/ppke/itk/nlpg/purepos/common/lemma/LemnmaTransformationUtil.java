package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.HashMap;
import java.util.Map;

public class LemnmaTransformationUtil {

	public static void addToken(String word, String stem, Integer tag,
			SuffixTree<String, ILemmaTransformation<String, Integer>> tree) {

		ILemmaTransformation<String, Integer> lemmaTrans = new SuffixLemmaTransformation(
				word, stem, tag);
		tree.addWord(word, lemmaTrans, 1, lemmaTrans.minimalCutLength());

	}

	public static Map<IToken, Double> batchConvert(
			Map<ILemmaTransformation<String, Integer>, Double> probMap,
			String word, IVocabulary<String, Integer> vocab) {

		Map<IToken, Double> ret = new HashMap<IToken, Double>();
		for (Map.Entry<ILemmaTransformation<String, Integer>, Double> entry : probMap
				.entrySet()) {
			IToken lemma = entry.getKey().convert(word, vocab);
			ret.put(lemma, entry.getValue());
		}

		return ret;

	}
}
