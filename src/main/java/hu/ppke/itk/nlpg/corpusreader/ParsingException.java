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
