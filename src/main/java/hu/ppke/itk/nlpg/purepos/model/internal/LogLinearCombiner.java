package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.ICombiner;

import java.util.List;

public abstract class LogLinearCombiner implements ICombiner {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3056205583251836854L;

	protected List<Double> lambdas;

	@Override
	public List<Double> getParameters() {
		return lambdas;
	}

	@Override
	public Double combine(Double... logProbs) {
		Double lambda1 = lambdas.get(0), lambda2 = lambdas.get(1);
		Double mp1 = logProbs[0], mp2 = logProbs[1];
		return mp1 * lambda1 + mp2 * lambda2;
	}

}
