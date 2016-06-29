package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.lemma.ILemmaTransformation;
import hu.ppke.itk.nlpg.purepos.model.ModelData;

import java.util.ArrayList;

public class LogLinearMLCombiner extends LogLinearCombiner {

	private static final long serialVersionUID = 3954363116292749063L;

	@Override
	public void calculateParameters(IDocument doc, RawModelData rawModeldata,
			ModelData<String, Integer> data) {
		if (lambdas == null || lambdas.size() < 2) {
			lambdas = new ArrayList<Double>(2);
			lambdas.add(0.0);
			lambdas.add(0.1);
		}
	}

	@Override
	public Double combine(IToken tok, ILemmaTransformation<String, Integer> t,
			CompiledModelData<String, Integer> compiledModelData,
			ModelData<String, Integer> modelData) {
		LemmaUnigramModel<String> unigramLemmaModel = compiledModelData.unigramLemmaModel;
		Double uniScore = unigramLemmaModel.getLogProb(tok.getStem());
		Double suffixScore = smooth(compiledModelData.lemmaGuesser
				.getTagLogProbability(tok.getToken(), t));

		return uniScore * lambdas.get(0) + suffixScore * lambdas.get(1);
	}
}
