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

import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;

/**
 * Implementors should represent a document element, which contains others.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type of the contained document elements
 */
public interface IDocElementContainer<T extends IDocElement> extends
		IDocElement, Iterable<T>, Collection<T>, RandomAccess, List<T> {
	/**
	 * Convert the representation to list of strings
	 * 
	 * @return string list
	 */
	public List<String> toList();

	/**
	 * Returns the string representation of the object. Iterable elements are
	 * separated by the separator.
	 * 
	 * @param separator
	 *            elements are separated by
	 * @return string representation
	 */
	public String toString(String separator);

}
