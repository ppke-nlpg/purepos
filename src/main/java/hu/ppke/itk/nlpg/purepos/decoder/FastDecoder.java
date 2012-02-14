package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;
import hu.ppke.itp.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.log4j.Logger;

public abstract class FastDecoder extends Decoder<String, Integer> {

	protected Logger logger = Logger.getLogger(getClass());
	protected static final double UNKNOWN_TAG_WEIGHT = -99.0;
	protected IMorphologicalAnalyzer morphologicalAnalyzer;
	protected double logTheta;
	protected double sufTheta;
	protected int maxGuessedTags;
	protected Set<Integer> tags;
	String tab = "\t";

	public FastDecoder(Model<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, double logTheta,
			double sufTheta, int maxGuessedTags) {
		super(model);
		this.morphologicalAnalyzer = morphologicalAnalyzer;
		this.logTheta = logTheta;
		this.sufTheta = sufTheta;
		this.maxGuessedTags = maxGuessedTags;
		this.tags = model.getTagVocabulary().getTagIndeces();
	}

	/**
	 * Calculates the next probabilities (tag transition probability and
	 * observation probability) for the given tag sequence, for the given word.
	 * 
	 * @param prevTags
	 * @param word
	 * @param isFirst
	 * @return
	 */
	protected Map<Integer, Pair<Double, Double>> getNextProb(
			final List<Integer> prevTags, final String word,
			final boolean isFirst) {
		/*
		 * if EOS then the returning probability is the probability of that the
		 * next tag is EOS_TAG
		 */
		SpecTokenMatcher spectokenMatcher = new SpecTokenMatcher();
		if (word.equals(Model.getEOSToken())) {
			return getNextForEOSToken(prevTags);
		}
		String lWord = Util.toLower(word);
		/* has any uppercased character */
		boolean isUpper = Util.isUpper(word);
		/* possible analysis list from MA */
		List<Integer> anals = null;

		boolean isOOV = true;

		SeenType seen = null;
		IProbabilityModel<Integer, String> wordProbModel = null;
		String wordForm = word;
		Set<Integer> tags = null;
		boolean isSpec;
		String specName;

		/* tags to integers */
		List<String> strAnals = morphologicalAnalyzer.getTags(word);
		if (Util.isNotEmpty(strAnals)) {
			isOOV = false;
			anals = new ArrayList<Integer>();
			// seen = SeenType.Seen;
			for (String tag : strAnals) {
				// TODO: what should we do with those tags, that are not seen
				// previously, but returned by the MA? - Hunpos adds it with -99
				// weight
				if (model.getTagVocabulary().getIndex(tag) != null) {
					anals.add(model.getTagVocabulary().getIndex(tag));
				}
				// else {
				// anals.add(model.getTagVocabulary().addElement(tag));
				// }
			}
			// TODO: quick hack for surviving (match the MA output with the tag
			// vocabulary!)
			if (anals.size() == 0)
				isOOV = true;

		}
		/* check whether we have lexicon info */
		tags = model.getStandardTokensLexicon().getTags(word);
		if (Util.isNotEmpty(tags)) {
			wordProbModel = model.getStandardEmissionModel();
			wordForm = word;
			seen = SeenType.Seen;
			/* whether if it a sentence starting word */
		} else {
			tags = model.getStandardTokensLexicon().getTags(lWord);
			if (isFirst && isUpper && Util.isNotEmpty(tags)) {
				wordProbModel = model.getStandardEmissionModel();
				wordForm = lWord;
				seen = SeenType.LowerCasedSeen;
			} else {
				specName = spectokenMatcher.matchLexicalElement(word);
				isSpec = specName != null;
				/* whether if it is a spec token */
				if (isSpec) {
					wordProbModel = model.getSpecTokensEmissionModel();
					tags = model.getSpecTokensLexicon().getTags(specName);
					if (Util.isNotEmpty(tags)) {
						seen = SeenType.SpecialToken;
					} else {
						seen = SeenType.Unseen;
					}
					wordForm = specName;
				} else {
					seen = SeenType.Unseen;
				}
			}
		}
		/* if the token is somehow known then use the models */
		if (seen != SeenType.Unseen) {
			// logger.trace("obs is seen");
			return getNextForSeenToken(prevTags, wordProbModel, wordForm, tags);
		} else {
			// logger.trace("obs is unseen");
			if (Util.isNotEmpty(anals) && anals.size() == 1) {
				// logger.trace("obs is in voc and has only one possible tag");
				return getNextForSingleTaggedToken(prevTags, anals);
			} else {
				return getNextForGuessedToken(prevTags, lWord, isUpper, anals,
						isOOV);

			}
		}
	}

	private Map<Integer, Pair<Double, Double>> getNextForGuessedToken(
			final List<Integer> prevTags, String lWord, boolean isUpper,
			List<Integer> anals, boolean isOOV) {
		// Map<Integer, Double> tagProbs = new HashMap<Integer, Double>();
		// List<Integer> possibleTags;
		// logger.trace("guessing for:" + lWord);
		ISuffixGuesser<String, Integer> guesser = null;
		if (isUpper) {
			// logger.trace("using upper guesser");
			guesser = model.getUpperCaseSuffixGuesser();
		} else {
			// logger.trace("using lower guesser");
			guesser = model.getLowerCaseSuffixGuesser();
		}
		Map<Integer, Pair<Double, Double>> ret;
		if (!isOOV) {
			// logger.trace("obs is in voc");
			ret = getNextForGuessedVocToken(prevTags, lWord, anals, guesser);
		} else {
			// logger.trace("obs is oov");
			ret = getNextForGuessedOOVToken(prevTags, lWord, guesser);
		}

		return ret;
	}

	private Map<Integer, Pair<Double, Double>> getNextForGuessedOOVToken(
			final List<Integer> prevTags, String lWord,
			ISuffixGuesser<String, Integer> guesser) {
		Map<Integer, Pair<Double, Double>> tagProbs;
		Map<Integer, Double> guessedTags = guesser
				.getTagLogProbabilities(lWord);
		// for (Integer key : guessedTags.keySet()) {
		//
		// logger.trace("\ttag " + key + " " + guessedTags.get(key));
		// }

		Set<Entry<Integer, Double>> prunedGuessedTags = pruneGuessedTags(guessedTags);

		tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		for (Entry<Integer, Double> guess : prunedGuessedTags) {
			Double emissionProb = guess.getValue();
			// logger.debug("trans "
			// + model.getTagVocabulary().getWord(guess.getKey()) + " "
			// + guessedVal);
			Integer tag = guess.getKey();
			Double tagTransProb = model.getTagTransitionModel().getLogProb(
					prevTags, tag);
			// logger.debug("emission "
			// + model.getTagVocabulary().getWord(guess.getKey()) + " "
			// + guessedVal);
			tagProbs.put(
					tag,
					new ImmutablePair<Double, Double>(tagTransProb,
							emissionProb
									- Math.log(model.getAprioriTagProbs().get(
											tag))));
		}
		return tagProbs;
	}

	private Map<Integer, Pair<Double, Double>> getNextForGuessedVocToken(
			final List<Integer> prevTags, String lWord, List<Integer> anals,
			ISuffixGuesser<String, Integer> guesser) {
		Map<Integer, Pair<Double, Double>> tagProbs;
		List<Integer> possibleTags;
		possibleTags = anals;
		tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		for (Integer tag : possibleTags) {
			Double emissionProb = 0.0;
			if (model.getTagVocabulary().getWord(tag) == null) {
				emissionProb = UNKNOWN_TAG_WEIGHT;
			} else {
				emissionProb = guesser.getTagLogProbability(lWord, tag)
						- Math.log(model.getAprioriTagProbs().get(tag));
			}
			tagProbs.put(
					tag,
					new ImmutablePair<Double, Double>(model
							.getTagTransitionModel().getLogProb(prevTags, tag),
							emissionProb
									- Math.log(model.getAprioriTagProbs().get(
											tag))));
		}
		return tagProbs;
	}

	private Map<Integer, Pair<Double, Double>> getNextForSingleTaggedToken(
			final List<Integer> prevTags, List<Integer> anals) {
		Map<Integer, Pair<Double, Double>> tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		Integer tag = anals.get(0);
		tagProbs.put(tag, new ImmutablePair<Double, Double>(model
				.getTagTransitionModel().getLogProb(prevTags, tag), 0.0));
		return tagProbs;
	}

	private Map<Integer, Pair<Double, Double>> getNextForSeenToken(
			final List<Integer> prevTags,
			IProbabilityModel<Integer, String> wordProbModel, String wordForm,
			Set<Integer> tags) {
		Map<Integer, Pair<Double, Double>> tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		for (Integer tag : tags) {
			Double tagProb = model.getTagTransitionModel().getLogProb(prevTags,
					tag);
			List<Integer> actTags = new ArrayList<Integer>(prevTags);
			actTags.add(tag);
			Double emissionProb = wordProbModel.getLogProb(actTags, wordForm);
			tagProbs.put(tag, new ImmutablePair<Double, Double>(tagProb,
					emissionProb));
		}
		return tagProbs;
	}

	private HashMap<Integer, Pair<Double, Double>> getNextForEOSToken(
			final List<Integer> prevTags) {
		Double eosProb = model.getTagTransitionModel().getLogProb(prevTags,
				model.getEOSIndex());
		HashMap<Integer, Pair<Double, Double>> ret = new HashMap<Integer, Pair<Double, Double>>();
		// TODO: this 1.0 can be anything, it is better to be zero
		ret.put(model.getEOSIndex(), new ImmutablePair<Double, Double>(eosProb,
				1.0));
		return ret;
	}

	protected Set<Entry<Integer, Double>> pruneGuessedTags(
			Map<Integer, Double> guessedTags) {
		TreeSet<Entry<Integer, Double>> set = new TreeSet<Map.Entry<Integer, Double>>(
		/* reverse comparator */
		new Comparator<Entry<Integer, Double>>() {

			@Override
			public int compare(Entry<Integer, Double> o1,
					Entry<Integer, Double> o2) {
				if (o1.getValue() > o2.getValue())
					return -1;
				else if (o1.getValue() < o2.getValue())
					return 1;
				else
					return Double.compare(o1.getKey(), o2.getKey());
			}
		});

		int maxTag = SuffixGuesser.getMaxProbabilityTag(guessedTags);
		double maxVal = guessedTags.get(maxTag);
		double minval = maxVal - sufTheta;
		for (Entry<Integer, Double> entry : guessedTags.entrySet()) {
			if (entry.getValue() > minval) {
				set.add(entry);
			}
		}
		if (set.size() > maxGuessedTags) {
			Iterator<Entry<Integer, Double>> it = set.descendingIterator();
			while (set.size() > maxGuessedTags) {
				it.next();
				it.remove();
			}
		}

		return set;
	}

}
