package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.common.Statistics;

public class RawModelData extends AbstractRawModelData<String, Integer> {

	private static final long serialVersionUID = 5815206789886465250L;

	public RawModelData(Integer taggingOrder, Integer emissionOrder) {

		stat = new Statistics();

		tagNGramModel = new NGramModel<Integer>(taggingOrder + 1);
		stdEmissionNGramModel = new NGramModel<String>(emissionOrder + 1);
		specEmissionNGramModel = new NGramModel<String>(2);
		lemmaTree = new HashLemmaTree(100);
		lemmaUnigramModel = new LemmaUnigramModel<String>();
		combiner = new LogLinearCombiner();
	}

	@Deprecated
	protected RawModelData() {

	}

}