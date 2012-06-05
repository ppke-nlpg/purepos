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
