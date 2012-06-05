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

}
