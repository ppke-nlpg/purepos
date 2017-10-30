package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.IntVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.Lexicon;

import java.io.Serializable;

public class ModelData<W, T extends Comparable<T>> implements Serializable {

	private static final long serialVersionUID = -7850119447574169739L;

	public int taggingOrder;
	public int emissionOrder;
	public int suffixLength;
	public int rareFreqency;
	public ILexicon<W, T> standardTokensLexicon;
	public ILexicon<W, T> specTokensLexicon;
	public IVocabulary<String, T> tagVocabulary;
	public T eosIndex;
	public T bosIndex;
	public String lemmaTransformationType;
	public int lemmaThreshold;

	public static final String EOS_TAG = "</S>";
	public static final String BOS_TAG = "<S>";
	public static final String EOS_TOKEN = "<SE>";
	public static final String BOS_TOKEN = "<SB>";

	public static ModelData<String, Integer> create(int taggingOrder_,
			int emissionOrder_, int suffixLength_, int rareFreqency_) {
		ModelData<String, Integer> ret = new ModelData<String, Integer>(
				taggingOrder_, emissionOrder_, suffixLength_, rareFreqency_,
				new Lexicon<String, Integer>(), new Lexicon<String, Integer>(),
				new IntVocabulary<String>());
		return ret;

	}

	protected ModelData(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFreqency, ILexicon<W, T> standardTokensLexicon,
			ILexicon<W, T> specTokensLexicon,
			IVocabulary<String, T> tagVocabulary) {
		this.taggingOrder = taggingOrder;
		this.emissionOrder = emissionOrder;
		this.suffixLength = suffixLength;
		this.rareFreqency = rareFreqency;
		this.standardTokensLexicon = standardTokensLexicon;
		this.specTokensLexicon = specTokensLexicon;
		this.tagVocabulary = tagVocabulary;

		this.eosIndex = tagVocabulary.addElement(ModelData.EOS_TAG);
		this.bosIndex = tagVocabulary.addElement(ModelData.BOS_TAG);

	}

	@Deprecated
	protected ModelData() {

	}

}