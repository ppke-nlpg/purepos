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
package hu.ppke.itk.nlpg.docmodel;

/**
 * Represents a token of a sentence.
 * 
 * @author György Orosz
 * 
 */
public interface IToken extends IDocElement {
	/**
	 * Returns the token string
	 * 
	 * @return token string
	 */
	public String getToken();

	/**
	 * REturns the POS tag.
	 * 
	 * @return POS tag
	 */
	public String getTag();

	/**
	 * Returns the stem of the token if exists.
	 * 
	 * @return stem of the word
	 */
	public String getStem();

	/**
	 * Returns the string representation of the token. Including the tag and the
	 * stem as well
	 * 
	 * @param separator
	 *            string which separates the stem, token and the tag
	 * @return string representation of the token
	 */
	public String toString(String separator);
}
