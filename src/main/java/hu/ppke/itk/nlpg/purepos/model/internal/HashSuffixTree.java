package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import hu.ppke.itk.nlpg.purepos.model.SuffixTree;

import java.util.HashMap;

public class HashSuffixTree<T> extends
		SuffixTree<String, T> {
	HashMap<String, HashMap<T, Integer>> representation = new HashMap<String, HashMap<T, Integer>>();
	Integer totalFreq = 0;

	public HashSuffixTree(int maxSuffixLength) {
		super(maxSuffixLength);
	}

	@Override
	public void addWord(String word, T tag, int count) {
		int end = word.length();
		int start = Math.max(0, end - maxSuffixLength);
		for (int pointer = start; pointer < end; pointer++) {
			String suffix = word.substring(pointer);
			increment(suffix, tag, count);
		}
	}

	protected void increment(String suffix, T tag, int count) {
		if (representation.containsKey(suffix)) {
			HashMap<T, Integer> tagCounts = representation.get(suffix);
			if (tagCounts.containsKey(tag)) {
				tagCounts.put(tag, tagCounts.get(tag) + count);
			} else {
				tagCounts.put(tag, count);
			}
			// representation.put(suffix, tagCounts);
		} else {
			HashMap<T, Integer> value = new HashMap<T, Integer>();
			value.put(tag, count);
			representation.put(suffix, value);
		}
		totalFreq += count;
	}

	@Override
	public ISuffixGuesser<String, T> generateGuesser(float theta) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Using weighted average for standard deviation
	 */
	@Override
	public double calculateTheta(HashMap<T, Integer> aprioriProbs) {
		// TODO: understand how it really works -> weighted average of stddev
		double pAv = 0;
		for (Integer val : aprioriProbs.values()) {
			pAv += Math.pow(val, 2);
		}
		double theta = 0;
		for (Integer aProb : aprioriProbs.values()) {
			theta += aProb * Math.pow(aProb - pAv, 2);
		}

		return Math.sqrt(theta);
	}
}
