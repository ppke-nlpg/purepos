package hu.ppke.itk.nlpg.purepos.cli;

import java.io.File;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CLIOptions {

	// Training options
	@Option(name = "-t", aliases = "--tag-order", usage = "Order of tag transition. Second order means trigram tagging. The default is 2.", metaVar = "<number>")
	int tagOrder = 2;
	@Option(name = "-e", aliases = "--emission-order", usage = "Order of emission. First order means that the given word depends only on its tag. The default is 2.", metaVar = "<number>")
	int emissionOrder = 2;
	@Option(name = "-s", aliases = "--suffix-length", usage = "Use a suffix trie for guessing unknown words tags with the given maximum suffix length. The default is 10. ", metaVar = "<length>")
	int suffixLength = 10;
	@Option(name = "-r", aliases = "--rare-frequency", usage = "Add only words to the suffix trie with frequency less than the given treshold. The default is 10.", metaVar = "<treshold>")
	int rareFreq = 10;
	@Option(name = "-i", aliases = "--input-file", usage = "File containg the training set. The default is the dtandard input", metaVar = "<file>")
	String fromFile = null;

	// Tagging options
	// TODO: use it better
	@Option(name = "-a", aliases = "--analyzer", usage = "Set the morphological analyzer. The default is to use the integrated.", metaVar = "none|intgrated|<morphologicalTableFile>")
	String morphology = "none"; // integrated, none, morphtable
	@Option(name = "-p", aliases = "--only-pos-tags", usage = "Do not perform stemming, output only POS tags.")
	boolean noStemming = false;
	@Option(name = "-g", aliases = "--max-guessed", usage = "Limit the max guessed tags for each token. The default is 10.", metaVar = "<number>")
	int maxGuessed = 10;
	@Option(name = "-o", aliases = "--output-file", usage = "File where the tagging output is redirected.", metaVar = "<file>")
	String toFile;

	// Common options
	@Argument(metaVar = "tag|train", usage = "train for training the tagger, tag for tagging a text with the given model", required = true)
	String command;

	@Option(name = "-e", aliases = "--encoding", usage = "Encoding used to read the training set, or write the results. The default is your OS default.")
	String encoding = null;

	@Option(name = "-h", aliases = "--help", usage = "print this message")
	boolean printHelp = false;

	@Option(name = "-m", aliases = "--model", usage = "specifies a path to a model file", metaVar = "<modelfile>", required = true)
	File modelName;

	// Other stuff
	String formatMessage = "";
}
