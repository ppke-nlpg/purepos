package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

public interface ITagger {

	public ISentence tagSentence(List<String> sentence);

	public ISentence tagSentence(String sentence);

	public void tag(Scanner scanner, PrintStream ps);

}
