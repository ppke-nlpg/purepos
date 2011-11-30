package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;

import java.util.List;

public interface ITagger {

	public ISentence tagSentence(List<String> sentence);

	public ISentence tagSentence(String sentence);

}
