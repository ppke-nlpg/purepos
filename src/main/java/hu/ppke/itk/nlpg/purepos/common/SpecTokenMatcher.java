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

import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class for matching special tokens.
 * 
 * @author György Orosz
 * 
 */
public class SpecTokenMatcher implements ISpecTokenMatcher {

	protected LinkedHashMap<String, Pattern> patterns = new LinkedHashMap<String, Pattern>();


	/**
	 * Initialize patterns from HunPos
	 */
	public SpecTokenMatcher() {
		addPattern("@CARD", "^[0-9]+$");
		addPattern("@CARDPUNCT", "^[0-9]+\\.$");
		addPattern("@CARDSEPS", "^[0-9\\.,:-]+[0-9]+$");
		addPattern("@CARDSUFFIX", "^[0-9]+[a-zA-Z][a-zA-Z]?[a-zA-Z]?$");
		addPattern("@HTMLENTITY", "^&[^;]+;?$");
		addPattern("@PUNCT", "^\\pP+$");
		// addPattern("@ABBREV",
		// "^[A-Za-zöüóőúéáűíÖÜÓŐÚÉÁŰÍ][öüóőúéáűía-z]*\\.$");
	}

	@Override
	public String matchLexicalElement(String token) {
		for (Entry<String, Pattern> pattern : patterns.entrySet()) {
			Matcher m = pattern.getValue().matcher(token);
			if (m.matches())
				return pattern.getKey();
		}
		return null;
	}

	protected void addPattern(String name, String pattern) {
		patterns.put(name, Pattern.compile(pattern, Pattern.UNICODE_CHARACTER_CLASS));
	}

}
