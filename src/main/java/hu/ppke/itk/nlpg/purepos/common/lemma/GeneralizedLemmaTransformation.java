package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.GeneralizedLemmaTransformation.Transformation;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public class GeneralizedLemmaTransformation extends
		AbstractLemmaTransformation<Transformation> {

	public GeneralizedLemmaTransformation(String word, String lemma, Integer tag) {
		super(word, lemma, tag);
	}

	private static final long serialVersionUID = -2376160223585239419L;

	public class Transformation implements Serializable {

		private static final long serialVersionUID = 8291301251800430106L;

		public final int removeStart;
		public final int removeEnd;
		public final String addStart;
		public final String addEnd;

		public final Integer tag;
		public final boolean toLower;

		private final String strRep;
		private final int hashCode;

		public Transformation(int removeStart, int removeEnd, String addStart,
				String addEnd, Integer tag, boolean toLower) {
			super();
			this.removeStart = removeStart;
			this.removeEnd = removeEnd;
			this.addStart = addStart;
			this.addEnd = addEnd;
			this.tag = tag;
			this.toLower = toLower;

			String l = toLower ? "_" : "-";
			this.strRep = "(" + l + ",< -" + removeStart + "+'" + addStart
					+ "', >-" + removeEnd + "+'" + addEnd + "' -" + tag + ")";
			this.hashCode = this.strRep.hashCode();
		}

		public String toString() {
			return strRep;
		}

		@Override
		public int hashCode() {
			return this.hashCode;
		}

		@Override
		public boolean equals(Object other) {
			if (other instanceof Transformation) {
				Transformation o = (Transformation) other;
				return this.removeEnd == o.removeEnd
						&& this.removeStart == o.removeStart
						&& this.addStart.equals(o.addStart)
						&& this.addEnd.equals(o.addEnd)
						&& this.tag.equals(o.tag);
			} else
				return false;

		}
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

		// ignore case
		str1 = str1.toLowerCase();
		str2 = str2.toLowerCase();

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
	public int minimalCutLength() {
		return representation.removeEnd;
	}

	protected boolean lowerTransformed(String word, String lemma) {
		if (word.length() > 0 && lemma.length() > 0) {
			String ws = word.substring(0, 1), ls = lemma.substring(0, 1);
			boolean isWordUpper = ws.toUpperCase().equals(ws);
			boolean isLemmaLower = ls.toLowerCase().equals(ls);
			return isWordUpper && isLemmaLower;
		} else
			return false;

	}

	@Override
	protected Transformation decode(String word, String lemma, Integer tag) {
		Pair<Integer, Integer> posWord_Lemma = longestSubstring(word, lemma);
		Pair<Integer, Integer> posLemma_Word = longestSubstring(lemma, word);
		boolean lowered = lowerTransformed(word, lemma);
		if (posWord_Lemma.getRight() < 2) {
			return new Transformation(0, word.length(), "", lemma, tag, lowered);
		}

		int removeStart = posWord_Lemma.getLeft();
		int removeEnd = word.length()
				- (posWord_Lemma.getLeft() + posWord_Lemma.getRight());
		String addStart = lemma.substring(0, posLemma_Word.getLeft());
		String addEnd = lemma.substring(posLemma_Word.getLeft()
				+ posLemma_Word.getRight());
		return new Transformation(removeStart, removeEnd, addStart, addEnd,
				tag, lowered);
	}

	@Override
	protected Pair<String, Integer> encode(String word,
			Transformation representation) {
		boolean upperWord = Util.isUpper(word);
		// try {
		int subEnd = Math.max(0, word.length() - representation.removeEnd);
		String lemma = word.substring(0, subEnd) + representation.addEnd;
		lemma = representation.addStart
				+ lemma.substring(Math.min(representation.removeStart,
						lemma.length()));
		lemma = lemma.toLowerCase();
		if (upperWord && !representation.toLower && lemma.length() > 0) {
			lemma = lemma.substring(0, 1).toUpperCase() + lemma.substring(1);
		}
		return Pair.of(lemma, representation.tag);
		// } catch (Exception e) {
		// System.err.println(word);
		// System.err.println(representation);
		// System.err.println(e.getMessage());
		// }
		// return null;
	}
}
