package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.ILemmaTransformation;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemmaUtil;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.ModelData;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class LogLinearTriCombiner extends LogLinearCombiner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4264362007575382294L;

	@Override
	public void calculateParameters(IDocument doc, RawModelData rawModeldata,
			ModelData<String, Integer> data) {
		Map<Integer, Double> aprioriProbs = rawModeldata.tagNGramModel
				.getWordAprioriProbs();
		Double theta = SuffixTree.calculateTheta(aprioriProbs);
		ISuffixGuesser<String, ILemmaTransformation<String, Integer>> lemmaSuffixGuesser = rawModeldata.lemmaSuffixTree
				.createGuesser(theta);
		ISuffixGuesser<String, String> lemmaProb = rawModeldata.lemmaFreqTree
				.createGuesser(theta);
		LemmaUnigramModel<String> lemmaUnigramModel = rawModeldata.lemmaUnigramModel;

		Double lambdaS = 1.0, lambdaU = 1.0, lambdaL = 1.0;
		if (lambdas != null && lambdas.size() > 1) {
			lambdaS = lambdas.get(0);
			lambdaU = lambdas.get(1);
			lambdaL = lambdas.get(2);
		}
		lambdas = new ArrayList<Double>(3);

		for (ISentence sentence : doc.getSentences()) {
			for (IToken tok : sentence) {
				Map<IToken, Pair<ILemmaTransformation<String, Integer>, Double>> suffixProbs = LemmaUtil
						.batchConvert(lemmaSuffixGuesser
								.getTagLogProbabilities(tok.getToken()), tok
								.getToken(), data.tagVocabulary);

				Map<IToken, Double> uniProbs = new HashMap<IToken, Double>();

				for (IToken t : suffixProbs.keySet()) {
					Double uniscore = lemmaUnigramModel.getLogProb(t.getStem());
					uniProbs.put(t, uniscore);
				}

				Map<IToken, Double> lemmaProbs = new HashMap<IToken, Double>();
				for (IToken t : suffixProbs.keySet()) {
					// if (t.getTag().equals(tok.getTag())) {
					Double lemmaScore = lemmaProb.getTagLogProbability(
							t.getStem(), LemmaUtil.mainPosTag(t.getTag()));
					lemmaProbs.put(t, lemmaScore);
					// }
				}

				Map.Entry<IToken, Double> uniMax = Util.findMax(uniProbs);
				Pair<IToken, Double> suffixMax = Util.findMax2(suffixProbs);
				Map.Entry<IToken, Double> lemmaMax = Util.findMax(lemmaProbs);
				Double actUniProb = lemmaUnigramModel.getLogProb(tok.getStem());
				Double actLemmaProb = lemmaProb.getTagLogProbability(
						tok.getStem(), LemmaUtil.mainPosTag(tok.getTag()));
				// Pair<String, Integer> lemmaCode = SuffixCoder.decode(tok,
				// data.tagVocabulary);
				Double actSuffProb;
				if (suffixProbs.containsKey(tok)) {
					actSuffProb = suffixProbs.get(tok).getValue();
				} else {
					actSuffProb = Util.UNKOWN_VALUE;
				}
				Double uniProp = actUniProb - uniMax.getValue(), suffProp = actSuffProb
						- suffixMax.getValue(), lemmaProp = actLemmaProb
						- lemmaMax.getValue();
				if (uniProp > suffProp && uniProp > lemmaProp) {
					lambdaU += uniProp;
				} else if (suffProp > uniProp && suffProp > lemmaProp) {
					lambdaS += suffProp;
				} else if (lemmaProp > uniProp && lemmaProp > suffProp) {
					lambdaL += lemmaProp;// - uniProp;
				}

			}

		}
		double sum = lambdaU + lambdaS + lambdaL;
		lambdaU = lambdaU / sum;
		lambdaS = lambdaS / sum;
		lambdaL = lambdaL / sum;
		lambdas.add(lambdaU);
		lambdas.add(lambdaS);
		lambdas.add(lambdaL);
		// return lambdas;
	}

	@Override
	public Double combine(IToken tok, ILemmaTransformation<String, Integer> t,
			CompiledModelData<String, Integer> compiledModelData,
			ModelData<String, Integer> modelData) {
		LemmaUnigramModel<String> unigramLemmaModel = compiledModelData.unigramLemmaModel;
		Double uniScore = unigramLemmaModel.getLogProb(tok.getStem());
		Double suffixScore = smooth(compiledModelData.lemmaGuesser
				.getTagLogProbability(tok.getToken(), t));
		Double lemmaProb = compiledModelData.suffixLemmaModel
				.getTagLogProbability(tok.getStem(),
						LemmaUtil.mainPosTag(tok.getTag()));

		return uniScore * lambdas.get(0) + suffixScore * lambdas.get(1)
				+ lemmaProb * lambdas.get(2);
	}
}
