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
import org.apache.commons.lang3.tuple.MutablePair;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static hu.ppke.itk.nlpg.purepos.common.Util.tagVocabulary;

/**
 * Trie node, storing frequencies / probabilities of a given n-gram part
 * 
 * @author György Orosz
 * 
 * @param <I>
 *            type for nodeID
 * @param <N>
 *            type for frequency / probability
 * @param <W>
 *            type for words
 */
public abstract class TrieNode<I, N extends Number, W> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5867650276492059939L;
	// TODO: PERF: is it worth using berkeleylm, instead of my implementation:
	// it is
	// known to be fast and small
	protected final I id;
	protected N num;
	protected HashMap<I, TrieNode<I, N, W>> childNodes;
	protected final HashMap<W, N> words;

	/**
	 * Zero element of the given Number type
	 * 
	 * @return
	 */
	protected abstract N zero();

	/**
	 * Incrementing the given Number object
	 * 
	 * @param n
	 * @return
	 */
	protected abstract N increment(N n);

	protected abstract TrieNode<I, N, W> createNode(I id);

	/**
	 * Constructor
	 * 
	 * @param id
	 *            The id of the node (usually the context part)
	 * @param word
	 *            word to store
	 */
	public TrieNode(I id, W word) {
		this(id);
		addWord(word);
	}

	protected TrieNode(I id) {
		this.id = id;
		num = zero();
		words = new HashMap<W, N>();
	}

	/**
	 * Add a word the the node, and increments its frequency.
	 * 
	 * @param word
	 */
	protected void addWord(W word) {
		if (words.containsKey(word)) {
			words.put(word, increment(words.get(word)));
		} else {
			words.put(word, increment(zero()));
		}
		num = increment(num);
	}

	/**
	 * Add a child to the node.
	 * 
	 * @param child
	 */
	public TrieNode<I, N, W> addChild(I child) {
		if (childNodes == null) {
			childNodes = new HashMap<I, TrieNode<I, N, W>>();
		}
		if (!childNodes.containsKey(child)) {
			TrieNode<I, N, W> childNode = createNode(child);
			childNodes.put(child, childNode);
			return childNode;
		} else {
			return childNodes.get(child);
		}
	}

	/**
	 * Return the id of the node.
	 * 
	 * @return
	 */
	public I getId() {
		return id;
	}

	/**
	 * Returns the frequency / probability of the node.
	 * 
	 * @return
	 */
	public N getNum() {
		return num;
	}

	/**
	 * Returns child nodes.
	 * 
	 * @return
	 */
	public Map<I, TrieNode<I, N, W>> getChildNodes() {
		return childNodes;
	}

	/**
	 * Returns words and their frequencies / probabilities.
	 * 
	 * @return
	 */
	public Map<W, N> getWords() {
		return words;
	}

	/**
	 * Returns true if has a child node with the specified id.
	 * 
	 * @param id
	 * @return
	 */
	public boolean hasChild(I id) {
		if (childNodes == null)
			return false;
		return childNodes.containsKey(id);
	}

	/**
	 * Returns the node with the specified id.
	 * 
	 * @param id
	 * @return
	 */
	public TrieNode<I, N, W> getChild(I id) {
		if (childNodes == null)
			return null;
		return childNodes.get(id);
	}

	/**
	 * Returns true if this node has the requested word.
	 * 
	 * @param word
	 * @return
	 */
	public boolean hasWord(W word) {
		if (words == null)
			return false;
		return words.containsKey(word);
	}

	/**
	 * Returns the numeric value according to the word.
	 * 
	 * @param word
	 * @return
	 */
	public N getWord(W word) {
		return words.get(word);
	}
	@Override
	public String toString() {
		return "(id:" + getId() // + ", childs:" + childNodes.toString()
				+ ", words:" + words.toString() + ")";
	}

	public String getReprString() {
		return getReprString("\t",0);
	}

	public String getReprString(String tab, int m) {
		String ret = "";
		for (int i = 0; i< m; i++){
			ret += tab;
		}
		String id_ = id.toString();
		if (id_.equals("-1")){
			ret += "root";
		} else {
			ret += Transformation.decodeTag(id_,Util.tagVocabulary);
		}
		ret += ": " + getNum();
		ret += ", words:" + words.toString();
		if (childNodes != null && childNodes.size() > 0) {
			ret += "\n";
			for (TrieNode<I, N, W> node : childNodes.values())
				ret += node.getReprString(tab ,m+1);
			for (int i = 0; i< m; i++){
				ret += tab;
			}
		}
		ret += ")\n";
		return ret;
	}

	public HashMap<String,MutablePair<HashMap<String,String>,String>> getNodes(String parentTag){
			HashMap<String,MutablePair<HashMap<String,String>,String>> storage = new HashMap<String, MutablePair<HashMap<String, String>, String>>();
			String tag = createTag(parentTag);

			storage.put(tag,getNode());

			if (childNodes != null && childNodes.size() > 0) {
				for (TrieNode<I, N, W> child : childNodes.values()) {
					storage.putAll(child.getNodes(tag));
				}
			}

			return storage;
	}

	protected String createTag(String parentTag){
		String tag = "";
		if (getId().toString().equals("-1")){
			tag = "root";
		} else {
			if(!parentTag.equals("root")){
				tag += parentTag + " ";
			}
			tag += Transformation.decodeTag(this.id.toString(),Util.tagVocabulary);
		}
		return tag;
	}

	protected MutablePair<HashMap<String,String>,String> getNode(){
		HashMap<String,String> words = new HashMap<String, String>();
		for (HashMap.Entry<W,N> word: this.words.entrySet()){
			words.put(word.getKey().toString(),word.getValue().toString());
		}
		return new MutablePair<HashMap<String, String>, String>(words,getNum().toString());
	}

	public HashMap<String,ArrayList<String>> getEdges(String parentTag){

		HashMap<String,ArrayList<String>> storage = new HashMap<String,ArrayList<String>>();
		ArrayList<String> childs = new ArrayList<String>();

		String tag = createTag(parentTag);

		if (childNodes != null && childNodes.size() > 0) {
			for (TrieNode<I, N, W> child : childNodes.values()) {
				String childName = "";
				if (!getId().toString().equals("-1")){
					childName += tag + " ";
				}
				childName += Transformation.decodeTag(child.id.toString(), Util.tagVocabulary);
				childs.add(childName);
				storage.putAll(child.getEdges(tag));
			}
		}
		storage.put(tag, childs);
		return storage;
	}
}
