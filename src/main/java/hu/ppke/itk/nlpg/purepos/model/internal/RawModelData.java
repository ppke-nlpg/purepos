package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemmaUtil;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

public class RawModelData extends AbstractRawModelData<String, Integer> {

	private static final long serialVersionUID = 5815206789886465250L;

	public RawModelData(Integer taggingOrder, Integer emissionOrder) {

		stat = new Statistics();

		tagNGramModel = new NGramModel<Integer>(taggingOrder + 1);
		stdEmissionNGramModel = new NGramModel<String>(emissionOrder + 1);
		specEmissionNGramModel = new NGramModel<String>(2);
		lemmaSuffixTree = new HashLemmaTree(100);
		lemmaFreqTree = new HashSuffixTree<String>(5);
		lemmaUnigramModel = new LemmaUnigramModel<String>();
		combiner = LemmaUtil.defaultCombiner();
	}

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
		ret.lemmaGuesser = rawModeldata.lemmaSuffixTree.createGuesser(theta);
		ret.suffixLemmaModel = rawModeldata.lemmaFreqTree.createGuesser(theta);
		ret.combiner = rawModeldata.combiner;

		return ret;
	}

	@Deprecated
	protected RawModelData() {

	}

}