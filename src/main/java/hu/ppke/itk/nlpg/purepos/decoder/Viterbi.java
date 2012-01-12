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
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

public class Viterbi extends IViterbi<String, Integer> {

	protected Logger logger = Logger.getLogger(getClass());
	protected static final double UNKNOWN_TAG_WEIGHT = -99.0;

	protected static enum SeenType {
		Seen, LowerCasedSeen, SpecialToken, Unseen;
	}

	protected static class State {
		public State(List<Integer> path, double weight) {
			this.path = new ArrayList<Integer>(path);

			this.weight = weight;
		}

		protected List<Integer> path = new ArrayList<Integer>();

		protected double weight = 0;

		public State createNext(Integer newStep, double plusWeight) {
			State s = new State(new ArrayList<Integer>(this.path), this.weight
					+ plusWeight);
			s.path.add(newStep);
			return s;
		}

		public List<Integer> getPath() {
			return path;
		}

		public double getWeight() {
			return weight;
		}

		@Override
		public String toString() {
			return "{path: " + path + " weight: " + weight + "}";
		}
	}

	Map<Integer, State> trellis = new HashMap<Integer, State>();
	IMorphologicalAnalyzer morphologicalAnalyzer;
	double logTheta;
	int maxGuessedTags;

	public Viterbi(Model<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, double logTheta,
			int maxGuessedTags) {
		super(model);
		this.morphologicalAnalyzer = morphologicalAnalyzer;
		this.logTheta = logTheta;
		this.maxGuessedTags = maxGuessedTags;
	}

	@Override
	public List<Integer> decode(final List<String> observations) {
		trellis.clear();
		List<String> obs = new ArrayList<String>(observations);
		// 1 EOS marker as in HunPos
		obs.add(Model.getEOSToken());
		int n = model.getTaggingOrder();
		// Map<Integer, Double> startProbs = model.getAprioriTagProbs();
		ArrayList<Integer> startTags = new ArrayList<Integer>();// TODO:n?*
		for (int j = 0; j < n; ++j) {
			startTags.add(model.getBOSIndex());
		}

		// TODO: hunpos worlflow is a bit different here*
		State s = new State(startTags, 0.0);

		trellis.put(model.getBOSIndex(), s);

		forward(obs);
		// find maximal element
		try {
			State maximalState = Collections.max(trellis.values(),
					new Comparator<State>() {
						@Override
						public int compare(State first, State second) {
							return Double.compare(first.getWeight(),
									second.getWeight());
						}
					});
			List<Integer> ret = maximalState.getPath();
			// System.out.println(ret);
			return ret.subList(model.getTaggingOrder(), ret.size() - 1);
		} catch (java.util.NoSuchElementException e) {
			logger.trace(observations);
			logger.trace(trellis);
			throw new RuntimeException(e);
		}

	}

	protected void forward(List<String> observations) {
		// Table<Integer, Integer, Double> nw = HashBasedTable.create();
		Map<Integer, Map<Integer, Double>> nextWeights = new HashMap<Integer, Map<Integer, Double>>();

		boolean isFirst = true;
		String tab = "\t";
		for (String obs : observations) {
			logger.trace("current observation: " + obs);
			try {
				// System.out.println(obs);
				nextWeights.clear();
				// if (isFirst) {
				// Map<Integer, Double> fProbs = getNextProb(
				// new ArrayList<Integer>(), Model.getBOSToken(), false);
				// nextWeights.put(model.getBOSIndex(), fProbs);
				// } else {

				for (Integer fromTag : model.getTagVocabulary().getTagIndeces()) {
					State s = trellis.get(fromTag);
					if (s != null) {
						Map<Integer, Double> nextProb = getNextProb(
								s.getPath(), obs, isFirst);
						nextWeights.put(fromTag, nextProb);
					}
				}
				logger.trace(tab + "nextweights: " + nextWeights);
				// }
				Map<Integer, State> trellisTmp = new HashMap<Integer, State>();
				for (Integer nextTag : model.getTagVocabulary().getTagIndeces()) {
					try {
						Integer fromTag = findMaxFor(nextTag, nextWeights);
						Double plusWeight = nextWeights.get(fromTag).get(
								nextTag);

						if (plusWeight != null) {
							logger.trace(tab + "transition:" + fromTag + "->"
									+ nextTag + " (" + plusWeight + ")");
							trellisTmp.put(nextTag, trellis.get(fromTag)
									.createNext(nextTag, plusWeight));
						}
					} catch (NoSuchElementException e) {
						logger.trace(e.getStackTrace());
					}
				}

				logger.trace(tab + "trellis:" + trellisTmp);
				if (trellisTmp.size() == 0) {
					logger.trace(tab + "Tellis is empty!");
					logger.trace(tab + nextWeights);
					logger.trace(tab + trellis);
				}
				// TODO: it could be really slow
				trellis = trellisTmp;

				isFirst = false;
			} catch (Throwable t) {
				logger.trace(obs + " " + observations.indexOf(obs) + " "
						+ " in: " + observations);
				logger.trace(t.getStackTrace());
				throw new RuntimeException(t);
			}

			trellis = doBeamPruning(trellis);
			logger.trace(tab + "pruned trellis:" + trellis);
		}

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
			final Map<Integer, Map<Integer, Double>> nextWeights) {
		Entry<Integer, Map<Integer, Double>> max = Collections.max(
				nextWeights.entrySet(),
				new Comparator<Map.Entry<Integer, Map<Integer, Double>>>() {

					@Override
					public int compare(Entry<Integer, Map<Integer, Double>> o1,
							Entry<Integer, Map<Integer, Double>> o2) {
						if (o1.getValue() == null
								|| o1.getValue().get(nextTag) == null)
							return 1;
						if (o2.getValue() == null
								|| o2.getValue().get(nextTag) == null)
							return -1;
						return Double.compare(
								o1.getValue().get(nextTag)
										+ trellis.get(o1.getKey()).getWeight(),
								o2.getValue().get(nextTag)
										+ trellis.get(o2.getKey()).getWeight());
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

	protected Map<Integer, Double> getNextProb(final List<Integer> prevTags,
			final String word, final boolean isFirst) {
		/*
		 * if EOS then the returning probability is the probability of that the
		 * next tag is EOS_TAG
		 */
		logger.debug("==>word " + word);
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
			logger.trace("obs is seen");
			return getNextForSeenToken(prevTags, wordProbModel, wordForm, tags);
		} else {
			logger.trace("obs is unseen");
			if (Util.isNotEmpty(anals) && anals.size() == 1) {
				logger.trace("obs is in voc and has only one possible tag");
				return getNextForSingleTaggedToken(prevTags, anals);
			} else {
				return getNextForGuessedToken(prevTags, lWord, isUpper, anals,
						isOOV);

			}
		}
	}

	protected Map<Integer, Double> getNextForGuessedToken(
			final List<Integer> prevTags, String lWord, boolean isUpper,
			List<Integer> anals, boolean isOOV) {
		// Map<Integer, Double> tagProbs = new HashMap<Integer, Double>();
		// List<Integer> possibleTags;
		logger.trace("guessing for:" + lWord);
		ISuffixGuesser<String, Integer> guesser = null;
		if (isUpper) {
			logger.trace("using upper guesser");
			guesser = model.getUpperCaseSuffixGuesser();
		} else {
			logger.trace("using lower guesser");
			guesser = model.getLowerCaseSuffixGuesser();
		}
		if (!isOOV) {
			logger.trace("obs is in voc");
			return getNextForGuessedVocToken(prevTags, lWord, anals, guesser);
		} else {
			logger.trace("obs is oov");
			return getNextForGuessedOOVToken(prevTags, lWord, guesser);

		}
	}

	protected Map<Integer, Double> getNextForGuessedOOVToken(
			final List<Integer> prevTags, String lWord,
			ISuffixGuesser<String, Integer> guesser) {
		Map<Integer, Double> tagProbs;
		Map<Integer, Double> guessedTags = guesser
				.getTagLogProbabilities(lWord);

		Set<Entry<Integer, Double>> prunedGuessedTags = pruneGuessedTags(guessedTags);

		tagProbs = new HashMap<Integer, Double>();
		for (Entry<Integer, Double> guess : prunedGuessedTags) {
			Double guessedVal = guess.getValue();
			// logger.debug("trans "
			// + model.getTagVocabulary().getWord(guess.getKey()) + " "
			// + guessedVal);
			Double tagVal = model.getTagTransitionModel().getLogProb(prevTags,
					guess.getKey());
			logger.debug("emission "
					+ model.getTagVocabulary().getWord(guess.getKey()) + " "
					+ guessedVal);
			tagProbs.put(guess.getKey(), tagVal + guessedVal);
		}
		return tagProbs;
	}

	protected Map<Integer, Double> getNextForGuessedVocToken(
			final List<Integer> prevTags, String lWord, List<Integer> anals,
			ISuffixGuesser<String, Integer> guesser) {
		Map<Integer, Double> tagProbs;
		List<Integer> possibleTags;
		possibleTags = anals;
		tagProbs = new HashMap<Integer, Double>();
		for (Integer tag : possibleTags) {
			Double lexProb = 0.0;
			if (model.getTagVocabulary().getWord(tag) == null) {
				lexProb = UNKNOWN_TAG_WEIGHT;
			} else {
				lexProb = guesser.getTagLogProbability(lWord, tag)
						- Math.log(model.getAprioriTagProbs().get(tag));
			}
			tagProbs.put(tag,
					model.getTagTransitionModel().getLogProb(prevTags, tag)
							+ lexProb);
		}
		return tagProbs;
	}

	protected Map<Integer, Double> getNextForSingleTaggedToken(
			final List<Integer> prevTags, List<Integer> anals) {
		Map<Integer, Double> tagProbs = new HashMap<Integer, Double>();
		Integer tag = anals.get(0);
		tagProbs.put(tag,
				model.getTagTransitionModel().getLogProb(prevTags, tag));
		return tagProbs;
	}

	protected Map<Integer, Double> getNextForSeenToken(
			final List<Integer> prevTags,
			IProbabilityModel<Integer, String> wordProbModel, String wordForm,
			Set<Integer> tags) {
		Map<Integer, Double> tagProbs = new HashMap<Integer, Double>();
		for (Integer tag : tags) {
			Double tagModelVal = model.getTagTransitionModel().getLogProb(
					prevTags, tag);
			List<Integer> actTags = new ArrayList<Integer>(prevTags);
			actTags.add(tag);
			Double wordModelVal = wordProbModel.getLogProb(actTags, wordForm);
			tagProbs.put(tag, tagModelVal + wordModelVal);
		}
		return tagProbs;
	}

	protected HashMap<Integer, Double> getNextForEOSToken(
			final List<Integer> prevTags) {
		Double eosProb = model.getTagTransitionModel().getLogProb(prevTags,
				model.getEOSIndex());
		HashMap<Integer, Double> ret = new HashMap<Integer, Double>();
		ret.put(model.getEOSIndex(), eosProb);
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
