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
import hu.ppke.itk.nlpg.docmodel.internal.Token;

import java.util.ArrayList;
import java.util.List;

import com.morphologic.humor.JAnalHumor;
import com.morphologic.humor.MorphAnalysis;
import com.morphologic.lib.InitException;

/***
 * Wrapper class around Humor for only getting POS tags
 * 
 * @author György Orosz
 * 
 */
public class HumorAnalyzer extends AbstractMorphologicalAnalyzer {
	private static IMorphologicalAnalyzer hInstance = null;
	private final JAnalHumor humor;

	private HumorAnalyzer() throws InitException {
		humor = new JAnalHumor();
	}

	// private HumorAnalyzer(String lexPath) {
	// humor = new JAnalHumor(lexPath);
	// }

	@Override
	public List<String> getTags(String word) {
		return humor.getTags(word);
	}

	@Override
	public List<IToken> analyze(String word) {
		List<IToken> tokens = new ArrayList<IToken>();
		List<MorphAnalysis> anals = humor.getAnalysises(word);
		if (anals == null)
			return null;
		for (MorphAnalysis a : anals) {
			tokens.add(new Token(word, a.getStem(), a.getTag()));
		}
		return tokens;
	}

	public static IMorphologicalAnalyzer getInstance() throws InitException {
		if (hInstance == null)
			hInstance = new HumorAnalyzer();
		return hInstance;
	}

}
