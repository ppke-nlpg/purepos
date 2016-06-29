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
package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.ITagMapper;
import hu.ppke.itk.nlpg.purepos.model.SuffixGuesser;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.MutablePair;

/**
 * Suffix guesser implementation for String suffixes with a HashTable
 * representation. This representation may save some memory space but needs a
 * bit more time to calculate the probability for a word and a tag.
 * 
 * @author György Orosz
 * 
 * @param <T>
 */
// TODO: is it worth to create an implementation which recalculates the
// probabilities for all the suffixes?
public class HashSuffixGuesser<T> extends SuffixGuesser<String, T> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -8813089059654810794L;
	private final HashMap<String, MutablePair<HashMap<T, Integer>, Integer>> freqTable;
	private final double theta;
	private final double thetaPlusOne;
	// @SuppressWarnings("unused")
	// private final Map<T, Double> aprioriProbs;
	protected ITagMapper<T> mapper = null;
	protected String lemmaMapper = null;

	@Override
	public void setTagMapper(ITagMapper<T> mapper) {
		this.mapper = mapper;
	}

	// protected String lastWord = "";
	// protected T lastTag;
	// protected double lastLogProb;

	// protected Logger logger = Logger.getLogger(this.getClass());

	HashSuffixGuesser(
			HashMap<String, MutablePair<HashMap<T, Integer>, Integer>> freqTable,
			// Map<T, Double> aprioriProbs,
			double theta) {
		// this.aprioriProbs = aprioriProbs;
		this.freqTable = freqTable;
		this.theta = theta;
		this.thetaPlusOne = theta + 1;
	}

	@Override
	public Map<T, Double> getTagLogProbabilities(String word) {
		HashMap<T, Double> ret = new HashMap<T, Double>();
		// Set<T> tags = freqTable.get("").getLeft().keySet();
		// for (T tag : tags) {
		// ret.put(tag, getTagLogProbability(word, tag));
		// }
		// return ret;
		Map<T, Double> probs = getTagProbabilities(word);
		for (Map.Entry<T, Double> entry : probs.entrySet()) {
			ret.put(entry.getKey(), Math.log(entry.getValue()));
		}
		return ret;
	}

	@Override
	public Map<T, Double> getSmoothedTagLogProbabilities(String word) {
		HashMap<T, Double> ret = new HashMap<T, Double>();
		// Set<T> tags = freqTable.get("").getLeft().keySet();
		// for (T tag : tags) {
		// ret.put(tag, getTagLogProbability(word, tag));
		// }
		// return ret;
		Map<T, Double> probs = getTagProbabilities(word);
		for (Map.Entry<T, Double> entry : probs.entrySet()) {
			ret.put(entry.getKey(), Math.log(entry.getValue()));
		}
		return ret;
	}

	// protected Double smooth(Double val) {
	// return val;
	//
	// }

	public Map<T, Double> getTagProbabilities(String word) {
		Map<T, Double> mret = new HashMap<T, Double>();
		// Set<T> tags = freqTable.get("").getLeft().keySet();
		// for (T tag : tags) {
		// mret.put(tag, 0.0);
		// }
		for (int i = word.length(); i >= 0; --i) {
			// TODO: HA a kötjel, vagy egyéb guesser-jellegű probléma merül fel, azt itt kell kezelni,
			// nem pedig mindenféle postprocess gányolással.
			String suff = word.substring(i);
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suff);
			if (suffixValue != null) {
				Map<T, Integer> tagSufFreqs = suffixValue.getLeft();
				for (Map.Entry<T, Integer> entry : tagSufFreqs.entrySet()) {
					T tag = entry.getKey();
					Double tagSufFreqD = entry.getValue().doubleValue();
					Double relFreq = 0.0;
					Double ret = mret.get(tag);
					if (ret == null)
						ret = 0.0;

					relFreq = tagSufFreqD / suffixValue.getRight();

					mret.put(tag, (ret + (relFreq * theta)) / thetaPlusOne);

					// logger.debug("accu(" + tag + ") = (prev(" + retP
					// + ") + relfreq(" + relFreq + ") * theta(" + theta
					// + "))/thetaPO(" + thetaPlusOne + ") =" + ret);

				}
			}
		}
		return mret;

	}

	@Override
	public double getTagLogProbability(String word, T tag) {
		// System.out.println(tag + "\t" + word);
		// if (word == lastWord && tag == lastTag) {
		// return lastLogProb;
		// } else {
		double logProb = Math.log(getTagProbability(word, tag));
		// lastWord = word;
		// lastTag = tag;
		// lastLogProb = logProb;
		return logProb;// - Math.log(aprioriProbs.get(tag));
		// }
	}

	@Override
	public double getTagProbability(String word, T tag) {
		if (mapper != null) {
			tag = mapper.map(tag);
		}
		// TODO: are you sure to calculate with the empty suffix as well?
		// (Brants does this, but how about Halácsy?)
		// return getTagProbTnT(word, word.length(), tag);

		Map<T, Double> ret = getTagProbabilities(word);
		Double val = ret.get(tag);
		if (val != null)
			return val;
		else
			return 0.0;
		// return getTagProbHunPOS(word, tag);

		// Double ret = 0.0;
		// ret = getTagProbBoosted(word, tag, 2);
		// if (ret == 0)
		// ret = getTagProbBoosted(word, tag, 1);
		// if (ret == 0)
		// ret = getTagProbBoosted(word, tag, 0);
		// return ret;
		// return getTagProbRevHunPOS(word, tag);
	}

	@Deprecated
	protected double getTagProbBoosted(String word, T tag, Integer offset) {
		Double ret = 0.0;
		for (int i = word.length() - offset; i >= 0; --i) {
			String suff = word.substring(i);
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suff);
			if (suffixValue != null) {
				Integer tagSufFreq = suffixValue.getLeft().get(tag);
				Double relFreq = 0.0;
				if (tagSufFreq != null) {
					Double tagSufFreqD = tagSufFreq.doubleValue();
					relFreq = tagSufFreqD / suffixValue.getRight();

					ret = (ret + (relFreq * theta)) / thetaPlusOne;
					// logger.debug("accu(" + tag + ") = (prev(" + retP
					// + ") + relfreq(" + relFreq + ") * theta(" + theta
					// + "))/thetaPO(" + thetaPlusOne + ") =" + ret);
				} else {
					break;
				}
			}
		}
		return ret;
	}

	protected double getTagProbHunPOS(String word, T tag) {
		Double ret = 0.0;
		for (int i = word.length(); i >= 0; --i) {
			String suff = word.substring(i);
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suff);
			if (suffixValue != null) {
				Integer tagSufFreq = suffixValue.getLeft().get(tag);
				Double relFreq = 0.0;
				if (tagSufFreq != null) {
					Double tagSufFreqD = tagSufFreq.doubleValue();
					relFreq = tagSufFreqD / suffixValue.getRight();

					ret = (ret + (relFreq * theta)) / thetaPlusOne;
					// logger.debug("accu(" + tag + ") = (prev(" + retP
					// + ") + relfreq(" + relFreq + ") * theta(" + theta
					// + "))/thetaPO(" + thetaPlusOne + ") =" + ret);
				} else {
					break;
				}
			}
		}
		return ret;
	}

	@Deprecated
	protected double getTagProbRevHunPOS(String word, T tag) {
		Double ret = 0.0;
		for (int i = 0; i <= word.length(); ++i) {
			String suff = word.substring(i);
			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suff);
			if (suffixValue != null) {
				Integer tagSufFreq = suffixValue.getLeft().get(tag);
				Double relFreq = 0.0;
				if (tagSufFreq != null) {
					Double tagSufFreqD = tagSufFreq.doubleValue();
					relFreq = tagSufFreqD / suffixValue.getRight();

					ret = (ret + (relFreq * theta)) / thetaPlusOne;
					// logger.debug("accu(" + tag + ") = (prev(" + retP
					// + ") + relfreq(" + relFreq + ") * theta(" + theta
					// + "))/thetaPO(" + thetaPlusOne + ") =" + ret);
				}
			}
		}
		return ret;
	}

	/**
	 * Calculates the probability for a given suffix and tag.
	 * 
	 * @param word
	 *            the word which has the suffix
	 * @param index
	 *            end position of the suffix (usually the length of the suffix)
	 * @param tag
	 *            POS tag
	 * @return
	 */
	@Deprecated
	protected double getTagProbTnT(String word, int index, T tag) {

		if (index >= 0 && freqTable.containsKey(word.substring(index))) {
			String suffix = word.substring(index);

			MutablePair<HashMap<T, Integer>, Integer> suffixValue = freqTable
					.get(suffix);
			Integer tagSufFreq = suffixValue.getLeft().get(tag);
			Double tagSufFreqD;
			int newindex = index - 1;
			if (tagSufFreq == null) {
				newindex = -1;
				tagSufFreqD = 0.0;
			} else {
				tagSufFreqD = tagSufFreq.doubleValue();
			}
			Double relFreq = tagSufFreqD / suffixValue.getRight();
			double nTagProb = getTagProbTnT(word, newindex, tag);

			return (theta * relFreq + nTagProb) / thetaPlusOne;
		} else
			return 0;
	}

	@Deprecated
	@Override
	public T getMaxProbabilityTag(String word) {
		return getMaxProbabilityTag(getTagLogProbabilities(word));
	}

	@Override
	public String toString() {
		return freqTable.toString();
	}

	@Override
	public ITagMapper<T> getMapper() {
		return mapper;
	}

}
