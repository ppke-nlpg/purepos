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
package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.ISentence;

import java.io.PrintStream;
import java.util.List;
import java.util.Scanner;

/**
 * Interface for a POS tagger implementation
 * 
 * @author György Orosz
 * 
 */
public interface ITagger {


	public ISentence tagSentence(List<String> sentence);

	public List<ISentence> tagSentence(List<String> sentence,
			int maxResultsNumber);

	public ISentence tagSentence(String sentence);

	public List<ISentence> tagSentence(String sentence, int maxResultsNumber);

	public void tag(Scanner scanner, String inputFormat, PrintStream ps, String outputFormat);

	public void tag(Scanner scanner, String inputFormat, PrintStream ps, String outputFormat, int maxResultsNumber);

}
