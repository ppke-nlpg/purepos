package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.ILexicon;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;
import hu.ppke.itk.nlpg.purepos.model.Model;

import java.util.Map;

public class CompiledModel<W, T extends Comparable<T>> extends Model<W, T> {

	CompiledModel(int taggingOrder, int emissionOrder, int suffixLength,
			int rareFrequency, IProbabilityModel<T, T> tagTransitionModel,
			IProbabilityModel<T, W> standardEmissionModel,
			IProbabilityModel<T, W> specTokensEmissionModel,
			ISuffixGuesser<W, T> lowerCaseSuffixGuesser,
			ISuffixGuesser<W, T> upperCaseSuffixGuesser,
			HashLemmaTree lemmaTree, ILexicon<W, T> standardTokensLexicon,
			ILexicon<W, T> specTokensLexicon,
			IVocabulary<String, T> tagVocabulary, Map<T, Double> aprioriTagProbs) {
		super(taggingOrder, emissionOrder, suffixLength, rareFrequency,
				standardTokensLexicon, specTokensLexicon, tagVocabulary);

		this.tagTransitionModel = tagTransitionModel;
		this.standardEmissionModel = standardEmissionModel;
		this.specTokensEmissionModel = specTokensEmissionModel;
		this.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
		this.upperCaseSuffixGuesser = upperCaseSuffixGuesser;
		this.lemmaTree = lemmaTree;

		this.aprioriTagProbs = aprioriTagProbs;
	}

	private static final long serialVersionUID = -3426883448646064198L;

	protected IProbabilityModel<T, T> tagTransitionModel;

	protected IProbabilityModel<T, W> standardEmissionModel;

	protected IProbabilityModel<T, W> specTokensEmissionModel;

	protected ISuffixGuesser<W, T> lowerCaseSuffixGuesser;

	protected ISuffixGuesser<W, T> upperCaseSuffixGuesser;

	protected Map<T, Double> aprioriTagProbs;

	protected HashLemmaTree lemmaTree;

	public HashLemmaTree getLemmaTree() {
		return lemmaTree;
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
	 * @return the aprioriTagProbs
	 */
	public Map<T, Double> getAprioriTagProbs() {
		return aprioriTagProbs;
	}
}
