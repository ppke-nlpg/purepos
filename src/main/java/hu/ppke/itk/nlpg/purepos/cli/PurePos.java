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
import hu.ppke.itk.nlpg.purepos.common.serializer.SSerializer;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.morphology.HumorAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.MorphologicalTable;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Scanner;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

/**
 * Interface for using the tagger.
 * 
 * @author György Orosz
 * 
 */
public class PurePos implements Runnable {
	protected CLIOptions options;

	public PurePos(CLIOptions options) {
		this.options = options;
	}

	public static void train(String encoding, String modelPath,
			String inputPath, int tagOrder, int emissionOrder, int suffLength,
			int rareFreq) throws ParsingException, Exception {
		Scanner sc = createScanner(encoding, inputPath);
		Trainer trainer = new Trainer(sc, new CorpusReader());

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
			throws Exception {
		ITagger t = createTagger(modelPath, analyzer, noStemming, maxGuessed);
		Scanner input = createScanner(encoding, inputPath);
		PrintStream output;
		if (outPath == null) {
			output = new PrintStream(System.out, true, encoding);
		} else {
			output = new PrintStream(new File(outPath), encoding);
		}

		t.tag(input, output);
	}

	public static ITagger createTagger(String modelPath, String analyzer,
			boolean noStemming, int maxGuessed) throws Exception {
		System.err.println("Reading model... ");
		RawModel rawmodel = SSerializer.readModel(new File(modelPath));
		System.err.println("Compiling model... ");
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
		return t;
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
