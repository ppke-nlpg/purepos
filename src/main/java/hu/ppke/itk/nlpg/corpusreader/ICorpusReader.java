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
package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IDocElement;

import java.io.File;
import java.util.Scanner;

/**
 * Implementors class should be able to build a docmodel from a corpus (part).
 * 
 * @author György Orosz
 * 
 * @param <C>
 *            type of the element which is read
 */
public interface ICorpusReader<C extends IDocElement> {
	/**
	 * Reads a text into the container object
	 * 
	 * @param text
	 *            the text which is read
	 * @return
	 * @throws ParsingException
	 */
	public C read(String text) throws ParsingException;

	/**
	 * Reads a file into the container object
	 * 
	 * @param file
	 *            the file which is read
	 * @return
	 * @throws ParsingException
	 */
	public C readFromFile(File file) throws ParsingException;

	/**
	 * Reads into the container object
	 * 
	 * @param scanner
	 *            scanner object which is read
	 * @return
	 * @throws ParsingException
	 */
	public C readFromScanner(Scanner scanner) throws ParsingException;

}
