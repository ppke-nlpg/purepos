package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.common.lemma.ILemmaTransformation;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModelData;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModelData;

import java.io.Serializable;
import java.util.List;

public interface ICombiner extends Serializable {

	public List<Double> getParameters();

	public void calculateParameters(IDocument doc, RawModelData rawModeldata,
			ModelData<String, Integer> data);

	@Deprecated
	public Double combine(Double... logProbs);

	public Double combine(IToken tok, ILemmaTransformation<String, Integer> t,
			CompiledModelData<String, Integer> compiledModelData,
			ModelData<String, Integer> modelData);
}
