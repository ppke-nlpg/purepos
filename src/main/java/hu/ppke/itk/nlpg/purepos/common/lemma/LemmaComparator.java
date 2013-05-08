package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.model.ICombiner;
import hu.ppke.itk.nlpg.purepos.model.ModelData;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModelData;

import java.util.Comparator;

import org.apache.commons.lang3.tuple.Pair;

public class LemmaComparator implements
		Comparator<Pair<IToken, ILemmaTransformation<String, Integer>>> {

	/**
	 * 
	 */
	protected CompiledModelData<String, Integer> compiledModelData;
	protected ModelData<String, Integer> modelData;

	public LemmaComparator(
			CompiledModelData<String, Integer> compiledModelData,
			ModelData<String, Integer> modelData) {
		this.compiledModelData = compiledModelData;
		this.modelData = modelData;
	}

	@Override
	public int compare(Pair<IToken, ILemmaTransformation<String, Integer>> t1,
			Pair<IToken, ILemmaTransformation<String, Integer>> t2) {
		ICombiner combiner = compiledModelData.combiner;
		Double finalScore1 = combiner.combine(t1.getKey(), t1.getValue(),
				compiledModelData, modelData);
		Double finalScore2 = combiner.combine(t2.getKey(), t2.getValue(),
				compiledModelData, modelData);

		return Double.compare(finalScore1, finalScore2);
	}

	// private Double combine(Double uniScore, Double suffixScore) {
	//
	// Double lambda1 = lambdas.get(0), lambda2 = lambdas.get(1);
	// return uniScore * lambda1 + suffixScore * lambda2;
	// }

}