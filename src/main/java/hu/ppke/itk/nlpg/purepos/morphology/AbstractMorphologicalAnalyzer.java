package hu.ppke.itk.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.util.List;

public abstract class AbstractMorphologicalAnalyzer implements
		IMorphologicalAnalyzer {

	@Override
	public List<String> getTags(IToken word) {
		return getTags(word.getToken());
	}

	@Override
	public List<IToken> analyze(IToken word) {
		return analyze(word.getToken());
	}

}
