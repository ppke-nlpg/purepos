package com.morphologic.lib;

import java.util.Scanner;
import java.util.Vector;

/**
 * 
 * A JNI wrapper for HUMOR (High-speed Unification MORphology)
 * 
 * provided by Morphologic Kft.
 * 
 * Author: Istvan Endredy
 * 
 * Modified by: Endre
 **/

public class Humor {

	private static int _stemmingId = -1;

	private static int _generateId = -1;

	private static int _generateWFormsId = -1;

	private static int _accentId = -1;

	// options
	// filter options
	public final static int _FILTER_STEM = 0x1;
	public final static int _FILTER_STEM_AND_POS = 0x2;
	public final static int _FILTER_COMPOUND_WORDS = 0x4;
	public final static int _FILTER_DERIVED_STEMS = 0x8;
	public final static int _FILTER_SAME_FORM = 0x16;

	/*
	 * description: FILTER_STEM = the same stem will be only once
	 * FILTER_STEM_AND_POS = the same stem+pos will be only once for example
	 * "ment" has 3 stems: "ment[ige](en vmit)", "megy[IGE](o)",
	 * "ment[MN](vmitol mentes)" with FILTER_STEM output is: "ment,megy" with
	 * FILTER_STEM_AND_POS output is: "ment,ment,megy" FILTER_COMPOUND_WORDS =
	 * compound words are filtered if there are other alternatives (for example
	 * "szer+elem" will be filtered because it has stem "szerelem", too)
	 * FILTER_DERIVED_STEMS = "ad=s" != "ad", if "ad=s" is in lexicon
	 * FILTER_SAME_FORM = stems will be filtered which would be the same as
	 * input
	 */

	// output display options
	public final static int _SHOW_STEM_ONLY = 0x100;
	public final static int _SHOW_STEM_AND_POS = 0x200;
	public final static int _SHOW_COMPOUND_POSITIONS = 0x400;
	public final static int _SHOW_STEM_FULL = 0x800;

	/*
	 * description: SHOW_STEM_ONLY = output contains only stems ("alma,alom")
	 * SHOW_STEM_AND_POS = output contains stems and part of speech
	 * ("alma[FN],alom[FN]") SHOW_COMPOUND_POSITIONS = output contains compound
	 * delimiter positions ("ablak+kilincs") SHOW_STEM_FULL = output contains
	 * every mophological categories
	 */

	// other options
	public final static int _CASE_SENSITIVE = 0x1000;
	final static int _COPY_INPUT_CAPITALISATION = 0x2000;

	private static boolean _initialized = false;

	/**
	 * 
	 * Loads the JNI library, if available.
	 * 
	 * <br>
	 * <br>
	 **/

	public static void initialize(String directory, int languageCode)
			throws Throwable {

		// System.out.println( "Humor init:" + directory + " languageCode:" +
		// languageCode);

		System.loadLibrary("humor2java");

		_stemmingId = init(directory, languageCode, 1);

		// System.out.println( "_stemmingId: " + _stemmingId);

		/*
		 * _generateId = init(directory, languageCode, 2);
		 * 
		 * System.out.println( "_generateId: " + _generateId);
		 * 
		 * _generateWFormsId = init(directory, languageCode, 3);
		 */

		if (_stemmingId != -1 /* && _generateId !=-1 && _generateWFormsId != -1 */) {

			_initialized = true;

			// System.out.println("Initialized!");

		}

	}

	public static boolean isInitialized() {

		return _initialized;

	}

	public static int closeMorph(int ids) {

		try {

			return close(ids);

		} catch (UnsatisfiedLinkError x) {

			System.err.println("UnsatisfiedLinkError: " + x.getMessage());

			return -100;

		}

	}

	public static int close() {
		try {
			return close(_stemmingId);

		} catch (UnsatisfiedLinkError x) {

			System.err.println("UnsatisfiedLinkError: " + x.getMessage());

			return -100;

		}
	}

	public static String[] getStem(String word, int options) {

		try {

			return stemEx(_stemmingId, word, options);

		} catch (UnsatisfiedLinkError x) {

			System.err.println("UnsatisfiedLinkError: " + x.getMessage());

			return null;

		}

	}

	public static String[] getWordGenerate(String word) {

		try {

			return generate(_generateId, word);

		} catch (UnsatisfiedLinkError x) {

			System.err.println("UnsatisfiedLinkError: " + x.getMessage());

			return null;

		}

	}

	public static String[] getWordForms(String word, int limit) {

		try {

			return generateWordForms(_generateWFormsId, word, limit);

		} catch (UnsatisfiedLinkError x) {

			System.err.println("UnsatisfiedLinkError: " + x.getMessage());

			return null;

		}

	}

	public static String[] getSyns(String word, int context_size) {

		try {

			return getSynonims(_stemmingId, word, context_size);

		} catch (UnsatisfiedLinkError x) {

			return null;

		}

	}

	public static String[] getSuggestions(String word) {

		try {

			return suggest(_stemmingId, word);

		} catch (UnsatisfiedLinkError x) {

			return null;

		}

	}

	/**
	 * 
	 * Cant instantiate (Singleton design pattern)
	 **/

	public Humor() {
		String lexicon_directory = new String("."); // current dir

		int lang_code = 1038;

		try {

			Humor.initialize(lexicon_directory, lang_code);

		} catch (Throwable e) {

			System.out.println("There is a problem with the Humor library: "
					+ e);

			// return;

		}

		if (_stemmingId == -100) {

			System.out.println("Could not load the \"init\"!");

			// return;

		} else if (_stemmingId == -1) {

			System.out.println("Error in \"init\"! (is lexicon path correct?)");

			// return;

		}

		// System.out.println( " _stemmingId: " + _stemmingId);

		// System.out.println( " _generateId: " + _generateId);

		// System.out.println( " _generateWFormsId: " + _generateWFormsId);
	}

	public Humor(String directory) {
		String lexicon_directory = new String(directory); // current dir

		int lang_code = 1038;

		try {

			Humor.initialize(lexicon_directory, lang_code);

		} catch (Throwable e) {

			System.out.println("There is a problem with the Humor library: "
					+ e);

			// return;

		}

		if (_stemmingId == -100) {

			System.out.println("Could not load the \"init\"!");

			// return;

		} else if (_stemmingId == -1) {

			System.out.println("Error in \"init\"! (is lexicon path correct?)");

			// return;

		}

		// System.out.println( " _stemmingId: " + _stemmingId);

		// System.out.println( " _generateId: " + _generateId);

		// System.out.println( " _generateWFormsId: " + _generateWFormsId);
	}

	// /////////////////////////////////////////////////////////////////////////////////////

	// Private

	//

	private synchronized static native int init(String directory,
			int language_code, int mode) throws UnsatisfiedLinkError;

	private synchronized static native int close(int id)
			throws UnsatisfiedLinkError;

	// private synchronized static native String [] stem(int id, String word)
	// throws UnsatisfiedLinkError;

	private synchronized static native String[] stemEx(int id, String word,
			int options) throws UnsatisfiedLinkError;

	private synchronized static native String[] generate(int id, String word)
			throws UnsatisfiedLinkError;

	private synchronized static native String[] generateWordForms(int id,
			String word, int limit) throws UnsatisfiedLinkError;

	private synchronized static native String[] getSynonims(int id,
			String word, int context_size) throws UnsatisfiedLinkError;

	private synchronized static native String[] suggest(int id, String word)
			throws UnsatisfiedLinkError;

	public String[] Tovetmegadd(String szo, boolean uws) {
		// Initialize JNI extension

		String[] stems;

		String word = szo;//
		int options = _SHOW_STEM_AND_POS | _CASE_SENSITIVE
				| _FILTER_STEM_AND_POS;

		// _SHOW_STEM_FULL;
		// _SHOW_STEM_ONLY;

		stems = Humor.getStem(word, options);
		String s1, s2;
		if (stems != null) {
			for (int i = 0; i < stems.length; i++) {
				s1 = "";
				s2 = "";
				if (stems[i].contains("[")) {
					s1 = stems[i].substring(0, stems[i].indexOf("["));
					s2 = stems[i].substring(stems[i].indexOf("["));
					s1 = s1.toLowerCase();
					stems[i] = s1 + s2;
				} else {
					System.err.println("Hiba, a szónak nincs elemzése:\n"
							+ word);
					stems[i] += "[X?]";
				}

				// stems[i]=stems[i].toLowerCase();
			}
			// System.out.println( "\nstems of \"" + word +"\"");
			int sz = 0;
			for (int i = 0; i < stems.length; i++) {
				for (int j = i; j < stems.length; j++) {
					if (i != j) {
						if (stems[i].equals(stems[j])) {
							// System.out.println(stems[i]+"----"+stems[j]);
							sz++;
							stems[i] = "-.-";
						}
					}
				}
			}
			int tmpe = 0;
			String[] tmp = new String[stems.length - sz];
			for (int i = 0; i < stems.length; i++) {
				if (!stems[i].equals("-.-")) {
					tmp[tmpe] = stems[i];
					tmpe++;
				}
			}
			stems = tmp;

		} else {
			// ha nem ismeri a szót, akkor [FN?] jelölést kap
			if (uws && (word.length() > 1)
					&& (!word.contains("&") && !word.contains(";"))) {
				stems = new String[1];
				stems[0] = word + "[X?]";
			}
		}
		// System.out.println("stems == null");

		return stems;
	}

	void lezar() {
		int err = Humor.closeMorph(_stemmingId);

		err = Humor.closeMorph(_generateId);

		err = Humor.closeMorph(_generateWFormsId);

		if (err == -100) {

			System.out.println("Could not load the \"close\"!");

			// return;

		}
	}

	public Vector<String> Toszofajmegado(Vector<String> tomb,
			boolean kellenagybetus) {
		// Initialize JNI extension

		String szo = "";
		Vector<String> tmp = new Vector<String>();
		String minden = "";

		for (int i = 0; i < tomb.size(); i++) {

			szo = tomb.get(i);
			minden = "";

			String lexicon_directory = new String("."); // current dir

			int lang_code = 1038;

			try {

				Humor.initialize(lexicon_directory, lang_code);

			} catch (Throwable e) {

				System.out
						.println("There is a problem with the Humor library: "
								+ e);

				// return;

			}

			if (_stemmingId == -100) {

				System.out.println("Could not load the \"init\"!");

				// return;

			} else if (_stemmingId == -1) {

				System.out
						.println("Error in \"init\"! (is lexicon path correct?)");

				// return;

			}

			String[] stems;

			String word = szo;//

			int options = _FILTER_STEM | _SHOW_STEM_FULL | _CASE_SENSITIVE; // "alma[FN]"

			stems = Humor.getStem(word, options);

			if (stems != null) {

				// System.out.println( "\nstems of \"" + word +"\"");

				for (int j = 0; j < stems.length; j++) {
					// System.out.println(stems[j].toString());
					if (j == 0)
						minden = stems[j];
					else
						minden += "||" + stems[j];
				}
				tmp.addElement(minden);
			}

			else {
				if (kellenagybetus) {
					// if(word.charAt(0)==word.toUpperCase().charAt(0)){
					// item tmp=new item(word,word,"[FN?]");

					// tmp.addElement(word+"[FN?]");
					// }
				}
			}
			// else{
			// tomb.addElement(word);
			// }
			// System.out.println("stems == null");

			int err = Humor.closeMorph(_stemmingId);

			err = Humor.closeMorph(_generateId);

			err = Humor.closeMorph(_generateWFormsId);

			if (err == -100) {

				System.out.println("Could not load the \"close\"!");

				// return;

			}
		}
		return tmp;
	}

	static public void main(String args[]) {

		// Initialize JNI extension

		String lexicon_directory = new String(args[0]); // current dir

		int lang_code = 1038;

		try {

			Humor.initialize(lexicon_directory, lang_code);

		} catch (Throwable e) {

			System.out.println("There is a problem with the Humor library: "
					+ e);

			return;

		}

		if (_stemmingId == -100) {

			System.out.println("Could not load the \"init\"!");

			return;

		} else if (_stemmingId == -1) {

			System.out.println("Error in \"init\"! (is lexicon path correct?)");

			return;

		}

		// System.out.println(" _stemmingId: " + _stemmingId);

		// System.out.println( " _generateId: " + _generateId);

		// System.out.println( " _generateWFormsId: " + _generateWFormsId);

		String[] stems;
		String[] syns;

		int options = _SHOW_COMPOUND_POSITIONS | _SHOW_STEM_FULL;

		Scanner s = new Scanner(System.in);

		while (s.hasNext()) {
			String word = s.nextLine();
			stems = Humor.getStem(word, options);
			if (stems != null && stems.length > 0)
				for (String ss : stems) {
					System.out.print(ss + "\t");
				}
			System.out.println();
		}

		int err = Humor.closeMorph(_stemmingId);

		err = Humor.closeMorph(_generateId);

		err = Humor.closeMorph(_generateWFormsId);

		if (err == -100) {

			System.out.println("Could not load the \"close\"!");

			return;

		}

	}

}