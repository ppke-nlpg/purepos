package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModelData;

import java.io.Serializable;
import java.util.List;

public interface ICombiner extends Serializable {

	public List<Double> getParameters();

	public void calculateParameters(IDocument doc, RawModelData rawModeldata,
			ModelData<String, Integer> data);

	public Double combine(Double... logProbs);
}
