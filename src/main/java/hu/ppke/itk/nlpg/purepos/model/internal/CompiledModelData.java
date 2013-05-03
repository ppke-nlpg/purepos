package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.common.lemma.ILemmaTransformation;
import hu.ppke.itk.nlpg.purepos.model.ICombiner;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.Map;

public class CompiledModelData<W, T extends Comparable<T>> {
	public LemmaUnigramModel<W> unigramLemmaModel;
	public ICombiner combiner;
	public ISuffixGuesser<W, ILemmaTransformation<W, Integer>> lemmaTree;

	public IProbabilityModel<T, T> tagTransitionModel;
	public IProbabilityModel<T, W> standardEmissionModel;
	public IProbabilityModel<T, W> specTokensEmissionModel;
	public ISuffixGuesser<W, T> lowerCaseSuffixGuesser;
	public ISuffixGuesser<W, T> upperCaseSuffixGuesser;
	public Map<T, Double> aprioriTagProbs;

	public static CompiledModelData<String, Integer> compile(
			RawModelData rawModeldata) {
		CompiledModelData<String, Integer> ret = new CompiledModelData<String, Integer>();
		ret.unigramLemmaModel = rawModeldata.lemmaUnigramModel;

		ret.tagTransitionModel = rawModeldata.tagNGramModel
				.createProbabilityModel();
		ret.standardEmissionModel = rawModeldata.stdEmissionNGramModel
				.createProbabilityModel();
		ret.specTokensEmissionModel = rawModeldata.specEmissionNGramModel
				.createProbabilityModel();
		ret.aprioriTagProbs = rawModeldata.tagNGramModel.getWordAprioriProbs();
		Double theta = SuffixTree.calculateTheta(ret.aprioriTagProbs);
		ret.lowerCaseSuffixGuesser = rawModeldata.lowerSuffixTree
				.createGuesser(theta);
		ret.upperCaseSuffixGuesser = rawModeldata.upperSuffixTree
				.createGuesser(theta);
		ret.lemmaTree = rawModeldata.lemmaTree.createGuesser(theta);
		ret.combiner = rawModeldata.combiner;

		return ret;
	}

	@Deprecated
	public CompiledModelData(LemmaUnigramModel<W> unigramLemmaModel,
			ICombiner combiner, IProbabilityModel<T, T> tagTransitionModel,
			IProbabilityModel<T, W> standardEmissionModel,
			IProbabilityModel<T, W> specTokensEmissionModel,
			ISuffixGuesser<W, T> lowerCaseSuffixGuesser,
			ISuffixGuesser<W, T> upperCaseSuffixGuesser,
			Map<T, Double> aprioriTagProbs,
			ISuffixGuesser<W, ILemmaTransformation<W, Integer>> lemmaTree) {
		this.unigramLemmaModel = unigramLemmaModel;
		this.combiner = combiner;
		this.tagTransitionModel = tagTransitionModel;
		this.standardEmissionModel = standardEmissionModel;
		this.specTokensEmissionModel = specTokensEmissionModel;
		this.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
		this.upperCaseSuffixGuesser = upperCaseSuffixGuesser;
		this.aprioriTagProbs = aprioriTagProbs;
		this.lemmaTree = lemmaTree;
	}

	@Deprecated
	protected CompiledModelData() {

	}

}