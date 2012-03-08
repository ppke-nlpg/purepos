package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.purepos.ITrainer;
import hu.ppke.itk.nlpg.purepos.MorphTagger;
import hu.ppke.itk.nlpg.purepos.Tagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.morphology.HumorAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.MorphologicalTable;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

public class EfficiencyTest implements Runnable {
	{
		String logFile = "log4j.properties";
		File t = new File(logFile);
		if (t.exists())
			PropertyConfigurator.configure(logFile);

	}

	protected Logger logger = Logger.getLogger(this.getClass());
	protected Model<String, Integer> model;
	protected Tagger tagger;
	protected ITrainer trainer;
	protected final String trainingCorpusPath;
	protected final File morphTable;

	public EfficiencyTest(String trainingCorpusPath, String morphFilePath) {
		this.trainingCorpusPath = trainingCorpusPath;
		this.morphTable = new File(morphFilePath);
	}

	@Override
	public void run() {
		long start = System.currentTimeMillis();
		long end;
		try {
			logger.info("Starting training...");

			trainer = new Trainer(new File(trainingCorpusPath),
					new CorpusReader());
			model = trainer.trainModel(2, 2, 10, 10);
			end = System.currentTimeMillis();
			logger.info("Done training in " + ((start - end) / 1000.0) + " ms!");
			logger.info("Starting tagging...");
			IMorphologicalAnalyzer analyzer;
			start = System.currentTimeMillis();
			if (morphTable.getName().equals("humor"))
				analyzer = HumorAnalyzer.getInstance();
			else if (morphTable.exists())
				analyzer = new MorphologicalTable(morphTable);

			else
				analyzer = new NullAnalyzer();
			System.err.println("MA: " + analyzer.getClass().getName());
			tagger = new MorphTagger(model, analyzer, Math.log(1000),
					Math.log(10), 10);

			// fully compatible with hunpos
			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in));
			String inputLine;
			PrintStream ps = new PrintStream(System.out, true);
			System.err.println(trainer.getStat().getStat(model));
			while ((inputLine = is.readLine()) != null) {
				inputLine = inputLine.trim();
				ISentence s = tagger.tagSentence(inputLine);
				if (s != null)
					ps.println(s.toString());
				else
					ps.println();
			}
			is.close();
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		end = System.currentTimeMillis();
		logger.info("Done tagging in " + ((start - end) / 1000.0) + " ms!");
	}

	public static void main(String[] args) {
		// MSZNY2011Demo demo = new MSZNY2011Demo("./res/testCorpus.txt");
		EfficiencyTest demo;
		if (args.length > 1)
			demo = new EfficiencyTest(args[0], args[1]);
		else
			demo = new EfficiencyTest(args[0], "");
		demo.run();
	}
}
