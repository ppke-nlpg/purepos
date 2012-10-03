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
package hu.ppke.itk.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MorphologicalTable extends AbstractMorphologicalAnalyzer {
	protected File morphFile;
	protected Map<String, List<String>> morphTable;

	public MorphologicalTable(File file) throws FileNotFoundException,
			IOException {
		this.morphFile = file;

		morphTable = readFile(morphFile);

	}

	protected Map<String, List<String>> readFile(File file) throws IOException,
			FileNotFoundException {
		HashMap<String, List<String>> mTable = new HashMap<String, List<String>>();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				new FileInputStream(file)));
		String line;
		while ((line = br.readLine()) != null) {
			String[] cells = line.split("\t");
			if (cells.length > 0) {

				String token = cells[0];
				List<String> anals = new ArrayList<String>();
				for (int i = 1; i < cells.length; ++i) {
					anals.add(cells[i]);
				}
				mTable.put(token, anals);
			}
		}

		return mTable;
	}

	@Override
	public List<String> getTags(String word) {
		return morphTable.get(word);
	}

	@Override
	public List<IToken> analyze(String word) {
		// List<String> tags = morphTable.get(word);
		// if (tags == null)
		// return null;
		// List<IToken> ret = new ArrayList<IToken>();
		// for (String tag : tags) {
		// ret.add(new Token(word, word, tag));
		// }
		// return ret;
		return null;
	}

}
