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

import hu.ppke.itk.nlpg.purepos.model.ILexicon;
import hu.ppke.itk.nlpg.purepos.model.INGramModel;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.DoubleTrieNode;
import hu.ppke.itk.nlpg.purepos.model.internal.HashLemmaTree;
import hu.ppke.itk.nlpg.purepos.model.internal.HashSuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.internal.HashSuffixTree;
import hu.ppke.itk.nlpg.purepos.model.internal.IntTrieNode;
import hu.ppke.itk.nlpg.purepos.model.internal.IntVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.Lexicon;
import hu.ppke.itk.nlpg.purepos.model.internal.NGram;
import hu.ppke.itk.nlpg.purepos.model.internal.NGramModel;
import hu.ppke.itk.nlpg.purepos.model.internal.POSTaggerModel;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.model.internal.TrieNode;
import hu.ppke.itk.nlpg.purepos.model.internal.Vocabulary;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.objenesis.strategy.SerializingInstantiatorStrategy;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.HashBiMap;

public class KryoSerializer implements ISerializer {
	protected Kryo createKryo() {
		Kryo kryo = new Kryo();
		kryo.setInstantiatorStrategy(new SerializingInstantiatorStrategy());
		kryo.register(ILexicon.class);
		kryo.register(INGramModel.class);
		kryo.register(IProbabilityModel.class);
		kryo.register(ISpecTokenMatcher.class);
		kryo.register(ISuffixGuesser.class);
		kryo.register(IVocabulary.class);
		kryo.register(Model.class);
		kryo.register(SuffixGuesser.class);
		kryo.register(SuffixTree.class);

		kryo.register(CompiledModel.class);
		kryo.register(DoubleTrieNode.class);
		kryo.register(HashLemmaTree.class);
		kryo.register(HashSuffixGuesser.class);
		kryo.register(HashSuffixTree.class);
		kryo.register(IntTrieNode.class);
		kryo.register(IntVocabulary.class);
		kryo.register(Lexicon.class);
		kryo.register(NGram.class);
		kryo.register(NGramModel.class);
		kryo.register(POSTaggerModel.class);
		kryo.register(RawModel.class);
		kryo.register(TrieNode.class);
		kryo.register(Vocabulary.class);

		kryo.register(Pair.class);
		kryo.register(MutablePair.class);
		kryo.register(HashBiMap.class);

		return kryo;

	}

	@Override
	public RawModel readModel(File from) throws Exception {
		Kryo kryo = createKryo();
		Input input = new Input(new FileInputStream(from));
		RawModel model = kryo.readObject(input, RawModel.class);
		input.close();
		return model;
	}

	@Override
	public void writeModel(RawModel m, File to) throws Exception {
		Kryo kryo = createKryo();
		Output output = new Output(new FileOutputStream(to));
		kryo.writeObject(output, m);
		output.close();
	}
}
