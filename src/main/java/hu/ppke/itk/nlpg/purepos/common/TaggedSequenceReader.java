package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.UserMorphology;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.tuple.Pair;

public class TaggedSequenceReader {
	Scanner cleanedSeq;
	Map<String, List<Pair<String, String>>> table = new HashMap<String, List<Pair<String, String>>>();
	// Pattern parts;
	String sepStart, sepEnd, sepMid, startAnal;
	Pattern anals;

	public TaggedSequenceReader(Scanner s) {
		this(s, "{{", "||", "}}", "[");
	}

	public TaggedSequenceReader(Scanner s, String sepStart, String sepMid,
			String sepEnd, String startAnal) {
		this.sepEnd = sepEnd;
		this.sepStart = sepStart;
		this.sepMid = sepMid;
		this.startAnal = startAnal;
		String wordRe = "\\S*";
		anals = Pattern.compile(Pattern.quote(sepStart) + wordRe
				+ Pattern.quote(sepEnd));

		StringBuffer sb = new StringBuffer();
		String line;
		while (s.hasNext()) {
			line = s.nextLine();
			sb.append(line + "\n");
			addtoDict(line);
		}

		cleanedSeq = new Scanner(clean(sb.toString()));

	}

	protected void addtoDict(String line) {
		Scanner ps = new Scanner(line);
		String token;
		while (ps.hasNext()) {
			token = ps.next();
			if (token.indexOf(sepEnd) - token.indexOf(sepStart) > sepStart
					.length()) {
				int start = token.indexOf(sepStart) + sepStart.length();
				int end = token.indexOf(sepEnd);
				String wordform = token.substring(0, start - sepStart.length());
				String rest = token.substring(start, end);
				List<String> anals = Arrays.asList(rest.split(Pattern
						.quote(sepMid)));
				for (String anal : anals) {
					int split = anal.indexOf(startAnal);
					String stem = anal.substring(0, split);
					String tag = anal.substring(split);
					putAnal(wordform, tag, stem);
				}
			}
		}

	}

	protected void putAnal(String token, String tag, String stem) {
		if (!table.containsKey(token)) {
			table.put(token, new ArrayList<Pair<String, String>>());
		}
		table.get(token).add(Pair.of(tag, stem));
	}

	protected String clean(String s) {
		Matcher m = anals.matcher(s);
		String ret = m.replaceAll("");
		return ret;
	}

	public Scanner getScanner() {
		return this.cleanedSeq;
	}

	public IMorphologicalAnalyzer getMorphologicalAnalyzer() {
		return new UserMorphology(table);
	}
}
