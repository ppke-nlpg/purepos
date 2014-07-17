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
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

/**
 * Implements a probability model for n-grams. Tags are represented as Integers.
 * 
 * @author György Orosz
 * 
 * @param <W>
 */
public class ProbModel<W> implements IProbabilityModel<Integer, W>,
		Serializable {

	private static final long serialVersionUID = -8143201121322353289L;
	protected ITagMapper<W> elementMapper = null;
	protected ITagMapper<Integer> contextMapper = null;

	@Override
	public void setElementMapper(ITagMapper<W> mapper) {
		this.elementMapper = mapper;
	}

	@Override
	public void setContextMapper(ITagMapper<Integer> mapper) {
		this.contextMapper = mapper;
	}

	// protected Logger logger = Logger.getLogger(getClass());
	// TODO: MEM: use a more memory efficient model for storing these data
	protected DoubleTrieNode<W> root;

	protected ProbModel(DoubleTrieNode<W> root) {
		this.root = root;
	}

	public ProbModel(IntTrieNode<W> origRoot, ArrayList<Double> lambdas) {
		this.root = createRoot(origRoot, lambdas);
	}

	@Override
	public Double getProb(List<Integer> context, W word) {
		// for a context which is greater then the size of the model, the
		// context is cut and the greatest context probability is calculated
		// String d = context.toString() + " " + word.toString();
		if (elementMapper != null) {
			word = elementMapper.map(word);
		}
		if (contextMapper != null) {
			context = contextMapper.map(context);
		}
		ListIterator<Integer> iterator = context.listIterator(context.size());
		TrieNode<Integer, Double, W> node = root;
		Boolean findMore = true;
		Integer prev = -1;
		if (iterator.hasPrevious()) {
			prev = iterator.previous();
			findMore = node.hasChild(prev) && node.getChild(prev).hasWord(word);
		} else
			findMore = false;

		while (findMore) {
			node = node.getChild(prev);
			if (iterator.hasPrevious()) {
				prev = iterator.previous();
				findMore = node.hasChild(prev)
						&& node.getChild(prev).hasWord(word);
			} else
				findMore = false;
		}
		if (node.hasWord(word))
			return node.getWord(word);
		else
			return 0.0;
	}

	@Override
	public Double getLogProb(List<Integer> context, W word) {
		Double prob = getProb(context, word);
		double lp = Math.log(prob);
		// logger.trace("\tlogprob for " + word + " and context " + context +
		// ":"
		// + lp);
		return lp;
	}

	// @Override
	// TODO: PERF: implement getProb() using this function
	public Map<W, Double> getWordProbs(List<Integer> context) {
		ListIterator<Integer> iterator = context.listIterator(context.size());
		TrieNode<Integer, Double, W> node = root;
		Boolean findMore = true;
		Integer prev = -1;
		if (iterator.hasPrevious()) {
			prev = iterator.previous();
			findMore = node.hasChild(prev);// &&
											// node.getChild(prev).hasWord(word);
		} else
			findMore = false;

		while (findMore) {
			node = node.getChild(prev);
			if (iterator.hasPrevious()) {
				prev = iterator.previous();
				findMore = node.hasChild(prev);
				// && node.getChild(prev).hasWord(word);
			} else
				findMore = false;
		}
		return node.getWords();
	}

	protected DoubleTrieNode<W> createRoot(IntTrieNode<W> node,
			ArrayList<Double> lambdas) {
		DoubleTrieNode<W> newRoot = calcProbs(node);
		// apply lambda multiplication
		double prob;
		for (Map.Entry<W, Double> e : newRoot.getWords().entrySet()) {
			prob = lambdas.get(0) + lambdas.get(1) * e.getValue();
			newRoot.addWord(e.getKey(), prob);
		}
		// for all childs in the node calculate the probs (check null case)
		Map<W, Double> words = newRoot.getWords();
		if (node.getChildNodes() != null)
			for (TrieNode<Integer, Integer, W> child : node.getChildNodes()
					.values()) {
				DoubleTrieNode<W> ch = createChild((IntTrieNode<W>) child,
						words, lambdas, 2);
				// TODO: FIXED should I handle the null case?
				newRoot.addChild(ch);

			}
		return newRoot;
	}

	protected DoubleTrieNode<W> createChild(IntTrieNode<W> originalNode,
			Map<W, Double> parentWords, ArrayList<Double> lambdas, int level) {
		if (lambdas.size() > level) {
			DoubleTrieNode<W> node = calcProbs(originalNode);
			// apply lambdas to word probs
			double lambda = lambdas.get(level);
			for (Map.Entry<W, Integer> wordEntry : originalNode.getWords()
					.entrySet()) {
				W word = wordEntry.getKey();
				double prob = parentWords.get(word);
				prob += lambda * originalNode.getAprioriProb(word);
				node.addWord(word, prob);
			}

			// for all child nodes apply the same
			if (originalNode.getChildNodes() != null)
				for (TrieNode<Integer, Integer, W> child : originalNode
						.getChildNodes().values()) {
					DoubleTrieNode<W> ch = createChild((IntTrieNode<W>) child,
							node.getWords(), lambdas, level + 1);
					if (ch != null)
						node.addChild(ch);
				}
			return node;
		} else {
			return null;
		}
	}

	protected DoubleTrieNode<W> calcProbs(IntTrieNode<W> node) {
		DoubleTrieNode<W> newRoot = new DoubleTrieNode<W>(node.getId());
		double tmpPrb = 0.0;
		// for all words in node calculate the probs
		for (W word : node.getWords().keySet()) {
			tmpPrb = node.getAprioriProb(word);
			newRoot.addWord(word, tmpPrb);
		}
		return newRoot;
	}

	public String getReprString() {
		return root.getReprString();
	}

	@Override
	public ITagMapper<Integer> getContextMapper() {
		return this.contextMapper;
	}

}
