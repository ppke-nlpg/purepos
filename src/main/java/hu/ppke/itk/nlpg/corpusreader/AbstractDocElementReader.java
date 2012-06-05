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
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Abstract class for reading a coprus
 * 
 * @author György Orosz
 * 
 * @param <C>
 *            DocElement type which is read
 */
public abstract class AbstractDocElementReader<C extends IDocElement>
		implements ICorpusReader<C> {

	protected String lineSeparator = "\n";// System.getProperty("line.separator");
	protected String fileEncoding = "UTF-8";
	protected String separator;

	@Override
	public C readFromFile(File file) throws ParsingException {
		String text;
		try {
			text = readFile(file);
			return read(text);
		} catch (FileNotFoundException e) {
			throw new ParsingException(e);
		}

	}

	@Override
	public C readFromScanner(Scanner sc) throws ParsingException {
		String text;

		text = readScanner(sc);
		return read(text);

	}

	/**
	 * Sets the encoding for reading a text file. The default is UTF-8.
	 * 
	 * @param enc
	 *            file encoding
	 */
	public void setEncoding(String enc) {
		this.fileEncoding = enc;
	}

	/**
	 * Sets the line separator string for reading. This character is appended
	 * after each line during the reading.
	 * 
	 * @param sep
	 *            line end marker character
	 */
	public void setLineSeparator(String sep) {
		this.lineSeparator = sep;
	}

	/**
	 * Reads the text content from a file.
	 * 
	 * @param file
	 * @return
	 * @throws FileNotFoundException
	 */
	protected String readFile(File file) throws FileNotFoundException {
		Scanner scanner = new Scanner(file, fileEncoding);
		return readScanner(scanner);

	}

	protected String readScanner(Scanner scanner) {
		StringBuffer ret = new StringBuffer();
		while (scanner.hasNext()) {
			ret.append(scanner.nextLine() + lineSeparator);
		}
		return ret.toString();
	}

}
