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
package hu.ppke.itk.nlpg.purepos.common.serializer;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;

import junit.framework.Assert;

import org.junit.Test;

@SuppressWarnings("static-access")
public class SerializerTest {
	@Test
	public void readWriteTest() throws Exception {
		CorpusReader r = new CorpusReader();
		IDocument d = r
				.read("Michael#Michael#[FN][NOM] Karaman#??Karaman#[FN][NOM]"
						+ " ,#,#[PUNCT] az#az#[DET] Ann#Ann#[FN][NOM] 1#1#[SZN][NOM]");
		RawModel model = new RawModel(3, 3, 10, 10);
		model.train(d);
		String pathname = "./_test.model";
		File f = new File(pathname);
		SSerializer.writeModel(model, f);
		RawModel readModel = SSerializer.readModel(f);

		// TODO: write equality test case, now it is enough that it doesn't fail
		String modelTagVocab = model.getTagVocabulary().toString();
		String readTagVocab = readModel.getTagVocabulary().toString();
		// System.out.println(modelTagVocab);
		// System.out.println(readTagVocab);
		Assert.assertEquals(modelTagVocab.length(), readTagVocab.length());
		Assert.assertEquals(model.getEmissionOrder(),
				readModel.getEmissionOrder());
		Assert.assertEquals(model.getRareFreqency(),
				readModel.getRareFreqency());
		Assert.assertEquals(model.getSuffixLength(),
				readModel.getSuffixLength());
		Assert.assertEquals(model.getTaggingOrder(),
				readModel.getTaggingOrder());
		Assert.assertEquals(model.getBOSIndex(), readModel.getBOSIndex());
		Assert.assertEquals(model.getEOSIndex(), readModel.getEOSIndex());
		Assert.assertEquals(model.getEOSTag(), readModel.getEOSTag());
		Assert.assertEquals(model.getEOSToken(), readModel.getEOSToken());
		Assert.assertEquals(model.getBOSTag(), readModel.getBOSTag());
		Assert.assertEquals(model.getBOSToken(), readModel.getBOSToken());
		Assert.assertEquals(model.getLastStat(), readModel.getLastStat());
		Assert.assertEquals(model.getSpecTokensLexicon().size(), readModel
				.getSpecTokensLexicon().size());
		Assert.assertEquals(model.getStandardTokensLexicon().size(), readModel
				.getStandardTokensLexicon().size());

		SSerializer.deleteModel(f);
	}

	@Test
	public void incrementalTest() throws Exception {
		CorpusReader r = new CorpusReader();
		IDocument d1 = r
				.read("Michael#Michael#[FN][NOM] Karaman#??Karaman#[FN][NOM]"
						+ " ,#,#[PUNCT] az#az#[DET] Ann#Ann#[FN][NOM] 1#1#[SZN][NOM]");
		IDocument d2 = r
				.read("Ez#ez#[FN|NM][NOM] volt#van#[IGE][Me3] a#a#[DET]"
						+ " legszebb#szép#[FF][MN][_FOK][NOM]"
						+ " estém#este#[FN][PSe1][NOM] .#.#[PUNCT] ");
		RawModel model = new RawModel(3, 3, 10, 10);
		model.train(d1);
		Assert.assertEquals(6, model.getStandardTokensLexicon().size());
		String pathname = "./_test.model";
		File f = new File(pathname);
		SSerializer.writeModel(model, f);
		RawModel readModel = SSerializer.readModel(f);
		readModel.train(d2);
		Assert.assertEquals(12, readModel.getStandardTokensLexicon().size());
		SSerializer.deleteModel(f);
	}
}
