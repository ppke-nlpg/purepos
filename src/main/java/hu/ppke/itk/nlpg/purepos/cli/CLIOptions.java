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

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

/**
 * Command line options parser class
 * 
 * @author György Orosz
 * 
 */
public class CLIOptions {

	// Training options
	@Option(name = "-t", aliases = "--tag-order", usage = "Order of tag transition. Second order means trigram tagging. The default is 2. Training only option.", metaVar = "<number>")
	int tagOrder = 2;
	@Option(name = "-e", aliases = "--emission-order", usage = "Order of emission. First order means that the given word depends only on its tag. The default is 2.  Training only option.", metaVar = "<number>")
	int emissionOrder = 2;
	@Option(name = "-s", aliases = "--suffix-length", usage = "Use a suffix trie for guessing unknown words tags with the given maximum suffix length. The default is 10.  Training only option.", metaVar = "<length>")
	int suffixLength = 10;
	@Option(name = "-r", aliases = "--rare-frequency", usage = "Add only words to the suffix trie with frequency less than the given threshold. The default is 10.  Training only option.", metaVar = "<threshold>")
	int rareFreq = 10;

	// Tagging options
	@Option(name = "-a", aliases = "--analyzer", usage = "Set the morphological analyzer. <analyzer> can be 'none', 'integrated' or a file : <morphologicalTableFile>. The default is to use the integrated one. Tagging only option. ", metaVar = "<analyzer>")
	String morphology = "integrated"; // integrated, none, morphtable
	// @Option(name = "-d", aliases = "--separator", usage =
	// "Separator characters and tag starting character for annotated input (divided by spaces). Eg.: \"{{ || }} [\"",
	// metaVar = "<characters>")
	// String separator = "{{ || }} [";
	// @Option(name = "-p", aliases = "--only-pos-tags", usage =
	// "Do not perform stemming, output only POS tags. Tagging only option.")
	boolean noStemming = false;
	@Option(name = "-g", aliases = "--max-guessed", usage = "Limit the max guessed tags for each token. The default is 10. Tagging only option.", metaVar = "<number>")
	int maxGuessed = 10;
	@Option(name = "-n", aliases = "--max-results", usage = "Set the expected maximum number of tag sequences (with its score). The default is 1. Tagging only option.", metaVar = "<number>")
	int maxResultsNumber = 1;


	@Option(name = "-b", aliases = "--beam-theta", usage = "Set the beam-search limit. The default is 1000. Tagging only option.", metaVar = "<theta>")
	int beamTheta = 1000;

	@Option(name = "-o", aliases = "--output-file", usage = "File where the tagging output is redirected. Tagging only option.", metaVar = "<file>")
	String toFile;

	// dump options
	@Option(name = "-ps", aliases = "--print-separately", usage = "The model objects will printed separately. The default is false. Dump only option.",metaVar = "<file>" )
	boolean separatePrint = false;

	@Option(name = "-dot", usage = "The model objects will printed in dot \"friendly\" format.",metaVar = "<file>" )
	boolean dot = false;

	// Common options
	@Argument(metaVar = "tag|train|dump", usage = "Mode selection: train for training the tagger, tag for tagging a text with the given model, dump for getting statistics from the model",
			required = true)
	String command;

	@Option(name = "-c", aliases = "--encoding", usage = "Encoding used to read the training set, or write the results. The default is your OS default.", metaVar = "<encoding>")
	String encoding = System.getProperty("file.encoding");

	@Option(name = "-h", aliases = "--help", usage = "Print this message.")
	boolean printHelp = false;

	@Option(name = "-m", aliases = "--model", usage = "Specifies a path to a model file. If an exisiting model is given for training, the tool performs incremental training.", metaVar = "<modelfile>", required = true)
	String modelName;

	@Option(name = "-i", aliases = "--input-file", usage = "File containing the training set (for tagging) or the text to be tagged (for tagging). The default is the standard input.", metaVar = "<file>")
	String fromFile = null;

	@Option(name = "-d", aliases = "--beam-decoder", usage = "Use Beam Search decoder. The default is to employ the Viterbi algorithm. Tagging only option.")
	boolean useBeamSearch = false;

	@Option(name = "-f", aliases = "--config-file", usage = "Configuration file containg tag mappings. Defaults to do not map any tag.", metaVar = "<file>")
	String configFile = null;

	@Option(name = "-if", aliases = "--input-format", usage = "Set the format of the input file: vert for vertical, ord for ordinary. The default is ordinary.", metaVar = "<file>")
	String inputFormat = "ord";

	@Option(name = "-of", aliases = "--output-format", usage = "Set the format of the output file: vert for vertical, ord for ordinary. The default is ordinary.", metaVar = "<file>")
	String outputFormat = "ord";

	@Option(name = "-l", aliases = "--lemma-transformation", usage = "Chooses between the LemmaTransformation classes: \"suffix\" or \"generalized\". The default is the \"suffix\".")
	String lemmaTransformationType = "suffix";
	@Option(name = "-lt", aliases = "--lemma-threshold", usage ="Sets the threshold of the GeneralizedLemmaTransformation's decode function. Only an option, when the transformation type is \"generalized\".", metaVar = "<threshold>")
	int lemmaThreshold = 2;

	// Other stuff
	String formatMessage = "";

	@Override
	public String toString() {
		String s = "";
		s += "tagOrder: " + tagOrder + "\n";
		s += "emissionOrder: " + emissionOrder + "\n";
		s += "suffixLength: " + suffixLength + "\n";
		s += "morphology: " + morphology + "\n";
		s += "noStemming: " + noStemming + "\n";
		s += "maxGuessed: " + maxGuessed + "\n";
		s += "lemmaTransformationType: " + lemmaTransformationType + "\n";
		s += "lemmaThreshold: " + lemmaThreshold + "\n";
		s += "toFile: " + toFile + "\n";
		s += "outputFormat: " + outputFormat + "\n";
		s += "command: " + command + "\n";
		s += "encoding: " + encoding + "\n";
		s += "printHelp: " + printHelp + "\n";
		s += "modelName: " + modelName + "\n";
		s += "fromFile: " + fromFile + "\n";
		s += "inputFormat: " + inputFormat + "\n";
		return s;

	}
}
