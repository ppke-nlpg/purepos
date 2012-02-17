package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.ITrainer;
import hu.ppke.itk.nlpg.purepos.MorphTagger;
import hu.ppke.itk.nlpg.purepos.Tagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itp.nlpg.purepos.morphology.HumorAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MSZNY2011Demo implements Runnable {

	protected Model<String, Integer> model;
	protected Tagger tagger;
	protected ITrainer trainer;
	protected final String trainingCorpusPath;

	public MSZNY2011Demo(String trainingCorpusPath) {
		this.trainingCorpusPath = trainingCorpusPath;
	}

	@Override
	public void run() {

		try {

			trainer = new Trainer(new File(trainingCorpusPath),
					new CorpusReader());
			model = trainer.trainModel(2, 2, 10, 10);
			tagger = new MorphTagger(model, HumorAnalyzer.getInstance(),
					Math.log(10), Math.log(1000), 20);

			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in));
			String inputLine;

			while ((inputLine = is.readLine()) != null) {

				ISentence s = tagger.tagSentence(inputLine);
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
		MSZNY2011Demo demo = new MSZNY2011Demo(args[0]);
		demo.run();
	}

}
