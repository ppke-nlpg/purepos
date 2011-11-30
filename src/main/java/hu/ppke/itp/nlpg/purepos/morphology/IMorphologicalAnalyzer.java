package hu.ppke.itp.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.util.Set;

public interface IMorphologicalAnalyzer {
	public Set<String> getTags(String word);

	public Set<String> getTags(IToken word);

	public Set<IToken> analyze(IToken word);

	public Set<IToken> analyze(String word);

}
