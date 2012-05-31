package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.purepos.ITagger;
import hu.ppke.itk.nlpg.purepos.MorphTagger;
import hu.ppke.itk.nlpg.purepos.POSTagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.common.Serializator;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.morphology.HumorAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.MorphologicalTable;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Scanner;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class PurePos implements Runnable {
	protected CLIOptions options;

	public PurePos(CLIOptions options) {
		this.options = options;
	}

	public static void train(String encoding, String modelPath,
			String inputPath, int tagOrder, int emissionOrder, int suffLength,
			int rareFreq) throws ParsingException, IOException,
			ClassNotFoundException {

		Scanner sc = createScanner(encoding, inputPath);
		Trainer trainer = new Trainer(sc, new CorpusReader());

		File modelFile = new File(modelPath);
		RawModel retModel;
		if (modelFile.exists()) {
			retModel = Serializator.readModel(modelFile);
			retModel = trainer.trainModel(retModel);
		} else {
			retModel = trainer.trainModel(tagOrder, emissionOrder, suffLength,
					rareFreq);
		}
		System.err.println(trainer.getStat().getStat(retModel));

		Serializator.writeModel(retModel, new File(modelPath));
	}

	protected static Scanner createScanner(String encoding, String inputPath)
			throws FileNotFoundException {
		Scanner sc;
		if (inputPath != null) {
			sc = new Scanner(new File(inputPath), encoding);
		} else {
			sc = new Scanner(System.in, encoding);
		}
		return sc;
	}

	public static void tag(String encoding, String modelPath, String inputPath,
			String analyzer, boolean noStemming, int maxGuessed, String outPath)
			throws IOException, ClassNotFoundException {
		RawModel rawmodel = Serializator.readModel(new File(modelPath));
		CompiledModel<String, Integer> model = rawmodel.compile();
		ITagger t;
		IMorphologicalAnalyzer ma;
		if (analyzer.equals("none")) {
			ma = new NullAnalyzer();
		} else if (analyzer.equals("integrated")) {
			// TODO: set lex files through environment vars
			ma = HumorAnalyzer.getInstance();
		} else {
			ma = new MorphologicalTable(new File(analyzer));
		}
		if (noStemming) {
			t = new POSTagger(model, ma, Math.log(10000), Math.log(10),
					maxGuessed);
		} else {
			t = new MorphTagger(model, ma, Math.log(10000), Math.log(10),
					maxGuessed);
		}
		Scanner input = createScanner(encoding, inputPath);
		PrintStream output;
		if (outPath == null) {
			output = new PrintStream(System.out, true, encoding);
		} else {
			output = new PrintStream(new File(outPath), encoding);
		}

		t.tag(input, output);
	}

	@Override
	public void run() {
		try {
			if (options.command.equals("train")) {
				train(options.encoding, options.modelName, options.fromFile,
						options.tagOrder, options.emissionOrder,
						options.suffixLength, options.rareFreq);
			} else if (options.command.equals("tag")) {
				tag(options.encoding, options.modelName, options.fromFile,
						options.morphology, options.noStemming,
						options.maxGuessed, options.toFile);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// TODO: error handling
		}
	}

	public static void main(String[] args) {
		CLIOptions options = new CLIOptions();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			parser.parseArgument(args);

			PurePos app = new PurePos(options);
			app.run();
		} catch (CmdLineException e) {
			System.err.println("Error: " + e.getMessage());
			System.err
					.println("\nUsage: java -jar <purepos.jar> [options...] arguments...");
			parser.printUsage(System.err);
			return;
		}
	}
}
