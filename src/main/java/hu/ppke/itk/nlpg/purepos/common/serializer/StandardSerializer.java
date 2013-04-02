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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class StandardSerializer implements ISerializer {

	@Override
	public RawModel readModel(File from) throws Exception {
		FileInputStream fis = new FileInputStream(from);
		ObjectInputStream input = new ObjectInputStream(fis);
		RawModel model;
		model = (RawModel) input.readObject();
		input.close();
		return model;
	}

	@Override
	public void writeModel(RawModel m, File to) throws IOException {
		FileOutputStream fos = new FileOutputStream(to);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(m);
		out.close();
		fos.close();
	}

}
