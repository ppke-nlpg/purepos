package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.HunPosTrainer;
import hu.ppke.itk.nlpg.purepos.ITrainer;
import hu.ppke.itk.nlpg.purepos.Tagger;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.MorphologicalTable;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class DevelopingTest implements Runnable {
	{
		PropertyConfigurator.configure("log4j.properties");
	}

	protected Logger logger = Logger.getLogger(this.getClass());
	protected Model<String, Integer> model;
	protected Tagger tagger;
	protected ITrainer trainer;
	protected final String trainingCorpusPath;
	protected final File morphTable;

	public DevelopingTest(String trainingCorpusPath, String morphFilePath) {
		this.trainingCorpusPath = trainingCorpusPath;
		this.morphTable = new File(morphFilePath);
	}

	@Override
	public void run() {

		try {

			trainer = new HunPosTrainer(new File(trainingCorpusPath));
			model = trainer.trainModel(2, 2, 10, 10);
			IMorphologicalAnalyzer analyzer = new MorphologicalTable(morphTable);
			tagger = new Tagger(model, analyzer, Math.log(1000), Math.log(10),
					10);

			// fully compatible with hunpos
			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in, "ISO-8859-2"));
			String inputLine;
			PrintStream ps = new PrintStream(System.out, true, "ISO-8859-2");
			System.err.println(trainer.getStat().getStat(model));
			String sentence = "";
			while ((inputLine = is.readLine()) != null) {
				inputLine = inputLine.trim();
				if (inputLine.equals("")) {
					sentence = sentence.substring(0, sentence.length() - 1);
					ISentence s = tagger.tagSentence(sentence);
					if (s != null) {
						for (IToken t : s) {
							ps.println(t.getToken() + "\t" + t.getTag() + "\t");
						}
					}
					ps.println();
					sentence = "";
				} else {
					sentence += inputLine + " ";
				}
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	public static void main(String[] args) {
		// MSZNY2011Demo demo = new MSZNY2011Demo("./res/testCorpus.txt");
		DevelopingTest demo = new DevelopingTest(args[0], args[1]);
		demo.run();
	}

}
