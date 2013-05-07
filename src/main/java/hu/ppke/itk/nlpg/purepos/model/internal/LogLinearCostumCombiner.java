package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.ILemmaTransformation;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemnmaTransformationUtil;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.ModelData;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LogLinearCostumCombiner extends LogLinearCombiner {

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
		ISuffixGuesser<String, ILemmaTransformation<String, Integer>> lemmaSuffixGuesser = rawModeldata.lemmaTree
				.createGuesser(theta);
		lambdas = new ArrayList<Double>(2);
		Double lambdaS = 1.0, lambdaU = 1.0;
		for (ISentence sentence : doc.getSentences()) {
			for (IToken tok : sentence) {
				Map<IToken, Double> suffixProbs = LemnmaTransformationUtil
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
				Map.Entry<IToken, Double> suffixMax = Util.findMax(suffixProbs);
				Double actUniProb = rawModeldata.lemmaUnigramModel
						.getLogProb(tok.getStem());
				// Pair<String, Integer> lemmaCode = SuffixCoder.decode(tok,
				// data.tagVocabulary);
				Double actSuffProb;
				if (suffixProbs.containsKey(tok)) {
					actSuffProb = suffixProbs.get(tok);
				} else {
					actSuffProb = Util.UNKOWN_VALUE;
				}
				Double uniProp = actUniProb - uniMax.getValue(), suffProp = actSuffProb
						- suffixMax.getValue();
				if (uniProp > suffProp) {
					// lambdaU += suffProp;// uniProp - suffProp;
					lambdaU += uniProp - suffProp;
				} else if (suffProp > uniProp) {
					// lambdaS += uniProp; // suffProp - uniProp;
					lambdaS += suffProp - uniProp;
				}

			}

		}
		double sum = lambdaU + lambdaS;
		lambdaU = lambdaU / sum;
		lambdaS = lambdaS / sum;
		lambdas.add(lambdaU);
		lambdas.add(lambdaS);
		// return lambdas;
	}

}
