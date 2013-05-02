package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.model.ModelData;

import java.util.ArrayList;

public class LogLinearMLCombiner extends LogLinearCombiner {

	private static final long serialVersionUID = 3954363116292749063L;

	@Override
	public void calculateParameters(IDocument doc, RawModelData rawModeldata,
			ModelData<String, Integer> data) {
		// TODO Auto-generated method stub
		lambdas = new ArrayList<Double>();
		lambdas.add(1.0);
		lambdas.add(0.0);

	}

}
