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
