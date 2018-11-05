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

import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;

public class SSerializer {
	protected final static ISerializer serializer = new StandardSerializer();

	public static void writeModel(RawModel m, File to) throws Exception {
		serializer.writeModel(m, to);
	}

	public static RawModel readModel(File from) throws Exception {
		return serializer.readModel(from);
	}

	static boolean deleteModel(File f) {
		return f.delete();
	}

	public static void writeModelEx(RawModel m, String to) throws Exception {
		serializer.writeModel(m, new File(to));
	}

	public static RawModel readModelEx(String from) throws Exception {
		return serializer.readModel(new File(from));
	}

	static boolean deleteModelEx(String f) {
		return new File(f).delete();
	}
}
