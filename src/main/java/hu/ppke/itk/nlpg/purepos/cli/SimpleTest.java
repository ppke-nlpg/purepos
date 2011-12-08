package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.Tagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.model.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class SimpleTest implements Runnable {
	{
		PropertyConfigurator.configure("log4j.properties");
	}

	protected Logger logger = Logger.getLogger(this.getClass());
	protected Model<String, Integer> model;
	protected Tagger tagger;
	protected Trainer trainer;
	protected final String trainingCorpusPath;

	public SimpleTest(String trainingCorpusPath) {
		this.trainingCorpusPath = trainingCorpusPath;
	}

	@Override
	public void run() {

		try {

			trainer = new Trainer(new File(trainingCorpusPath));
			model = trainer.trainModel(2, 2, 10, 10);
			tagger = new Tagger(model, Math.log(10), 20);

			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in));
			String inputLine;

			System.out.println(trainer.getStat().getStat(model));
			while ((inputLine = is.readLine()) != null) {
				// logger.trace("sentence: " + inputLine);
				ISentence s = tagger.tagSentence(inputLine.trim());
				if (s != null) {
					for (IToken t : s) {
						System.out.print(t + " ");
					}
				}
				System.out.println();
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}

	}

	public static void main(String[] args) {
		// MSZNY2011Demo demo = new MSZNY2011Demo("./res/testCorpus.txt");
		SimpleTest demo = new SimpleTest(args[0]);
		demo.run();
	}

}
