/*******************************************************************************
 * Copyright (c) 2012 György Orosz, Attila Novák.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/
 * 
 * This file is part of PurePos.
 * 
 * PurePos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PurePos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.purepos.ITagger;
import hu.ppke.itk.nlpg.purepos.MorphTagger;
import hu.ppke.itk.nlpg.purepos.POSTagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.cli.configuration.Configuration;
import hu.ppke.itk.nlpg.purepos.cli.configuration.ConfigurationReader;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.serializer.SSerializer;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.model.internal.StringMapper;
import hu.ppke.itk.nlpg.purepos.model.internal.StringMapping;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.MorphologicalTable;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;

import org.apache.commons.configuration.ConfigurationException;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Interface for using the tagger.
 * 
 * @author György Orosz
 * 
 */
public class PurePos implements Runnable {
	private static final String TAG_OPT = "tag";
	private static final String TRAIN_OPT = "train";
	private static final String PRE_MA = "pre";
	private static final String NONE_MA = "none";
	private static final String INTEGRATED_MA = "integrated";
	protected CLIOptions options;

	// protected static TaggedSequenceReader taggedSeqReader;

	public PurePos(CLIOptions options) {
		this.options = options;
	}

	public static void train(String encoding, String modelPath, String inputPath, String inputFormat, int tagOrder,
							 int emissionOrder, int suffLength, int rareFreq) throws ParsingException, Exception {
		Scanner sc = createScanner(encoding, inputPath, false);
		Trainer trainer = new Trainer(sc, new CorpusReader(inputFormat));

		File modelFile = new File(modelPath);
		RawModel retModel;
		
		if (modelFile.exists()) {
			System.err.println("Reading model... ");
			retModel = SSerializer.readModel(modelFile);
			System.err.println("Training model... ");
			retModel = trainer.trainModel(retModel);
		} else {
			System.err.println("Training model... ");
			retModel = trainer.trainModel(tagOrder, emissionOrder, suffLength,
					rareFreq);
		}
		System.err.println(trainer.getStat().getStat(retModel));

		System.err.println("Writing model... ");
		SSerializer.writeModel(retModel, new File(modelPath));
		System.err.println("Done!");
	}

	protected static Scanner createScanner(String encoding, String inputPath,
			boolean taggedSeq) throws Exception {
		Scanner sc;
		if (inputPath != null) {
			sc = new Scanner(new File(inputPath), encoding);
		} else {
			sc = new Scanner(System.in, encoding);
		}
		// if (taggedSeq) {
		// String[] parts = seps.split(" ");
		// if (parts == null || parts.length < 4)
		// throw new Exception("Badly formatted separator parameter!");
		// taggedSeqReader = new TaggedSequenceReader(sc, parts[0], parts[1],
		// parts[2], parts[3]);
		// return taggedSeqReader.getScanner();
		// } else
		return sc;
	}

	public static void tag(String encoding, String modelPath, String inputPath, String inputFormat,
			String analyzer, boolean noStemming, int maxGuessed, int maxresnum,
			int beamTheta, boolean useBeamSearch, String outPath, String outputFormat) throws Exception {
		Scanner input = createScanner(encoding, inputPath,
				analyzer.equals(PRE_MA));
//
//		Configuration conf;
//		if (configFile != null) {
//			ConfigurationReader reader = new ConfigurationReader();
//			conf = reader.read(new File(configFile));
//			Util.LEMMA_MAPPER = new StringMapper(conf.getLemmaMappings());
//		} else {
//			conf = new Configuration(new LinkedList<StringMapping>(), new LinkedList<StringMapping>());
//		}

		ITagger t = createTagger(modelPath, analyzer, noStemming, maxGuessed,
				Math.log(beamTheta), useBeamSearch, Util.CONFIGURATION);

		PrintStream output;
		if (outPath == null) {
			output = new PrintStream(System.out, true, encoding);
		} else {
			output = new PrintStream(new File(outPath), encoding);
		}
		System.err.println("Tagging:");
		t.tag(input, inputFormat, output, outputFormat, maxresnum);
	}

	public static ITagger createTagger(String modelPath, String analyzer,
			boolean noStemming, int maxGuessed, double beamLogTheta,
			boolean useBeamSearch, Configuration conf) throws Exception {
		IMorphologicalAnalyzer ma;
		if (analyzer.equals(INTEGRATED_MA)) {
			// TODO: set lex files through environment vars
			try {
				// System.err
				// .println("Trying to use Humor morphological analyzer.");
				ma = loadHumor();
			} catch (ClassNotFoundException e) {
				System.err
						.println("Humor java files are not found. Not using any morphological analyzer.");
				ma = new NullAnalyzer();
			} catch (Exception e) {
				System.err.println(e.getMessage());
				System.err.println("Not using any morphological analyzer.");
				ma = new NullAnalyzer();
			}
		} else if (analyzer.equals(NONE_MA)) {
			ma = new NullAnalyzer();

			// } else if (analyzer.equals(PRE_MA)) {
			// ma = taggedSeqReader.getMorphologicalAnalyzer();
		} else {
			System.err.println("Using morphological table at: " + analyzer
					+ ".");
			ma = new MorphologicalTable(new File(analyzer));
		}

		System.err.println("Reading model... ");
		RawModel rawmodel = SSerializer.readModel(new File(modelPath));
		System.err.println("Compiling model... ");
		CompiledModel<String, Integer> model = rawmodel.compile(conf);
		ITagger t;

		// double beamLogTheta = Math.log(1000);
		// double beamLogTheta = Math.log(10000000);
		// double beamLogTheta = Double.POSITIVE_INFINITY;
		double suffixLogTheta = Math.log(10);
		if (noStemming) {
			t = new POSTagger(model, ma, beamLogTheta, suffixLogTheta,
					maxGuessed, useBeamSearch);
		} else {
			t = new MorphTagger(model, ma, beamLogTheta, suffixLogTheta,
					maxGuessed, useBeamSearch);
		}
		return t;
	}

	/**
	 * Loads the latest Humor jar file and create an analyzer instance
	 * 
	 * @return analyzer instance
	 */
	protected static IMorphologicalAnalyzer loadHumor()
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException, MalformedURLException {
		String humorPath = System.getProperty("humor.path");
		if (humorPath == null)
			throw new ClassNotFoundException("Humor jar file is not present");

		File dir = new File(humorPath);

		File[] candidates = dir.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".jar")
						&& filename.startsWith("humor-");
			}
		});

		Arrays.sort(candidates);

		@SuppressWarnings("deprecation")
		URL humorURL = candidates[candidates.length - 1].toURL();

		URLClassLoader myLoader = new URLClassLoader(new URL[] { humorURL },
				PurePos.class.getClassLoader());
		Class<?> humorClass = Class.forName(
				"hu.ppke.itk.nlpg.purepos.morphology.HumorAnalyzer", true,
				myLoader);
		return (IMorphologicalAnalyzer) humorClass.newInstance();
	}

	@Override
	public void run() {
		try {
			Configuration conf;
			if (options.configFile != null) {
				ConfigurationReader reader = new ConfigurationReader();
				conf = reader.read(new File(options.configFile));
				Util.LEMMA_MAPPER = new StringMapper(conf.getLemmaMappings());
			} else {
				conf = new Configuration();
			}
			Util.CONFIGURATION = conf;
			
			if (options.command.equals(TRAIN_OPT)) {
				train(options.encoding, options.modelName, options.fromFile,
						options.inputFormat,options.tagOrder,options.emissionOrder,
						options.suffixLength, options.rareFreq);
			} else if (options.command.equals(TAG_OPT)) {
				tag(options.encoding, options.modelName, options.fromFile,
						options.inputFormat, options.morphology,
						options.noStemming, options.maxGuessed,
						options.maxResultsNumber, options.beamTheta,
						options.useBeamSearch, options.toFile,
						options.outputFormat);
			}
		} catch (ConfigurationException e) {
			System.err.println("Malformed configuration file: " + e.getMessage() );
		} catch (ParsingException e) {
			System.err.println(e.getWrappedException().getMessage() );
		} catch (Exception e) {
			// System.err.println(e.getMessage());
			e.printStackTrace();

			System.exit(-1);
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
		} catch (Throwable e) {
			System.err.println(e);
			parser.printUsage(System.err);
		}
	}
}
