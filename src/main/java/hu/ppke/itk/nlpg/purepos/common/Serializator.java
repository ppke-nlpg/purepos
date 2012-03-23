package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializator {
	public static void writeModel(Model<String, Integer> m, File to)
			throws IOException {
		FileOutputStream fos = new FileOutputStream(to);
		ObjectOutputStream out = new ObjectOutputStream(fos);
		out.writeObject(m);
		out.close();
		fos.close();
	}

	public static Model<String, Integer> readModel(File from)
			throws IOException, ClassNotFoundException {
		FileInputStream fis = new FileInputStream(from);
		ObjectInputStream input = new ObjectInputStream(fis);
		Model<String, Integer> model;
		model = (Model<String, Integer>) input.readObject();
		return model;
	}

}
