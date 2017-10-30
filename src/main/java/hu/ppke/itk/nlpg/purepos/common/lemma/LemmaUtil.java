package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.model.ICombiner;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.ModelData;
import hu.ppke.itk.nlpg.purepos.model.internal.AbstractRawModelData;
import hu.ppke.itk.nlpg.purepos.model.internal.LogLinearBiCombiner;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

public class LemmaUtil {

	public static String suffix = "suffix";
	public static String generalized = "generalized";

	public static Map<IToken, Pair<ILemmaTransformation<String, Integer>, Double>>
	batchConvert(
			Map<ILemmaTransformation<String, Integer>, Double> probMap,
			String word,
			IVocabulary<String, Integer> vocab)
	{

		Map<IToken, Pair<ILemmaTransformation<String, Integer>, Double>> ret = new HashMap<IToken, Pair<ILemmaTransformation<String, Integer>, Double>>();
		for (Map.Entry<ILemmaTransformation<String, Integer>, Double> entry : probMap
				.entrySet()) {
			IToken lemma = entry.getKey().convert(word, vocab);
			ret.put(lemma, Pair.of(entry.getKey(), entry.getValue()));
//			Pair<ILemmaTransformation<String, Integer>, Double> ent = ret.get(lemma);
//			if (ent == null) {
//				ret.put(lemma, Pair.of(entry.getKey(), entry.getValue()));
//			} else if (ent.getRight() < entry.getValue()) {
//				ret.put(lemma, Pair.of(entry.getKey(), entry.getValue()));
//			}

		}

		return ret;

	}

	public static ILemmaTransformation<String, Integer> defaultLemmaRepresentation(
			String word, String stem, Integer tag, ModelData<String, Integer> data) {
		if (data.lemmaTransformationType.equals(generalized)){
			//System.out.println("Using GeneralizedLemmaTransformation..."); //debug
			return new GeneralizedLemmaTransformation(word,stem,tag,data.lemmaThreshold);
		}
		//System.out.println("Using SuffixLemmaTransformation..."); //debug
		return new SuffixLemmaTransformation(word, stem, tag);
	}

	public static ILemmaTransformation<String, Integer> defaultLemmaRepresentation(
			IToken tok,
			ModelData<String, Integer> data
	) {


		Integer t = data.tagVocabulary.getIndex(tok.getTag());
		return defaultLemmaRepresentation(tok.getToken(), tok.getStem(), t , data);


	}

	public static ICombiner defaultCombiner() {
		return new LogLinearBiCombiner();
	}

	public static void storeLemma(String word, String lemma, Integer tag,
			String tagString, AbstractRawModelData<String, Integer> rawModelData,ModelData<String, Integer> data ) {

		rawModelData.lemmaUnigramModel.increment(lemma);

		int count = 1;
		ILemmaTransformation<String, Integer> lemmaTrans = defaultLemmaRepresentation(
				word, lemma, tag, data);
		rawModelData.lemmaSuffixTree.addWord(word, lemmaTrans, count,
				lemmaTrans.minimalCutLength());

		// rawModelData.lemmaFreqTree.addWord(lemma, mainPosTag(tagString),
		// count);

	}

	protected static Pattern mainPosPat = Pattern
			.compile("\\[([^.\\]]*)[.\\]]");

	public static String mainPosTag(String tag) {
		Matcher matcher = mainPosPat.matcher(tag);
		matcher.find();
		try {
			return matcher.group(1);
		} catch (Exception e) {
			System.err.println(tag);
			return null;
		}

	}
}
