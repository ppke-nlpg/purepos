/*******************************************************************************
 * Copyright (c) 2012 György Orosz, Attila Novák.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/
 * 
 * This file is part of PurePos.
 * 
 * PurePos is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PurePos is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.common.AnalysisQueue;
import hu.ppke.itk.nlpg.purepos.common.Global;
import hu.ppke.itk.nlpg.purepos.common.SpecTokenMatcher;
import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.model.IMapper;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.NGram;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractDecoder extends Decoder<String, Integer> {

	private static final double EOS_EMISSION_PROB = 1.0;
	// protected Logger logger = Logger.getLogger(getClass());
	protected static final double UNKNOWN_TAG_WEIGHT = -99.0;
	protected IMorphologicalAnalyzer morphologicalAnalyzer;
	protected double logTheta;
	protected double sufTheta;
	protected int maxGuessedTags;

	protected Set<Integer> tags;
	String tab = "\t";

	public AbstractDecoder(CompiledModel<String, Integer> model,
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
	protected Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextProbs(
			final Set<NGram<Integer>> prevTagsSet, final String word,
			final int position, final boolean isFirst) {

		/*
		 * if EOS then the returning probability is the probability of that the
		 * next tag is EOS_TAG
		 */
		SpecTokenMatcher spectokenMatcher = new SpecTokenMatcher();
		if (word.equals(Model.getEOSToken())) {
			return getNextForEOSToken(prevTagsSet);
		}
		String lWord = Util.toLower(word);
		/* has any uppercased character */
		boolean isUpper = Util.isUpper(lWord, word);
		/* possible analysis list from MA */
		List<Integer> anals = null;

		boolean isOOV = true;

		SeenType seen = null;
		IProbabilityModel<Integer, String> wordProbModel = null;
		String wordForm = word;
		Set<Integer> tags = null;
		boolean isSpec = false;
		String specName;

		/* tags to integers */
		List<String> strAnals = morphologicalAnalyzer.getTags(word);
		if (Util.isNotEmpty(strAnals)) {
			isOOV = false;
			anals = new ArrayList<Integer>();
			// seen = SeenType.Seen;
			for (String tag : strAnals) {

				if (model.getTagVocabulary().getIndex(tag) != null) {
					anals.add(model.getTagVocabulary().getIndex(tag));
				} else {
					anals.add(model.getTagVocabulary().addElement(tag));
				}
			}

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

		AnalysisQueue userAnals = Global.analysisQueue;
		if (userAnals.hasAnal(position)) {
			Set<Integer> newTags = userAnals.getTags(position,
					model.getTagVocabulary());
			if (userAnals.useProbabilties(position)) {
				IProbabilityModel<Integer, String> newWordModel = userAnals
						.getLexicalModelForWord(position,
								model.getTagVocabulary());
				// newWordModel.setContextMapper(model.getStandardEmissionModel()
				// .getContextMapper());
				return getNextForSeenToken(prevTagsSet, newWordModel, wordForm,
						isSpec, newTags, anals);
			} else {
				if (seen != SeenType.Unseen) {

					return getNextForSeenToken(prevTagsSet, wordProbModel,
							wordForm, isSpec, newTags, anals);
				} else {
					if (Util.isNotEmpty(newTags) && newTags.size() == 1) {
						return getNextForSingleTaggedToken(prevTagsSet, newTags);
					} else {
						return getNextForGuessedToken(prevTagsSet, lWord,
								isUpper, newTags, false);
					}

				}

			}

		} else {

			/* if the token is somehow known, then use the models */
			if (seen != SeenType.Unseen) {
				// logger.trace("obs is seen");
				return getNextForSeenToken(prevTagsSet, wordProbModel,
						wordForm, isSpec, tags, anals);
			} else {
				// logger.trace("obs is unseen");
				if (Util.isNotEmpty(anals) && anals.size() == 1) {
					// logger.trace("obs is in voc and has only one possible tag");
					return getNextForSingleTaggedToken(prevTagsSet, anals);
				} else {
					return getNextForGuessedToken(prevTagsSet, lWord, isUpper,
							anals, isOOV);

				}
			}
		}
	}

	private Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextForGuessedToken(
			final Set<NGram<Integer>> prevTagsSet, String wordForm,
			boolean isUpper, Collection<Integer> anals, boolean isOOV) {
		ISuffixGuesser<String, Integer> guesser = null;
		if (isUpper) {
			guesser = model.getUpperCaseSuffixGuesser();
		} else {
			guesser = model.getLowerCaseSuffixGuesser();
		}
		Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> ret;
		if (!isOOV) {
			ret = getNextForGuessedVocToken(prevTagsSet, wordForm, anals,
					guesser);
		} else {
			ret = getNextForGuessedOOVToken(prevTagsSet, wordForm, guesser);
		}

		return ret;
	}

	private Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextForGuessedOOVToken(
			final Set<NGram<Integer>> prevTagsSet, String lWord,
			ISuffixGuesser<String, Integer> guesser) {

		Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> ret = new HashMap<NGram<Integer>, Map<Integer, Pair<Double, Double>>>();
		Map<Integer, Pair<Double, Double>> tagProbs;
		Map<Integer, Double> guessedTags = guesser
				.getTagLogProbabilities(lWord);

		Set<Entry<Integer, Double>> prunedGuessedTags = pruneGuessedTags(guessedTags);

		tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		for (NGram<Integer> prevTags : prevTagsSet) {
			for (Entry<Integer, Double> guess : prunedGuessedTags) {
				Double emissionProb = guess.getValue();
				Integer tag = guess.getKey();

				Double tagTransProb = model.getTagTransitionModel().getLogProb(
						prevTags.toList(), tag);
				double aprioriProb = Math.log(model.getAprioriTagProbs()
						.get(tag));
				tagProbs.put(
						tag,
						new ImmutablePair<Double, Double>(tagTransProb,
								emissionProb
										- aprioriProb));

			}
			ret.put(prevTags, tagProbs);
		}
		return ret;
	}

	// TODO: biztos, hogy jó, ez?
	private Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextForGuessedVocToken(
			final Set<NGram<Integer>> prevTagsSet, String lWord,
			Collection<Integer> anals, ISuffixGuesser<String, Integer> guesser) {

		Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> ret = new HashMap<NGram<Integer>, Map<Integer, Pair<Double, Double>>>();
		Map<Integer, Pair<Double, Double>> tagProbs;
		Collection<Integer> possibleTags;
		possibleTags = anals;
		tagProbs = new HashMap<Integer, Pair<Double, Double>>();
		for (Integer tag : possibleTags) {
			Double emissionProb = 0.0;

			Double transitionProb;
			Integer newTag = guesser.getMapper().map(tag);
			if (newTag > model.getTagVocabulary().getMaximalIndex()) {
				emissionProb = UNKNOWN_TAG_WEIGHT;
				// TODO: RESEARCH: new tags should handled better
				transitionProb = Double.NEGATIVE_INFINITY;
				tagProbs.put(tag, new ImmutablePair<Double, Double>(
						transitionProb, emissionProb));
				for (NGram<Integer> prevTags : prevTagsSet) {
					ret.put(prevTags, tagProbs);
				}
			} else {
				Double aprioriProb = model.getAprioriTagProbs().get(newTag);
				Double logAprioriPorb = Math.log(aprioriProb);
				emissionProb = guesser.getTagLogProbability(lWord, tag)
						- logAprioriPorb;
				for (NGram<Integer> prevTags : prevTagsSet) {
					transitionProb = model.getTagTransitionModel().getLogProb(
							prevTags.toList(), tag);
					tagProbs.put(tag, new ImmutablePair<Double, Double>(
							transitionProb, emissionProb));
					ret.put(prevTags, tagProbs);
				}
			}

		}
		return ret;
	}

	private Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextForSingleTaggedToken(
			final Set<NGram<Integer>> prevTagsSet, Collection<Integer> anals) {
		Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> ret = new HashMap<NGram<Integer>, Map<Integer, Pair<Double, Double>>>();
		for (NGram<Integer> prevTags : prevTagsSet) {
			Map<Integer, Pair<Double, Double>> tagProbs = new HashMap<Integer, Pair<Double, Double>>();
			Integer tag = anals.iterator().next();
			Double tagProb = model.getTagTransitionModel().getLogProb(
					prevTags.toList(), tag);
			tagProb = tagProb == Float.NEGATIVE_INFINITY ? 0 : tagProb;
			tagProbs.put(tag, new ImmutablePair<Double, Double>(tagProb, 0.0));
			ret.put(prevTags, tagProbs);
		}
		return ret;
	}

	private Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextForSeenToken(
			final Set<NGram<Integer>> prevTagsSet,
			IProbabilityModel<Integer, String> wordProbModel, String wordForm,
			boolean isSpec, Collection<Integer> tags, Collection<Integer> anals) {
		Collection<Integer> tagset = filterTagsWithMorphology(tags, anals,
				wordProbModel.getContextMapper());

		Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> ret = new HashMap<NGram<Integer>, Map<Integer, Pair<Double, Double>>>();
		for (NGram<Integer> prevTags : prevTagsSet) {
			Map<Integer, Pair<Double, Double>> tagProbs = new HashMap<Integer, Pair<Double, Double>>();
			for (Integer tag : tagset) {

				Double tagProb = model.getTagTransitionModel().getLogProb(
						prevTags.toList(), tag);
				List<Integer> actTags = new ArrayList<Integer>(
						prevTags.toList());
				actTags.add(tag);
				// //
				Double emissionProb = wordProbModel.getLogProb(actTags,
						wordForm);
				tagProbs.put(tag, new ImmutablePair<Double, Double>(tagProb,
						emissionProb));
			}
			ret.put(prevTags, tagProbs);
		}
		return ret;
	}

	protected Collection<Integer> filterTagsWithMorphology(
			Collection<Integer> tags, Collection<Integer> anals,
			IMapper<Integer> mapper) {

		Collection<Integer> common;
		if (anals != null) {
			if (mapper != null) {
				common = mapper.filter(anals, tags);
			} else {
				common = new HashSet<Integer>(anals);
				common.retainAll(tags);
			}
			if (common.size() > 0)
				return common;
		}
		return tags;
	}

	private Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> getNextForEOSToken(
			final Set<NGram<Integer>> prevTagsSet) {
		Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> ret = new HashMap<NGram<Integer>, Map<Integer, Pair<Double, Double>>>();
		for (NGram<Integer> prevTags : prevTagsSet) {
			Double eosProb = model.getTagTransitionModel().getLogProb(
					prevTags.toList(), model.getEOSIndex());
			Map<Integer, Pair<Double, Double>> r = new HashMap<Integer, Pair<Double, Double>>();
			r.put(model.getEOSIndex(), new ImmutablePair<Double, Double>(
					eosProb, EOS_EMISSION_PROB));
			ret.put(prevTags, r);
		}
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

	protected List<Integer> decompose(Node node) {
		Node act = node;
		Node prev;
		prev = act.getPrevious();
		List<Integer> stack = new LinkedList<Integer>();
		while (prev != null) {
			stack.add(0, act.getState().getLast());
			act = prev;
			prev = act.getPrevious();
		}

		return stack;
	}

	@Override
	public List<Integer> decode(List<String> observations) {
		return decode(observations, 1).get(0).getKey();

	}

	protected List<Pair<List<Integer>, Double>> cleanResults(
			List<Pair<List<Integer>, Double>> tagSeqList) {
		List<Pair<List<Integer>, Double>> ret = new ArrayList<Pair<List<Integer>, Double>>();
		for (Pair<List<Integer>, Double> element : tagSeqList) {
			List<Integer> tagSeq = element.getKey();
			List<Integer> newTagSeq = tagSeq.subList(0, tagSeq.size() - 1);
			ret.add(Pair.of(newTagSeq, element.getValue()));
		}
		return ret;
	}

	protected List<String> prepareObservations(List<String> observations) {
		List<String> obs = new ArrayList<String>(observations);

		obs.add(Model.getEOSToken()); // adds 1 EOS marker as in HunPos
		return obs;
	}

	protected NGram<Integer> createInitialElement() {
		int n = model.getTaggingOrder() - 1;

		ArrayList<Integer> startTags = new ArrayList<Integer>();
		for (int j = 0; j <= n; ++j) {
			startTags.add(model.getBOSIndex());
		}

		NGram<Integer> startNGram = new NGram<Integer>(startTags,
				model.getTaggingOrder());
		return startNGram;
	}

	protected Node startNode(final NGram<Integer> start) {
		return new Node(start, 0.0, null);
	}

}
