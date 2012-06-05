package hu.ppke.itk.nlpg.purepos.common.serializer;

import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;

public interface ISerializer {
	public RawModel readModel(File from) throws Exception;

	public void writeModel(RawModel m, File to) throws Exception;
}
