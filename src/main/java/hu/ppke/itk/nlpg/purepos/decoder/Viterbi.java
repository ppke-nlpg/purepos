package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.internal.SpecTokenMatcher;
import hu.ppke.itp.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

public class Viterbi extends IViterbi<String, Integer> {
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
		List<String> obs = new ArrayList<String>(observations);
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
	}

	protected void forward(List<String> observations) {
		// Table<Integer, Integer, Double> nw = HashBasedTable.create();
		Map<Integer, Map<Integer, Double>> nextWeights = new HashMap<Integer, Map<Integer, Double>>();

		boolean isFirst = true;
		for (String obs : observations) {
			System.out.println(obs);
			nextWeights.clear();
			// if (isFirst) {
			// Map<Integer, Double> fProbs = getNextProb(
			// new ArrayList<Integer>(), Model.getBOSToken(), false);
			// nextWeights.put(model.getBOSIndex(), fProbs);
			// } else {

			for (Integer fromTag : model.getTagVocabulary().getTagIndeces()) {
				State s = trellis.get(fromTag);
				if (s != null) {
					Map<Integer, Double> nextProb = getNextProb(s.getPath(),
							obs, isFirst);
					nextWeights.put(fromTag, nextProb);
				}
			}
			// }
			Map<Integer, State> trellisTmp = new HashMap<Integer, State>();
			for (Integer nextTag : model.getTagVocabulary().getTagIndeces()) {
				int fromTag = findMaxFor(nextTag, nextWeights);
				Double plusWeight = nextWeights.get(fromTag).get(nextTag);

				if (plusWeight != null) {
					trellisTmp.put(nextTag,
							trellis.get(fromTag)
									.createNext(nextTag, plusWeight));
				}
			}
			// TODO: it could be really slow
			trellis = trellisTmp;

			isFirst = false;
		}

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
						return Double.compare(o1.getValue().get(nextTag), o2
								.getValue().get(nextTag));
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
		SpecTokenMatcher spectokenMatcher = new SpecTokenMatcher();
		if (word.equals(Model.getEOSToken())) {
			return getNextForEOSToken(prevTags);
		}
		String lWord = word.toLowerCase();
		/* has any uppercased character */
		boolean isUpper = !lWord.equals(word);
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
		if (isNotEmpty(strAnals)) {
			isOOV = false;
			anals = new ArrayList<Integer>();
			for (String tag : strAnals) {
				// TODO: what should we do with those tags, that are not seen
				// previously, but returned by the MA? - Hunpos adds it with -99
				// weight
				if (model.getTagVocabulary().getIndex(tag) != null) {
					anals.add(model.getTagVocabulary().getIndex(tag));
				}
			}
		}
		/* check whether we have lexicon info */
		tags = model.getStandardTokensLexicon().getTags(word);
		if (isNotEmpty(tags)) {
			wordProbModel = model.getStandardEmissionModel();
			wordForm = word;
			seen = SeenType.Seen;
			/* whether if it a sentence starting word */
		} else {
			tags = model.getStandardTokensLexicon().getTags(lWord);
			if (isFirst && isUpper && isNotEmpty(tags)) {
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
					seen = SeenType.SpecialToken;
					wordForm = specName;
				} else {
					seen = SeenType.Unseen;
				}
			}
		}
		/* if the token is somehow known then use the models */
		if (seen != SeenType.Unseen) {
			return getNextForSeenToken(prevTags, wordProbModel, wordForm, tags);
		} else {
			if (isNotEmpty(anals) && anals.size() == 1) {
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
		Map<Integer, Double> tagProbs = new HashMap<Integer, Double>();
		List<Integer> possibleTags;
		ISuffixGuesser<String, Integer> guesser = null;
		if (isUpper)
			guesser = model.getUpperCaseSuffixGuesser();
		else
			guesser = model.getLowerCaseSuffixGuesser();

		if (!isOOV) {
			return getNextForGuessedVocToken(prevTags, lWord, anals, guesser);
		} else {
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
			Double tagVal = model.getTagTransitionModel().getLogProb(prevTags,
					guess.getKey());
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
		Double eosProb = 0.0; // model.getTagTransitionModel().getLogProb(prevTags,model.getEOSIndex());
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
					return 0;
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

	protected static <E> boolean isNotEmpty(Collection<E> c) {
		return c != null && c.size() > 0;
	}
}
