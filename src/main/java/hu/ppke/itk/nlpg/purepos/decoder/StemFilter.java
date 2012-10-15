package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class StemFilter {
	private Set<String> stems = null;

	public StemFilter(File source) throws FileNotFoundException {
		stems = readFile(source);
	}

	protected Set<String> readFile(File path) throws FileNotFoundException {
		Set<String> ret = new HashSet<String>();

		Scanner fileScanner = new Scanner(path);
		while (fileScanner.hasNext())
			ret.add(fileScanner.next());

		return ret;
	}

	public List<IToken> filterStem(List<IToken> candidates) {
		if (stems.isEmpty())
			return candidates;
		List<IToken> ret = new ArrayList<IToken>();
		for (IToken c : candidates) {
			if (stems.contains(c.getStem()))
				ret.add(c);
		}
		if (ret.isEmpty())
			return candidates;
		else
			return ret;

	}
}
