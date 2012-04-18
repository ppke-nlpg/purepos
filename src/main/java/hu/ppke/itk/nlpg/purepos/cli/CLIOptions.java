package hu.ppke.itk.nlpg.purepos.cli;

import org.kohsuke.args4j.Argument;
import org.kohsuke.args4j.Option;

public class CLIOptions {

	// Training options
	@Option(name = "-t", aliases = "--tag-order", usage = "Order of tag transition. Second order means trigram tagging. The default is 2. Training only option.", metaVar = "<number>")
	int tagOrder = 2;
	@Option(name = "-e", aliases = "--emission-order", usage = "Order of emission. First order means that the given word depends only on its tag. The default is 2.  Training only option.", metaVar = "<number>")
	int emissionOrder = 2;
	@Option(name = "-s", aliases = "--suffix-length", usage = "Use a suffix trie for guessing unknown words tags with the given maximum suffix length. The default is 10.  Training only option.", metaVar = "<length>")
	int suffixLength = 10;
	@Option(name = "-r", aliases = "--rare-frequency", usage = "Add only words to the suffix trie with frequency less than the given treshold. The default is 10.  Training only option.", metaVar = "<treshold>")
	int rareFreq = 10;

	// Tagging options
	// TODO: use it better
	@Option(name = "-a", aliases = "--analyzer", usage = "Set the morphological analyzer. <analyzer> can be 'none', 'integrated' or a file : <morphologicalTableFile>. The default is to use the integrated one. Tagging only option. ", metaVar = "<analyzer>")
	String morphology = "integrated"; // integrated, none, morphtable
	@Option(name = "-p", aliases = "--only-pos-tags", usage = "Do not perform stemming, output only POS tags. Tagging only option.")
	boolean noStemming = false;
	@Option(name = "-g", aliases = "--max-guessed", usage = "Limit the max guessed tags for each token. The default is 10. Tagging only option.", metaVar = "<number>")
	int maxGuessed = 10;
	@Option(name = "-o", aliases = "--output-file", usage = "File where the tagging output is redirected. Tagging only option.", metaVar = "<file>")
	String toFile;

	// Common options
	@Argument(metaVar = "tag|train", usage = "Mode selection: train for training the tagger, tag for tagging a text with the given model.", required = true)
	String command;

	@Option(name = "-c", aliases = "--encoding", usage = "Encoding used to read the training set, or write the results. The default is your OS default.")
	String encoding = System.getProperty("file.encoding");

	@Option(name = "-h", aliases = "--help", usage = "Print this message.")
	boolean printHelp = false;

	@Option(name = "-m", aliases = "--model", usage = "Specifies a path to a model file.", metaVar = "<modelfile>", required = true)
	String modelName;

	@Option(name = "-i", aliases = "--input-file", usage = "File containg the training set (for tagging) or the text to be tagged (for tagging). The default is the standard input.", metaVar = "<file>")
	String fromFile = null;

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
		s += "toFile: " + toFile + "\n";
		s += "command: " + command + "\n";
		s += "encoding: " + encoding + "\n";
		s += "printHelp: " + printHelp + "\n";
		s += "modelName: " + modelName + "\n";
		s += "fromFile: " + fromFile + "\n";
		return s;

	}
}
