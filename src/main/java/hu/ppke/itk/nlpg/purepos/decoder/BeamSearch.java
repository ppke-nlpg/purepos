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

import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.NGram;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * Decoder that implements the beam search method.
 * 
 * @author György Orosz
 * 
 */
public class BeamSearch extends AbstractDecoder {
	public BeamSearch(CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, double logTheta,
			double sufTheta, int maxGuessedTags) {
		super(model, morphologicalAnalyzer, logTheta, sufTheta, maxGuessedTags);

	}

	// protected Logger logger = Logger.getLogger(this.getClass());

	@Override
	public List<Integer> decode(List<String> observations) {
		List<String> obs = new ArrayList<String>(observations);

		obs.add(Model.getEOSToken()); // adds 1 EOS marker as in HunPos
		int n = model.getTaggingOrder();

		ArrayList<Integer> startTags = new ArrayList<Integer>();
		for (int j = 0; j <= n; ++j) {
			startTags.add(model.getBOSIndex());
		}

		NGram<Integer> startNGram = new NGram<Integer>(startTags,
				model.getTaggingOrder());

		List<Integer> list = beamSearch(startNGram, obs);
		return list.subList(0, list.size() - 1);

	}

	public List<Integer> beamSearch(final NGram<Integer> start,
			final List<String> obs) {
		return beamSearch(start, obs, 1);

	}

	public List<Integer> beamSearch(final NGram<Integer> start,
			final List<String> observations, int resultsNumber) {
		HashMap<NGram<Integer>, Node> beam = new HashMap<NGram<Integer>, Node>();

		beam.put(start, new Node(start, 0.0, null));
		boolean isFirst = true;
		int pos = 0;
		for (String obs : observations) {
			// System.err.println(obs);

			// logger.trace("Current observation " + obs);
			// logger.trace("\tCurrent states:");
			// for (Entry<NGram<Integer>, Node> entry : beam.entrySet()) {
			// logger.trace("\t\t" + entry.getKey() + " - " + entry.getValue());
			// }

			HashMap<NGram<Integer>, Node> newBeam = new HashMap<NGram<Integer>, Node>();

			Table<NGram<Integer>, Integer, Double> nextProbs = HashBasedTable
					.create();
			Map<NGram<Integer>, Double> obsProbs = new HashMap<NGram<Integer>, Double>();
			Set<NGram<Integer>> contexts = beam.keySet();

			Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> nexts = getNextProbs(
					contexts, obs, pos, isFirst);

			for (Map.Entry<NGram<Integer>, Map<Integer, Pair<Double, Double>>> nextsEntry : nexts
					.entrySet()) {
				NGram<Integer> context = nextsEntry.getKey();
				Map<Integer, Pair<Double, Double>> nextContextProbs = nextsEntry
						.getValue();
				for (Map.Entry<Integer, Pair<Double, Double>> entry : nextContextProbs
						.entrySet()) {
					Integer tag = entry.getKey();
					nextProbs.put(context, tag, entry.getValue().getLeft());
					obsProbs.put(context.add(tag), entry.getValue().getRight());
				}
			}
			// for (Integer t : nextProbs.keySet()) {
			// logger.trace("\t\tNext node:" + context + t);
			// logger.trace("\t\tnode currentprob:"
			// + (beam.get(context) + nextProbs.get(t).getLeft()));
			// logger.trace("\t\tnode emissionprob:"
			// + nextProbs.get(t).getRight());
			// logger.trace("\n");
			// // logger.trace("\t\tNext node:" + context + t);
			// }
			for (Cell<NGram<Integer>, Integer, Double> cell : nextProbs
					.cellSet()) {
				Integer nextTag = cell.getColumnKey();
				NGram<Integer> context = cell.getRowKey();
				Double transVal = cell.getValue();
				NGram<Integer> newState = context.add(nextTag);
				Node from = beam.get(context);
				double newVal = transVal + beam.get(context).getWeight();
				update(newBeam, newState, newVal, from);
			}
			// adding observation probabilities
			// logger.trace("beam" + newBeam);
			if (nextProbs.size() > 1)
				for (NGram<Integer> tagSeq : newBeam.keySet()) {
					// Integer tag = tagSeq.getLast();
					Node node = newBeam.get(tagSeq);
					// Double prevVal = node.getWeight();

					Double obsProb = obsProbs.get(tagSeq);
					// logger.trace("put to beam: " + context + "(from) "
					// + tagSeq + " " + prevVal + "+" + obsProb);
					node.setWeight(obsProb + node.getWeight());
				}

			beam = prune(newBeam);
			isFirst = false;
			// for (Entry<NGram<Integer>, Node> e : beam.entrySet()) {
			// logger.trace("\t\tNode state: " + e.getKey() + " "
			// + e.getValue());
			// }
			++pos;
		}
		return findMax(beam);
	}

	private List<Integer> findMax(final HashMap<NGram<Integer>, Node> beam) {

		Node max = Collections.max(beam.values());
		// Node act = max;
		return decompose(max);

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

	private HashMap<NGram<Integer>, Node> prune(
			final HashMap<NGram<Integer>, Node> beam) {

		HashMap<NGram<Integer>, Node> ret = new HashMap<NGram<Integer>, Node>();
		// System.err.println(beam);
		// try {
		Node maxNode = Collections.max(beam.values());
		Double max = maxNode.getWeight();
		for (NGram<Integer> key : beam.keySet()) {
			Node actNode = beam.get(key);
			Double actVal = actNode.getWeight();
			if (!(actVal < max - logTheta)) {
				ret.put(key, actNode);
			}
		}
		// } catch (Exception e) {
		// e.printStackTrace();
		// System.err.println(beam);
		// }
		return ret;

	}

	private void update(HashMap<NGram<Integer>, Node> beam,
			NGram<Integer> newState, Double newWeight, Node fromNode) {

		if (!beam.containsKey(newState)) {
			// logger.trace("\t\t\tAS: " + newNGram + " from " + context
			// + " with " + newValue);

			beam.put(newState, new Node(newState, newWeight, fromNode));

		} else if (beam.get(newState).getWeight() < newWeight) {
			// logger.trace("\t\t\tUS: " + old + " to " + newNGram + " from "
			// + context + " with " + newValue);
			beam.get(newState).setPrevious(fromNode);
			beam.get(newState).setWeight(newWeight);
		} else {
			// logger.trace("\t\t\tNU: " + old + " to " + newNGram + " from "
			// + context + " with " + newValue);
		}
	}
}
