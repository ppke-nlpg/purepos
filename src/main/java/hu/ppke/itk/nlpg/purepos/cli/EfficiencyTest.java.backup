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
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.purepos.ITrainer;
import hu.ppke.itk.nlpg.purepos.MorphTagger;
import hu.ppke.itk.nlpg.purepos.POSTagger;
import hu.ppke.itk.nlpg.purepos.Trainer;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.morphology.HumorAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.MorphologicalTable;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.PrintStream;

/**
 * Class for testing the tagger's efficiency.
 * 
 * @author György Orosz
 * 
 */
public class EfficiencyTest implements Runnable {
	// {
	// String logFile = "log4j.properties";
	// File t = new File(logFile);
	// if (t.exists())
	// PropertyConfigurator.configure(logFile);
	//
	// }

	// protected Logger logger = Logger.getLogger(this.getClass());
	protected RawModel model;
	protected POSTagger tagger;
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
			System.err.println("Starting training...");

			trainer = new Trainer(new File(trainingCorpusPath),
					new CorpusReader());
			model = trainer.trainModel(2, 2, 10, 10);
			end = System.currentTimeMillis();
			System.err.println("Done training in " + ((start - end) / 1000.0)
					+ " ms!");
			System.err.println("Starting tagging...");
			IMorphologicalAnalyzer analyzer;
			start = System.currentTimeMillis();
			if (morphTable.getName().equals("humor"))
				analyzer = HumorAnalyzer.getInstance();
			else if (morphTable.exists())
				analyzer = new MorphologicalTable(morphTable);

			else
				analyzer = new NullAnalyzer();
			System.err.println("MA: " + analyzer.getClass().getName());
			tagger = new MorphTagger(model.compile(), analyzer, Math.log(1000),
					Math.log(10), 10);

			// fully compatible with hunpos
			BufferedReader is = new BufferedReader(new InputStreamReader(
					System.in));
			// BufferedReader is = new BufferedReader(new InputStreamReader(
			// new FileInputStream(new File("../test/eff/test.sent"))));
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
		System.err.println("Done tagging in " + ((start - end) / 1000.0)
				+ " ms!");
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
