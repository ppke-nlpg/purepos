package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.io.Serializable;

import org.apache.commons.lang3.tuple.Pair;

public interface ILemmaTransformation<W, T extends Comparable<T>> extends
		Serializable {
	public Pair<String, T> analyze(W word);

	public int minimalCutLength();

	public IToken convert(W word, IVocabulary<W, T> vocab);

	@Override
	public String toString();

	@Override
	public boolean equals(Object other);

	@Override
	public int hashCode();

}
