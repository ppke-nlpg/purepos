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
package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.decoder.StemFilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Collection;

public class Util {
	protected static final String STEM_FILTER_FILE = "purepos_stems.txt";
	protected static final String STEM_FILTER_PROPERTY = System
			.getProperty("stems.path");

	public static boolean isUpper(String lword, String word) {
		return !lword.equals(word);
	}

	@Deprecated
	public static boolean isUpper(String word) {
		return !toLower(word).equals(word);
	}

	public static String toLower(String word) {
		return word.toLowerCase();
	}

	public static <E> boolean isNotEmpty(Collection<E> c) {
		return c != null && c.size() > 0;
	}

	public static <E> boolean isEmpty(Collection<E> c) {
		return !isNotEmpty(c);
	}

	public static StemFilter crateStemFilter() {
		File localFile = new File(STEM_FILTER_FILE);
		String path = null;
		if (STEM_FILTER_PROPERTY != null) {
			File propFile = new File(STEM_FILTER_PROPERTY);
			if (propFile.exists()) {
				path = propFile.getAbsolutePath();
			}
		}

		if (localFile.exists()) {
			path = localFile.getAbsolutePath();
		}

		if (path == null)
			return null;
		try {
			return new StemFilter(new File(path));
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
