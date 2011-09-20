package hu.ppke.itk.nlpg.purepos.model;

/**
 * An object of this class is represnting the model of a POS tagger.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type parameter for representing tags
 * 
 * @param <T>
 *            type parameter for representing words
 * 
 */
public class Model<W, T> {

	protected int taggingOrder;

	protected int emissionOrder;

	protected ITagTransitionProbModel<T> tagTransitionModel;

	protected IEmissionProbModel<T> standardEmissionModel;

	protected IEmissionProbModel<T> specTokensEmissionModel;

	protected ILexicon<W, T> standardTokensLexicon;

	protected ILexicon<W, T> specTokensLexicon;

	protected IVocabulary<String, T> tagVocabulary;

	protected ISuffixGuesser lowerCaseSuffixGuesser;

	protected ISuffixGuesser upperCaseSuffixGuesser;

	protected int eosIndex;

	protected int bosIndex;

	protected float theta;

	protected float[] aprioriTagProbs;
}
