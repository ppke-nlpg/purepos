package hu.ppke.itk.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.util.List;

public interface IMorphologicalAnalyzer {
	public List<String> getTags(String word);

	public List<String> getTags(IToken word);

	public List<IToken> analyze(IToken word);

	public List<IToken> analyze(String word);

}
