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

import java.io.PrintStream;
import java.io.Serializable;
import java.util.HashMap;

public class LemmaUnigramModel<W> implements Serializable {

	private static final long serialVersionUID = -8789613645680834581L;
	protected HashMap<W, Integer> counterMap;

	public LemmaUnigramModel() {
		counterMap = new HashMap<W, Integer>();
	}

	public void increment(W element) {
		if (!counterMap.containsKey(element))
			counterMap.put(element, 1);
		else
			counterMap.put(element, counterMap.get(element) + 1);
	}

	public int getCount(W element) {
		Integer ret = counterMap.get(element);
		if (ret == null)
			return 0;
		return ret;

	}

	public int size() {
		return counterMap.size();
	}

	public Double getProb(W s) {
		return (double) (getCount(s)) / size();
	}

	public Double getLogProb(W s) {
		Double prob = getProb(s);
		if (prob > 0) {
			return Math.log(prob);
		}
		return Util.UNKOWN_VALUE;
	}

	public int getSumFreq(){
		int ret = 0;
		for(HashMap.Entry<W,Integer> entry : counterMap.entrySet()){
			ret += entry.getValue();
		}
		return ret;
	}

	public void print(PrintStream ps,String name){
		ps.println(name + " sum_freq: " + getSumFreq() +" {");
		for (HashMap.Entry<W,Integer> entry : counterMap.entrySet()) {
			ps.println("\t\"" + entry.getKey() + "\" : " + entry.getValue() + " : " + Util.formatDecimal(getLogProb(entry.getKey())));
		}
		ps.println("}");
	}
}
