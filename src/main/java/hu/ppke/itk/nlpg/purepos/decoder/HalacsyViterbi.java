package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;
import hu.ppke.itp.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
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

public class HalacsyViterbi extends IViterbi<String, Integer> {

	protected Logger logger = Logger.getLogger(getClass());
	protected static final double UNKNOWN_TAG_WEIGHT = -99.0;

	Map<Integer, State> trellis = new HashMap<Integer, State>();
	IMorphologicalAnalyzer morphologicalAnalyzer;
	double logTheta;
	int maxGuessedTags;
	Set<Integer> tags;
	String tab = "\t";

	public HalacsyViterbi(Model<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, double logTheta,
			int maxGuessedTags) {
		super(model);
		this.morphologicalAnalyzer = morphologicalAnalyzer;
		this.logTheta = logTheta;
		this.maxGuessedTags = maxGuessedTags;
		this.tags = model.getTagVocabulary().getTagIndeces();
	}

	@Override
	public List<Integer> decode(final List<String> observations) {
		trellis.clear();
		List<String> obs = new ArrayList<String>(observations);

		obs.add(Model.getEOSToken()); // adds 1 EOS marker as in HunPos
		int n = model.getTaggingOrder();

		ArrayList<Integer> startTags = new ArrayList<Integer>();

		for (int j = 0; j < n; ++j) { // TODO:n?*
			startTags.add(model.getBOSIndex());
		}

		State s = new State(startTags, 0.0); // TODO: hunpos worlflow is a bit
												// different here*

		trellis.put(model.getBOSIndex(), s);

		forward(obs);

		try { // find maximal element
			State maximalState = Collections.max(trellis.values(),
					new Comparator<State>() {
						@Override
						public int compare(State first, State second) {
							return Double.compare(first.getWeight(),
									second.getWeight());
						}
					});
			List<Integer> ret = maximalState.getPath();
			return ret.subList(model.getTaggingOrder(), ret.size() - 1);
		} catch (java.util.NoSuchElementException e) {
			// TODO: is it really needed?
			logger.trace(observations);
			logger.trace(trellis);
			throw new RuntimeException(e);
		}

	}

	protected void forward(List<String> observations) {
		Map<Integer, Map<Integer, Pair<Double, Double>>> nextWeights = new HashMap<Integer, Map<Integer, Pair<Double, Double>>>();

		boolean isFirst = true;
		for (String obs : observations) {

			logger.trace("current observation: " + obs);

			nextWeights = computeNextWeights(isFirst, obs);
			// logger.trace(tab + "nextweights: " + nextWeights);
			logger.trace("\tcurrent states:");
			for (State s : trellis.values())
				logger.trace("\t\t" + s.getPath() + " - " + s.getWeight());

			trellis = updateTrellis(nextWeights);

			isFirst = false;

			trellis = doBeamPruning(trellis);

		}

	}

	public Map<Integer, Map<Integer, Pair<Double, Double>>> computeNextWeights(
			boolean isFirst, String obs) {
		Map<Integer, Map<Integer, Pair<Double, Double>>> nextWeights = new HashMap<Integer, Map<Integer, Pair<Double, Double>>>();

		for (Integer fromTag : trellis.keySet()) {
			State s = trellis.get(fromTag);
			Map<Integer, Pair<Double, Double>> nextProb = getNextProb(
					s.getPath(), obs, isFirst);
			nextWeights.put(fromTag, nextProb);

		}
		return nextWeights;
	}

	public Map<Integer, State> updateTrellis(
			Map<Integer, Map<Integer, Pair<Double, Double>>> nextWeights) {
		Map<Integer, State> trellisTmp = new HashMap<Integer, State>();
		// Set<Integer> tags = nextWeights
		for (Integer nextTag : tags) {
			Integer fromTag = findMaxFor(nextTag, nextWeights);
			// transition prob
			Pair<Double, Double> plusWeightpair = nextWeights.get(fromTag).get(
					nextTag);

			if (plusWeightpair != null) {
				// emission prob
				Double plusWeight = plusWeightpair.getLeft();
				if (nextNodesNum(nextWeights) > 1) {
					plusWeight += plusWeightpair.getRight();
				}
				// logger.trace(tab
				// + "next state: .."
				// + fromTag
				// + ","
				// + nextTag
				// + " :"
				// + ((plusWeightpair.getLeft())
				// + +plusWeightpair.getRight() + trellis.get(
				// fromTag).getWeight()));
				trellisTmp.put(nextTag,
						trellis.get(fromTag).createNext(nextTag, plusWeight));
			}

		}
		return trellisTmp;
	}

	protected int nextNodesNum(
			Map<Integer, Map<Integer, Pair<Double, Double>>> nextWeights) {
		int num = 0;
		for (Map<Integer, Pair<Double, Double>> t : nextWeights.values()) {
			num += t.keySet().size();
		}
		return num;
	}

	protected Map<Integer, State> doBeamPruning(Map<Integer, State> trellis) {
		Map<Integer, State> ret = new HashMap<Integer, State>();
		Map.Entry<Integer, State> maxElement = Collections.max(
				trellis.entrySet(),
				new Comparator<Map.Entry<Integer, State>>() {

					@Override
					public int compare(Entry<Integer, State> o1,
							Entry<Integer, State> o2) {
						return Double.compare(o1.getValue().getWeight(), o2
								.getValue().getWeight());
					}
				});
		Double maxWeight = maxElement.getValue().getWeight();
		for (Integer key : trellis.keySet()) {
			Double w = trellis.get(key).getWeight();
			if (!(w < maxWeight - logTheta)) {
				ret.put(key, trellis.get(key));
			}
		}

		return ret;
	}

	protected int findMaxFor(final Integer nextTag,
			final Map<Integer, Map<Integer, Pair<Double, Double>>> nextWeights) {

		// find maximum according to tag transition probabilities
		Entry<Integer, Map<Integer, Pair<Double, Double>>> max = Collections
				.max(nextWeights.entrySet(),
						new Comparator<Map.Entry<Integer, Map<Integer, Pair<Double, Double>>>>() {

							@Override
							public int compare(
									Entry<Integer, Map<Integer, Pair<Double, Double>>> o1,
									Entry<Integer, Map<Integer, Pair<Double, Double>>> o2) {
								if (o1.getValue() == null
										|| o1.getValue().get(nextTag) == null)
									return 1;
								if (o2.getValue() == null
										|| o2.getValue().get(nextTag) == null)
									return -1;
								return Double.compare(o1.getValue()
										.get(nextTag).getLeft()
										+ trellis.get(o1.getKey()).getWeight(),
										o2.getValue().get(nextTag).getLeft()
												+ trellis.get(o2.getKey())
														.getWeight());
							}
						});
		return max.getKey();
		//
		// double max = Double.MIN_VALUE;
		// int maxTag = -1;
		// for (Entry<Integer, Map<Integer, Double>> entry : nextWeights
		// .entrySet()) {
		// double val = entry.getValue().get(nextTag);
		// if (val > max) {
		// max = val;
		// maxTag = entry.getKey();
		// }
		// }
		// return maxTag;
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
		Set<String> strAnals = morphologicalAnalyzer.getTags(word);
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

	protected Map<Integer, Pair<Double, Double>> getNextForGuessedToken(
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
		if (!isOOV) {
			// logger.trace("obs is in voc");
			return getNextForGuessedVocToken(prevTags, lWord, anals, guesser);
		} else {
			// logger.trace("obs is oov");
			return getNextForGuessedOOVToken(prevTags, lWord, guesser);

		}
	}

	protected Map<Integer, Pair<Double, Double>> getNextForGuessedOOVToken(
			final List<Integer> prevTags, String lWord,
			ISuffixGuesser<String, Integer> guesser) {
		Map<Integer, Pair<Double, Double>> tagProbs;
		Map<Integer, Double> guessedTags = guesser
				.getTagLogProbabilities(lWord);

		Set<Entry<Integer, Double>> prunedGuessedTags = pruneGuessedTags(guessedTags);

		tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		for (Entry<Integer, Double> guess : prunedGuessedTags) {
			Double emissionProb = guess.getValue();
			// logger.debug("trans "
			// + model.getTagVocabulary().getWord(guess.getKey()) + " "
			// + guessedVal);
			Double tagTransProb = model.getTagTransitionModel().getLogProb(
					prevTags, guess.getKey());
			// logger.debug("emission "
			// + model.getTagVocabulary().getWord(guess.getKey()) + " "
			// + guessedVal);
			tagProbs.put(guess.getKey(), new ImmutablePair<Double, Double>(
					tagTransProb, emissionProb));
		}
		return tagProbs;
	}

	protected Map<Integer, Pair<Double, Double>> getNextForGuessedVocToken(
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
			tagProbs.put(tag, new ImmutablePair<Double, Double>(model
					.getTagTransitionModel().getLogProb(prevTags, tag),
					emissionProb));
		}
		return tagProbs;
	}

	protected Map<Integer, Pair<Double, Double>> getNextForSingleTaggedToken(
			final List<Integer> prevTags, List<Integer> anals) {
		Map<Integer, Pair<Double, Double>> tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		Integer tag = anals.get(0);
		tagProbs.put(tag, new ImmutablePair<Double, Double>(model
				.getTagTransitionModel().getLogProb(prevTags, tag), 0.0));
		return tagProbs;
	}

	protected Map<Integer, Pair<Double, Double>> getNextForSeenToken(
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

	protected HashMap<Integer, Pair<Double, Double>> getNextForEOSToken(
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
		double minval = maxVal - logTheta;
		for (Entry<Integer, Double> entry : guessedTags.entrySet()) {
			if (entry.getValue() > minval) {
				set.add(entry);
			}
		}
		if (set.size() > maxGuessedTags) {
			Iterator<Entry<Integer, Double>> it = set.descendingIterator();
			while (set.size() > 20) {
				it.next();
				it.remove();
			}
		}

		return set;
	}

}
