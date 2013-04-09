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
package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.regex.Pattern;

/**
 * Stores a tag mapping.
 * 
 * @author György Orosz
 * 
 */
public class TagMapping {
	/**
	 * 
	 * @param tagPattern
	 *            pattern of the tag, should be an unsescaped Java regexp
	 * @param replacement
	 *            replacement string
	 * 
	 */
	public TagMapping(String tagPattern, String replacement) {
		// this.tagPattern = Pattern.compile(Pattern.quote(tagPattern));
		this.tagPattern = Pattern.compile(tagPattern);
		this.replacement = replacement;
	}

	protected Pattern tagPattern;
	protected String replacement;

	public Pattern getTagPattern() {
		return tagPattern;
	}

	public String getReplacement() {
		return replacement;
	}

}
