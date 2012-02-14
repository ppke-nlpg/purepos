package hu.ppke.itp.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.util.List;

public class NullAnalyzer extends AbstractMorphologicalAnalyzer {

	@Override
	public List<String> getTags(String word) {
		return null;
	}

	@Override
	public List<IToken> analyze(String word) {
		return null;
	}

}
