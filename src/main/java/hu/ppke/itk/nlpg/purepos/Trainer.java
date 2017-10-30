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

import hu.ppke.itk.nlpg.corpusreader.ICorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Standard POS training implementation.
 * 
 * @author György Orosz
 * 
 */
public class Trainer implements ITrainer {
	protected Statistics stat;
	protected IDocument document;
	protected ICorpusReader<IDocument> reader;

	public Trainer(File f, ICorpusReader<IDocument> reader)
			throws FileNotFoundException, ParsingException {
		this.reader = reader;
		document = readCorpus(f);
	}

	public Trainer(Scanner sc, ICorpusReader<IDocument> reader)
			throws ParsingException {
		this.reader = reader;
		this.document = readCorpus(sc);
	}

	protected IDocument readCorpus(File file) throws FileNotFoundException,
			ParsingException {
		return reader.readFromFile(file);

	}

	protected IDocument readCorpus(Scanner sc) throws ParsingException {
		return this.reader.readFromScanner(sc);

	}

	public Trainer(IDocument document) {
		this.document = document;
	}

	@Override
	public Statistics getStat() {
		return stat;
	}

	@Override
	public RawModel trainModel(int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency,String lemmaTransformationType, int lemmaThreshold ) {
		RawModel m = new RawModel(tagOrder, emissionOrder, maxSuffixLength,
				rareFrequency);
		m.setLemmaVariables(lemmaTransformationType,lemmaThreshold);
		return trainModel(m);
	}

	@Override
	public RawModel trainModel(RawModel m) {
		m.train(document);
		stat = m.getLastStat();
		return m;
	}

}
