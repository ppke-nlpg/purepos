package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.AbstractLemmaTransformation;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemmaUtil;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.ModelData;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

public class LogLinearBiCombiner extends LogLinearCombiner {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2201616144744450790L;

	@Override
	public void calculateParameters(IDocument doc, RawModelData rawModeldata,
			ModelData<String, Integer> data) {
		Map<Integer, Double> aprioriProbs = rawModeldata.tagNGramModel
				.getWordAprioriProbs();
		Double theta = SuffixTree.calculateTheta(aprioriProbs);
		ISuffixGuesser<String, AbstractLemmaTransformation<Pair<String,Integer>>> lemmaSuffixGuesser = rawModeldata.lemmaSuffixTree
				.createGuesser(theta);
		Double lambdaS = 1.0, lambdaU = 1.0;
		if (lambdas != null && lambdas.size() > 1) {
			lambdaS = lambdas.get(0);
			lambdaU = lambdas.get(1);
		}
		lambdas = new ArrayList<Double>(2);
		for (ISentence sentence : doc.getSentences()) {
			for (IToken tok : sentence) {
				Map<IToken, Pair<AbstractLemmaTransformation<Pair<String,Integer>>, Double>> suffixProbs = LemmaUtil
						.batchConvert(lemmaSuffixGuesser
								.getTagLogProbabilities(tok.getToken()), tok
								.getToken(), data.tagVocabulary);

				Map<IToken, Double> uniProbs = new HashMap<IToken, Double>();
				for (IToken t : suffixProbs.keySet()) {
					Double uniscore = rawModeldata.lemmaUnigramModel
							.getLogProb(t.getStem());
					uniProbs.put(t, uniscore);
				}

				Map.Entry<IToken, Double> uniMax = Util.findMax(uniProbs);
				Pair<IToken, Double> suffixMax = Util.findMax2(suffixProbs);
				Double actUniProb = rawModeldata.lemmaUnigramModel
						.getLogProb(tok.getStem());
				Double actSuffProb;
				if (suffixProbs.containsKey(tok)) {
					actSuffProb = suffixProbs.get(tok).getValue();
				} else {
					actSuffProb = Util.UNKOWN_VALUE;
				}
				Double uniProp = actUniProb - uniMax.getValue();
				Double suffProp = actSuffProb - suffixMax.getValue();
				if (uniProp > suffProp) {
					lambdaU += uniProp - suffProp;
				} else if (suffProp > uniProp) {
					lambdaS += suffProp - uniProp;
				}
			}

		}

		double sum = lambdaU + lambdaS;
		lambdaU = lambdaU / sum;
		lambdaS = lambdaS / sum;
		lambdas.add(lambdaU);
		lambdas.add(lambdaS);
	}

	@Override
	public Double combine(IToken tok, AbstractLemmaTransformation<Pair<String,Integer>> t,
			CompiledModelData<String, Integer> compiledModelData,
			ModelData<String, Integer> modelData) {
		LemmaUnigramModel<String> unigramLemmaModel = compiledModelData.unigramLemmaModel;
		Double uniScore = unigramLemmaModel.getLogProb(tok.getStem());
		double suffScore = compiledModelData.lemmaGuesser.getTagLogProbability(
				tok.getToken(), t);
		Double suffixScore = smooth(suffScore);

		//TODO: this is where the lemma probabilities are combined

		Double uniLambda = lambdas.get(0);
		Double suffixLambda = lambdas.get(1);
		
		if(Util.CONFIGURATION !=null && Util.CONFIGURATION.getWeight()!=null) {
			suffixLambda = Util.CONFIGURATION.getWeight();
			uniLambda = 1- suffixLambda;
		}
		
		return uniScore * uniLambda + suffixScore * suffixLambda;
	}

}
