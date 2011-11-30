package hu.ppke.itp.nlpg.purepos.morphology;

import java.util.Set;

public interface IMorphologicalAnalyzer {
	public Set<String> getTags(String word);

}
