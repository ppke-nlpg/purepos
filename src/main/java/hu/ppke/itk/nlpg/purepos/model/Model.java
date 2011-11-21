package hu.ppke.itk.nlpg.purepos.model;

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
public abstract class Model<W, T> {
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

	// protected double[] aprioriTagProbs;

}
