package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializator {
	public static void writeModel(RawModel m, File to) throws IOException {
		FileOutputStream fos = new FileOutputStream(to);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(m);
		out.close();
		fos.close();
	}

	static boolean deleteModel(File f) {
		return f.delete();
	}

	public static RawModel readModel(File from) throws IOException,
			ClassNotFoundException {
		FileInputStream fis = new FileInputStream(from);
		ObjectInputStream input = new ObjectInputStream(fis);
		RawModel model;
		model = (RawModel) input.readObject();
		return model;
	}

}
