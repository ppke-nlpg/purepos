package hu.ppke.itk.nlpg.purepos.common.lemma;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public interface ILemmaTransformation<W, T extends Comparable<T>> extends
		Serializable {
	public Pair<String, Integer> toAnalysis(W word);

	@Override
	public String toString();

	@Override
	public boolean equals(Object other);

	@Override
	public int hashCode();

}
