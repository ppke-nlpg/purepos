package hu.ppke.itk.nlpg.purepos.model;

import java.io.Serializable;
import java.util.List;

public interface ICombiner extends Serializable {
	public List<Double> getLambdas();
}
