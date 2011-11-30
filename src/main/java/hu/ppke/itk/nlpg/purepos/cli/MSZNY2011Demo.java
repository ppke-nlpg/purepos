package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.Tagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.model.Model;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class MSZNY2011Demo implements Runnable {

	protected Model<String, Integer> model;
	protected Tagger tagger;
	protected Trainer trainer;
	protected final String trainingCorpusPath;

	public MSZNY2011Demo(String trainingCorpusPath) {
		this.trainingCorpusPath = trainingCorpusPath;
	}

	@Override
	public void run() {

		try {

			trainer = new Trainer(new File(trainingCorpusPath));
			model = trainer.trainModel(2, 2, 10, 10);
			tagger = new Tagger(model, Math.log(10), 20);

			BufferedReader br = new BufferedReader(new InputStreamReader(
					System.in));
			// tagger.tagSentence(Arrays.asList("elmentem", "a", "boltba",
			// "."));
			while (true) {
				String line = br.readLine();
				ISentence s = tagger.tagSentence(line);
				for (IToken t : s) {
					System.out.print(t + " ");
				}
				System.out.println();
			}
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
