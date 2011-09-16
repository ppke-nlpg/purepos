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

/**
 * Wraps an exception into another
 * 
 * @author György Orosz
 * 
 */
public class ParsingException extends Exception {
	private final Exception exception;

	private static final long serialVersionUID = -1406009981991160401L;

	public ParsingException(String message) {
		exception = new Exception(message);
	}

	/**
	 * 
	 * @param e
	 *            wrapped exception
	 */
	public ParsingException(Exception e) {
		this.exception = e;
	}

	/**
	 * Returns the wrapped excpetion
	 * 
	 * @return
	 */
	public Exception getWrappedException() {
		return exception;
	}

	@Override
	public void printStackTrace() {
		exception.printStackTrace();
	}
}
