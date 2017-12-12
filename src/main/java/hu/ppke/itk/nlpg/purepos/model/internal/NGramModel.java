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

import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.Transformation;
import hu.ppke.itk.nlpg.purepos.model.INGramModel;

import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * N-gram model implementation which uses tries to store these elements.
 * (Similar to SRILM.)
 * 
 * Tries are stored in HashTables, for the sake of efficiency.
 * 
 * @author György Orosz
 * 
 * @param <C>
 *            context type
 * @param <W>
 *            word type
 */
public class NGramModel<W> extends INGramModel<Integer, W> implements
		Serializable {

	private static final long serialVersionUID = 5159356902216485765L;
	// Logger logger = Logger.getLogger(this.getClass());
	protected IntTrieNode<W> root;
	/*
	 * lambda1 is at position 1 and so on; lamda0 is seen to be used in Hunpos
	 * when calculating probs:
	 * 
	 * P(C| A B) = l3 * ML (C| A B) + l2 * ML (C | B) + l1 * ML (C) + l0
	 */
	protected ArrayList<Double> lambdas;

	public NGramModel(int n) {
		super(n);
		root = new IntTrieNode<W>(IntVocabulary.getExtremalElement());
		lambdas = new ArrayList<Double>();
	}

	@Override
	public void addWord(List<Integer> context, W word) {
		ListIterator<Integer> iterator = context.listIterator(context.size());
		IntTrieNode<W> act = root;
		int i = 0;
		int size = n - 1;
		act.addWord(word);
		while (iterator.hasPrevious() && i < size) {
			act = (IntTrieNode<W>) act.addChild(iterator.previous());
			act.addWord(word);
			i++;
		}

	}

	@Override
	public List<Double> getWordFrequency(List<Integer> context, W word) {
		ArrayList<Double> ret = new ArrayList<Double>();

		ret.add(root.getAprioriProb(word));
		if (!(context == null || context.size() == 0)) {
			ListIterator<Integer> it = context.listIterator(context.size());
			Integer previous;
			IntTrieNode<W> actNode = root;
			while (it.hasPrevious() && actNode != null) {
				previous = it.previous();
				if (actNode.hasChild(previous)) {
					actNode = (IntTrieNode<W>) actNode.getChild(previous);
					ret.add(actNode.getAprioriProb(word));
				} else {
					ret.add(0.0);
					while (it.hasPrevious()) {
						ret.add(0.0);
					}
					actNode = null;
				}
			}

		}

		return ret;
	}

	@Override
	public int getTotalFrequency() {
		return root.getNum();
	}

	protected double calculateModifiedFreqVal(
			List<TrieNode<Integer, Integer, W>> nodeList, int position, W word) {
		double contextFreq = nodeList.get(position).getNum();
		double wordFreq = nodeList.get(position).getWord(word);
		if (contextFreq == 1 || wordFreq == 1)
			return -1;
		else
			// TODO: RESEARCH: what if we would substract any value instead of
			// 1?
			return (wordFreq - 1) / (contextFreq - 1);

	}

	/**
	 * Finds the maximal frequency element in a nodelist.
	 * 
	 * @param list
	 * @param word
	 * @return
	 */
	protected Pair<Integer, Double> findMax(
			ArrayList<TrieNode<Integer, Integer, W>> list, W word) {

		Integer maxPos;
		Double maxVal;
		if (!(list == null || list.size() == 0)) {
			maxPos = -1;
			maxVal = 0.0;
			for (int i = list.size() - 1; i >= 0; --i) {
				double val = calculateModifiedFreqVal(list, i, word);
				if (val > maxVal) {
					maxPos = i;
					maxVal = val;
				}

			}
		} else {
			maxPos = null;
			maxVal = null;
		}
		ImmutablePair<Integer, Double> ret = new ImmutablePair<Integer, Double>(
				maxPos, maxVal);
		return ret;
	}

	@Override
	protected void calculateNGramLamdas() {
		adjustLamdas();
		// logger.trace("pure lambdas: " + lambdas);
		// normalization
		double sum = 0.0;
		lambdas.set(0, 0.0);
		for (Double e : lambdas) {
			sum += e;
		}
		// logger.debug(lambdas.toString());
		if (sum > 0) {
			for (int i = 0; i < lambdas.size(); ++i) {
				lambdas.set(i, lambdas.get(i) / sum);
			}
		}
		// logger.debug(lambdas);
		// logger.debug(lambdas.toString());
	}

	/**
	 * Calculate the lambdas, without smoothing
	 */
	protected void adjustLamdas() {
		lambdas = new ArrayList<Double>();
		for (int i = 0; i < n + 1; ++i) {
			lambdas.add(0.0);
		}
		ArrayList<TrieNode<Integer, Integer, W>> acc = new ArrayList<TrieNode<Integer, Integer, W>>();
		iterate(root, acc);
	}

	protected void iterate(TrieNode<Integer, Integer, W> node,
			ArrayList<TrieNode<Integer, Integer, W>> acc) {
		acc.add(node);
		if (node.getChildNodes() == null || node.getChildNodes().size() == 0) {
			for (W word : node.getWords().keySet()) {
				Pair<Integer, Double> max = findMax(acc, word);
				int index = max.getKey() + 1;
				if (max.getValue() != -1) {
					lambdas.set(index, lambdas.get(index) + node.getWord(word));
				}
				// logger.debug("Max:" + max + " add:" + node.getWord(word)
				// + " to:" + index + " lambdas:" + lambdas);
			}
		} else {
			for (TrieNode<Integer, Integer, W> child : node.getChildNodes()
					.values()) {
				iterate(child, acc);

			}
		}
		acc.remove(acc.size() - 1);
	}

	@Override
	public ProbModel<W> createProbabilityModel() {
		// logger.trace("NGramModel: " + getReprString());
		calculateNGramLamdas();
		return new ProbModel<W>(root, lambdas);
	}

	@Override
	public Map<W, Integer> getWords() {
		return root.getWords();
	}

	@Override
	public Map<W, Double> getWordAprioriProbs() {
		Map<W, Double> ret = new HashMap<W, Double>();
		double sumFreg = root.getNum();
		for (Entry<W, Integer> e : root.getWords().entrySet()) {
			double val = e.getValue();
			ret.put(e.getKey(), val / sumFreg);
		}
		return ret;
	}
	@Override
	public String getReprString() {
		calculateNGramLamdas();
		return root.getReprString() + "lambdas: " + lambdas;
	}

	@Override
	public HashMap<String,MutablePair<HashMap<String,String>,String>> getNodes(){
		return root.getNodes("");
	}

	@Override
	public ArrayList<Pair<String, String>> getEdges() {
		HashMap<String,ArrayList<String>> graph = root.getEdges("");
		ArrayList<Pair<String,String>> ret = new ArrayList<Pair<String, String>>();

		for (Entry<String,ArrayList<String>> node : graph.entrySet()){
			String key = node.getKey();
			for (String child : node.getValue()) {
				ret.add(Pair.of(key, child));
			}
		}
		return ret;
	}

	public void print(PrintStream ps,HashMap<String,MutablePair<HashMap<String,String>,String>> compiled,
					  boolean dot, String name, String mode){
		String header = "digraph " + name + " {";
		String footer = "}";

		ps.println(header);
		if (dot){
			NGramModel.printNodesDot(getNodes(),compiled,ps, mode);
		} else {
			NGramModel.printNodes(getNodes(),compiled,ps, mode);
		}
		NGramModel.printEdges(getEdges(),ps);
		ps.println(footer);

	}

	protected static void printNodes(HashMap<String,MutablePair<HashMap<String,String>,String>> raw,
							  HashMap<String,MutablePair<HashMap<String,String>,String>> compiled,
							  PrintStream ps, String mode){
		String newline = "\n";
		String tab = "\t";
		for(Map.Entry<String,MutablePair<HashMap<String,String>,String>> raw_entry: raw.entrySet()){
			String node = "";
			node += tab + raw_entry.getKey()  + " freq: "+ raw_entry.getValue().right;
			for (Map.Entry<String,String> word : raw_entry.getValue().left.entrySet()){
				String key = "";
				if (mode.equals("tag")){
					 key = Transformation.decodeTag(word.getKey(),Util.tagVocabulary);
				} else {
					 key = word.getKey();
				}
				node += newline+tab+tab+ "\"" + key + "\" : " + word.getValue();
				String freq = compiled.get(raw_entry.getKey()).left.get(word.getKey());
				if(!freq.equals("0.0")){
					node += " : " + freq;
				}
			}
			ps.println(node);
		}
	}

	protected static void printNodesDot(HashMap<String,MutablePair<HashMap<String,String>,String>> raw,
									 HashMap<String,MutablePair<HashMap<String,String>,String>> compiled,
									 PrintStream ps, String mode){

		String newline = "\\n";
		for(Map.Entry<String,MutablePair<HashMap<String,String>,String>> raw_entry: raw.entrySet()){
			String node = "";
			node += "\t\"" + raw_entry.getKey() +"\"" + "[label = \""+ raw_entry.getKey() + newline + "freq: "+ raw_entry.getValue().right + newline;
			for (Map.Entry<String,String> word : raw_entry.getValue().left.entrySet()){
				String key = "";
				if (mode.equals("tag")){
					key = Transformation.decodeTag(word.getKey(),Util.tagVocabulary);
				} else {
					key = word.getKey();
				}
				node += key + " : " + word.getValue();
				String freq = compiled.get(raw_entry.getKey()).left.get(word.getKey());
				if(!freq.equals("0.0")){
					node += " : " + freq;
				}
				node += newline;
			}
			node += "\"];";
			ps.println(node);
		}
	}

	protected static void printEdges(ArrayList<Pair<String, String>> edges, PrintStream ps){
		String edge_str = "->";
		String mark = "\"";

		for(Pair<String,String> edge: edges){
			ps.println("\t"+ mark + edge.getLeft() + mark + edge_str + mark + edge.getRight() + mark);
		}
	}
}
