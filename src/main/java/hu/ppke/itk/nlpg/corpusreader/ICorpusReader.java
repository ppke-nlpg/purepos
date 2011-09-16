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
package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.api.IDocElement;

import java.io.File;

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

}
