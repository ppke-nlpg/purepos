package hu.ppke.itk.nlpg.purepos.common;

import java.util.Collection;

public class Util {

	public static boolean isUpper(String word) {
		return !toLower(word).equals(word);
	}

	public static String toLower(String word) {
		return word.toLowerCase();
	}

	public static <E> boolean isNotEmpty(Collection<E> c) {
		return c != null && c.size() > 0;
	}
}
