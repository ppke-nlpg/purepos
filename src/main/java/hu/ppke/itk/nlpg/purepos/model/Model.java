package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.HashLemmaTree;

import java.io.Serializable;
import java.util.Map;

/**
 * An object of this class is representing the model of a POS tagger.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type parameter for representing tags
 * 
 * @param <W>
 *            type parameter for representing words
 * 
 */
public abstract class Model<W, T extends Comparable<T>> implements Serializable {

	private static final long serialVersionUID = -8584335542969140286L;

	protected static final String EOS_TAG = "</S>";

	protected static final String BOS_TAG = "<S>";

	protected static final String EOS_TOKEN = "<SE>";

	protected static final String BOS_TOKEN = "<SB>";

	protected int taggingOrder;

	protected int emissionOrder;

	protected int suffixLength;

	protected int rareFreqency;

	protected IProbabilityModel<T, T> tagTransitionModel;

	protected IProbabilityModel<T, W> standardEmissionModel;

	protected IProbabilityModel<T, W> specTokensEmissionModel;

	protected ILexicon<W, T> standardTokensLexicon;

	protected ILexicon<W, T> specTokensLexicon;

	protected IVocabulary<String, T> tagVocabulary;

	protected ISuffixGuesser<W, T> lowerCaseSuffixGuesser;

	protected ISuffixGuesser<W, T> upperCaseSuffixGuesser;

	protected T eosIndex;

	protected T bosIndex;

	// protected double theta;

	protected Map<Integer, Double> aprioriTagProbs;

	protected HashLemmaTree lemmaTree;

	public HashLemmaTree getLemmaTree() {
		return lemmaTree;
	}

	/**
	 * @return the eosTag
	 */
	public static String getEOSTag() {
		return EOS_TAG;
	}

	/**
	 * @return the bosTag
	 */
	public static String getBOSTag() {
		return BOS_TAG;
	}

	/**
	 * @return the eosToken
	 */
	public static String getEOSToken() {
		return EOS_TOKEN;
	}

	/**
	 * @return the bosToken
	 */
	public static String getBOSToken() {
		return BOS_TOKEN;
	}

	/**
	 * @return the taggingOrder
	 */
	public int getTaggingOrder() {
		return taggingOrder;
	}

	/**
	 * @return the emissionOrder
	 */
	public int getEmissionOrder() {
		return emissionOrder;
	}

	/**
	 * @return the suffixLength
	 */
	public int getSuffixLength() {
		return suffixLength;
	}

	/**
	 * @return the rareFreqency
	 */
	public int getRareFreqency() {
		return rareFreqency;
	}

	/**
	 * @return the tagTransitionModel
	 */
	public IProbabilityModel<T, T> getTagTransitionModel() {
		return tagTransitionModel;
	}

	/**
	 * @return the standardEmissionModel
	 */
	public IProbabilityModel<T, W> getStandardEmissionModel() {
		return standardEmissionModel;
	}

	/**
	 * @return the specTokensEmissionModel
	 */
	public IProbabilityModel<T, W> getSpecTokensEmissionModel() {
		return specTokensEmissionModel;
	}

	/**
	 * @return the standardTokensLexicon
	 */
	public ILexicon<W, T> getStandardTokensLexicon() {
		return standardTokensLexicon;
	}

	/**
	 * @return the specTokensLexicon
	 */
	public ILexicon<W, T> getSpecTokensLexicon() {
		return specTokensLexicon;
	}

	/**
	 * @return the tagVocabulary
	 */
	public IVocabulary<String, T> getTagVocabulary() {
		return tagVocabulary;
	}

	/**
	 * @return the lowerCaseSuffixGuesser
	 */
	public ISuffixGuesser<W, T> getLowerCaseSuffixGuesser() {
		return lowerCaseSuffixGuesser;
	}

	/**
	 * @return the upperCaseSuffixGuesser
	 */
	public ISuffixGuesser<W, T> getUpperCaseSuffixGuesser() {
		return upperCaseSuffixGuesser;
	}

	/**
	 * @return the eosIndex
	 */
	public T getEOSIndex() {
		return eosIndex;
	}

	/**
	 * @return the bosIndex
	 */
	public T getBOSIndex() {
		return bosIndex;
	}

	/**
	 * @return the aprioriTagProbs
	 */
	public Map<Integer, Double> getAprioriTagProbs() {
		return aprioriTagProbs;
	}

}
