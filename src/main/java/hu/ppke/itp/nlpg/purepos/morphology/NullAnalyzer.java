package hu.ppke.itp.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.util.Set;

public class NullAnalyzer implements IMorphologicalAnalyzer {

	@Override
	public Set<String> getTags(String word) {
		return null;
	}

	@Override
	public Set<String> getTags(IToken word) {
		return null;
	}

	@Override
	public Set<IToken> analyze(IToken word) {
		return null;
	}

	@Override
	public Set<IToken> analyze(String word) {
		return null;
	}

}
