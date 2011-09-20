/*******************************************************************************
 * Copyright (c) 2011 György Orosz, Attila Novák, Balázs Indig
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
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
