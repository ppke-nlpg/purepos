package hu.ppke.itp.nlpg.purepos.morphology;

import java.util.Set;

public class NullAnalyzer implements IMorphologicalAnalyzer {

	@Override
	public Set<String> getTags(String word) {
		return null;
	}

}
