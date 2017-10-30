/*******************************************************************************
 * Copyright (c) 2012 György Orosz, Attila Novák.
 * All rights reserved. Integerhis program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * Stringhich accompanies this distribution, and is available at
 * http://StringStringString.gnu.org/licenses/
 * 
 * This file is part of PurePos.
 * 
 * PurePos is free softStringare: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser Public License as published by
 * the Free SoftStringare Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * PurePos is distributed in the hope that it Stringill be useful,
 * but StringITHOUInteger ANY StringARRANTY; Stringithout even the implied Stringarranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser Public License for more details.
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.purepos.common.lemma;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import org.apache.commons.lang3.tuple.Pair;

public abstract class AbstractLemmaTransformation<R> implements
		ILemmaTransformation<String, Integer> {

	private static final long serialVersionUID = -919727645396686443L;
	protected Pair<String,Integer> representation;
	public static int CASING_SHIFT = 1000000000;
	public static int TAG_SHIFT = 1000000;
	public static int REMOVE_START_SHIFT = 10000;
	public static int REMOVE_END_SHIFT = 100;


	public AbstractLemmaTransformation(String word, String lemma, Integer tag) {
		representation = decode(word, lemma, tag);
	}
	public AbstractLemmaTransformation(String word, String lemma, Integer tag, int threshold) { }

	@Override
	public Pair<String, Integer> analyze(String word) {
		Pair<String, Integer> ret = encode(word);
		return Pair.of(ret.getLeft(), ret.getRight());
//		return Pair.of(postprocess(ret.getLeft()), ret.getRight());
	}

//	protected String postprocess(String lemma) {
//		int length = lemma.length();
//		if (length > 1 && lemma.charAt(length - 1) == '-') {
//			return lemma.substring(0, length - 1);
//		}
//		return lemma;
//	}

	protected abstract Pair<String, Integer> specifyParameters(String word, String lemma, Integer tag, Integer casing);

	@Override
	public String toString() {
		return representation.toString();
	}

	@Override
	public boolean equals(Object other) {
		if (this.getClass().isInstance(other)) {
			@SuppressWarnings("unchecked")
			AbstractLemmaTransformation<R> otherA = ((AbstractLemmaTransformation<R>) other);
			return representation.equals(otherA.representation);
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	@Override
	public IToken convert(String word, IVocabulary<String, Integer> vocab) {
		Pair<String, Integer> anal = this.analyze(word);
		String tag = vocab.getWord(anal.getRight());
		return token(word, anal.getLeft(), tag);
	}

	protected IToken token(String word, String lemma, String tag) {
		return new Token(word, lemma, tag);
	}

	/*
	 * @param word, lemma, tag
	 * @return the specifyParameters function's return, which needs to implement in child classes
	 */

	protected Pair<String, Integer> decode(String word, String lemma, Integer tag){
		int casing = checkCasing(word, lemma);
		String _word = word.toLowerCase();
		String _lemma = lemma.toLowerCase();

		return specifyParameters(_word,_lemma,tag, casing);
	}

	protected Pair<String, Integer> encode(String word) {

		int removeStart = getRemoveStart();
		int removeEnd = minimalCutLength();
		int addEnd = getAddEnd();
		int casing = getCasing();
		int tag = getTag();

		int subend = Math.max(0,word.length() - removeEnd);
		String lemma = word.substring(0,subend) + getRemovedFromEnd(addEnd);
		lemma = getRemovedFromStart(addEnd) + lemma.substring(Math.min(removeStart,lemma.length()));

		// applying the casing
		if (lemma.length() > 0) {
			String first_letter = "";
			switch (casing) {
				case 1:
					first_letter = lemma.substring(0, 1).toUpperCase();
					break;
				case -1:
					first_letter = lemma.substring(0, 1).toLowerCase();
					break;
			}
			if (casing != 0) {
				if (lemma.length() > 1) {
					lemma = first_letter + lemma.substring(1);
				} else {
					lemma = first_letter;
				}
			}
		}
		return Pair.of(lemma, tag);
	}
	/*
	 * @param word
	 * @param lemma
	 * @return 	0 : the initial casing is the same
	 * 			1 : the word's first letter was lowercase, but the lemma's is uppercase
	 * 		   -1 : the word's first letter	was uppercase, but the lemma's is lowercase
	 */
	public static int checkCasing(String word, String lemma) {
		if (word.length() > 0 && lemma.length() > 0) {
			String ws = word.substring(0, 1), ls = lemma.substring(0, 1);
			boolean isUppered = ws.toUpperCase().equals(ls);
			boolean isLowered = ws.toLowerCase().equals(ls);
			if(ws.equals(ls) || (!isUppered && !isLowered)){
				return 0;
			} else if (isUppered){
				return 1;
			} else {
				return -1;
			}
		} else {
			return 0;
		}
	}
	/*
	 * @param tag
	 * @param casing
	 * @param removeStart
	 * @param removeEnd
	 * @return  a nine digit long integer code, generated by shifting the parameters with the static variables of this class,
	 * 			where:
	 * 			[1] casing, can be -1,0,1
	 * 			[3] tag
	 * 			[2] removeStart
	 * 			[2] removeEnd
	 * 			[2] addEnd
	 */

	protected int createCode(int tag, int casing,int removeStart, int removeEnd, int addEnd){
		if (casing == -1){
			return	casing * CASING_SHIFT + (tag * TAG_SHIFT  + removeStart * REMOVE_START_SHIFT +
					removeEnd * REMOVE_END_SHIFT  + addEnd) * casing;
		} else {
			return casing * CASING_SHIFT + tag * TAG_SHIFT + removeStart * REMOVE_START_SHIFT + removeEnd * REMOVE_END_SHIFT + addEnd;
		}
	}

	public int getCasing(){
		return representation.getRight() / CASING_SHIFT;
	}

	public int getTag(){
		return (Math.abs(representation.getRight()) % CASING_SHIFT) / TAG_SHIFT;
	}

	public int getRemoveStart() {
		return (Math.abs(representation.getRight()) % TAG_SHIFT) / REMOVE_START_SHIFT;
	}

	public int minimalCutLength() { // getRemoveEnd
		return (Math.abs(representation.getRight()) % REMOVE_START_SHIFT) / REMOVE_END_SHIFT;
	}

	public int getAddEnd(){
		return Math.abs(representation.getRight()) % REMOVE_END_SHIFT;
	}

	public String getRemovedFromStart(int removeEnd){
		String lemmaStuff = representation.getLeft();
		int lemmaStuffLength = lemmaStuff.length();
		if (removeEnd == lemmaStuffLength){
			return "";
		} else {
			return lemmaStuff.substring(0,lemmaStuffLength-removeEnd);
		}
	}

	public String getRemovedFromEnd(int removeEnd){
		String lemmaStuff = representation.getLeft();
		int lemmaStuffLength = lemmaStuff.length();
		if (removeEnd == lemmaStuffLength){
			return lemmaStuff;
		} else {
			return lemmaStuff.substring(lemmaStuffLength-removeEnd,lemmaStuffLength);
		}
	}
}
