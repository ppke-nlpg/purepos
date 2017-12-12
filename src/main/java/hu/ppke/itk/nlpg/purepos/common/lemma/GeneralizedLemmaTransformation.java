package hu.ppke.itk.nlpg.purepos.common.lemma;

import org.apache.commons.lang3.tuple.Pair;

public class GeneralizedLemmaTransformation extends
		AbstractLemmaTransformation<Pair<String,Integer>> {

	private static final long serialVersionUID = -2376160223585239419L;
	protected static int threshold = 2;

	public GeneralizedLemmaTransformation(String word, String lemma, Integer tag) {
		super(word, lemma, tag);
	}

	public GeneralizedLemmaTransformation(String word, String lemma, Integer tag, int threshold) {
		super(word, lemma, tag, threshold);
		this.threshold = threshold;
		representation = decode(word, lemma, tag);
	}


	/**
	 * Calculates the longest substring efficiently.
	 * 
	 * See http://karussell.wordpress.com/2011/04/14/longest-common-substring-
	 * algorithm-in-java/
	 * 
	 * @param str1
	 * @param str2
	 * @return start position and length
	 */
	public static Pair<Integer, Integer> longestSubstring(String str1,
			String str2) {

		StringBuilder sb = new StringBuilder();
		if (str1 == null || str1.isEmpty() || str2 == null || str2.isEmpty())
			return Pair.of(0, 0);

		// java initializes them already with 0
		int[][] num = new int[str1.length()][str2.length()];
		int maxlen = 0;
		int lastSubsBegin = 0;

		for (int i = 0; i < str1.length(); i++) {
			for (int j = 0; j < str2.length(); j++) {
				if (str1.charAt(i) == str2.charAt(j)) {
					if ((i == 0) || (j == 0))
						num[i][j] = 1;
					else
						num[i][j] = 1 + num[i - 1][j - 1];

					if (num[i][j] > maxlen) {
						maxlen = num[i][j];
						// generate substring from str1 => i
						int thisSubsBegin = i - num[i][j] + 1;
						if (lastSubsBegin == thisSubsBegin) {
							// if the current LCS is the same as the last time
							// this block ran
							sb.append(str1.charAt(i));
						} else {
							// this block resets the string builder if a
							// different LCS is found
							lastSubsBegin = thisSubsBegin;
							sb = new StringBuilder();
							sb.append(str1.substring(lastSubsBegin, i + 1));
						}
					}
				}
			}
		}

		return Pair.of(lastSubsBegin, sb.length());
	}


	@Override
	protected Pair<String, Long> specifyParameters(String word, String lemma, Integer tag, Integer casing) {
		int removeStart;
		int removeEnd;
		String addStart = "";
		String addEnd = "";
		Pair<Integer, Integer> posWord_Lemma = longestSubstring(word, lemma);
		
		if (posWord_Lemma.getRight() < this.threshold) {
			removeStart = 0;
			removeEnd = word.length();
			addStart = "";
			addEnd = lemma;
		} else {
			Pair<Integer, Integer> posLemma_Word = longestSubstring(lemma, word);
			removeStart = posWord_Lemma.getLeft();
			removeEnd = word.length() - (removeStart + posWord_Lemma.getRight());
			addStart = lemma.substring(0, posLemma_Word.getLeft());
			addEnd = lemma.substring(posLemma_Word.getLeft()+posLemma_Word.getRight());
		}

		long code = Transformation.createCode(tag,casing,removeStart,removeEnd,addEnd.length());
		String lemmaStuff = addStart+addEnd;
		return Pair.of(lemmaStuff, code);
	}
}
