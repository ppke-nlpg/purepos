package hu.ppke.itp.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

import java.util.HashSet;
import java.util.Set;

import com.morphologic.lib.Humor;

/***
 * Wrapper class around Humor for only getting POS tags
 * 
 * @author oroszgy
 * 
 */
public class HumorAnalyzer implements IMorphologicalAnalyzer {
	public static String LEX_PATH = "/usr/local/share/humor/lex";

	public final int opt = Humor._CASE_SENSITIVE
	/* | Humor._FILTER_STEM_AND_POS */| Humor._SHOW_STEM_FULL;

	private static HumorAnalyzer instance = null;

	protected HumorAnalyzer(String lexPath) {
		init(lexPath);
	}

	/**
	 * Get the stemmer instance, which has the deafult lex path.
	 * 
	 * @return
	 */
	public static HumorAnalyzer getInstance() {
		return getInstance(LEX_PATH);
	}

	/**
	 * Get the stemmer instance, and sets the lex path. Nate that during an
	 * aplication lifetime only one instance can exists
	 * 
	 * @param lexPath
	 * @return
	 */
	public static HumorAnalyzer getInstance(String lexPath) {
		if (instance == null)
			instance = new HumorAnalyzer(lexPath);
		return instance;
	}

	private void init(String lexPath) {
		try {
			Humor.initialize(lexPath, 1038);
			// System.out.println(Humor._stemmingId);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void finalize() throws Throwable {
		Humor.close();
	}

	protected String[] getHumorAnalysises(String word) {
		String preprocessedWord = preprocessWord(word);
		String[] analysises = Humor.getStem(preprocessedWord, opt);
		String[] postprocessedAnalysises = postprocessAnalysises(analysises);
		return postprocessedAnalysises;
	}

	protected String[] postprocessAnalysises(String[] anals) {
		if (anals == null)
			return null;
		for (int i = 0; i < anals.length; ++i) {
			String anal = anals[i];
			String suffix = "][PUNCT]";
			if (anal.endsWith(suffix)) {
				anals[i] = anal.substring(0, anal.length()
						- (suffix.length() - 1));
			}
		}
		return anals;
	}

	protected String preprocessWord(String word) {
		return word.replaceAll("(.+)-$", "$1");
	}

	@Override
	public Set<String> getTags(String word) {
		String[] anals = getHumorAnalysises(word);
		HashSet<String> ret = new HashSet<String>();
		if (anals == null)
			return null;
		for (String a : anals) {
			ret.add(splitTag(a));
		}
		return ret;
	}

	@Override
	public Set<String> getTags(IToken word) {
		return getTags(word.getToken());
	}

	@Override
	public Set<IToken> analyze(IToken word) {
		return analyze(word.getToken());
	}

	@Override
	public Set<IToken> analyze(String word) {
		String[] anals = getHumorAnalysises(word);
		HashSet<IToken> ret = new HashSet<IToken>();
		if (anals == null)
			return null;
		for (String a : anals) {
			Token t = new Token(word, splitStem(a), splitTag(a));
			ret.add(t);
		}
		return ret;
	}

	protected String splitTag(String aw) {
		int indexOf = aw.indexOf('[');
		int lastIndexOf = aw.lastIndexOf(']');
		if (indexOf == -1 || lastIndexOf == -1)
			return "";
		return aw.substring(indexOf, lastIndexOf + 1);
	}

	protected String splitStem(String aw) {
		int indexOf = aw.indexOf('[');
		if (indexOf == -1)
			return aw;
		return aw.substring(0, indexOf);
	}
}
