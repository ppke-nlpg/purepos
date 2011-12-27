package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.HunPosTrainer;
import hu.ppke.itk.nlpg.purepos.ITrainer;
import hu.ppke.itk.nlpg.purepos.Tagger;
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
	protected ITrainer trainer;
	protected final String trainingCorpusPath;

	public SimpleTest(String trainingCorpusPath) {
		this.trainingCorpusPath = trainingCorpusPath;
	}

	@Override
	public void run() {

		try {

			trainer = new HunPosTrainer(new File(trainingCorpusPath));
			model = trainer.trainModel(2, 2, 10, 10);
			tagger = new Tagger(model, Math.log(10), 20);

			// fully compatible with hunpos
			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in, "ISO-8859-2"));
			String inputLine;

			System.out.println(trainer.getStat().getStat(model));
			String sentence = "";
			while ((inputLine = is.readLine()) != null) {
				inputLine = inputLine.trim();
				if (inputLine.equals("")) {
					sentence = sentence.substring(0, sentence.length() - 1);
					ISentence s = tagger.tagSentence(sentence);
					if (s != null) {
						for (IToken t : s) {
							System.out
									.println(t.getToken() + "\t" + t.getTag());
						}
					}
					System.out.println();
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
		SimpleTest demo = new SimpleTest(args[0]);
		demo.run();
	}

}
