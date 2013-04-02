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


/**
 * Represents a tag sequence and its probability.
 * 
 * @author György Orosz
 * 
 */
public class History implements Comparable<History> {

	protected final NGram<Integer> tagSeq;
	protected final Double logProb;

	public NGram<Integer> getTagSeq() {
		return tagSeq;
	}

	public Double getLogProb() {
		return logProb;
	}

	public History(NGram<Integer> tagSeq, Double logProb) {
		this.logProb = logProb;
		this.tagSeq = tagSeq;
	}

	@Override
	public int hashCode() {
		return tagSeq.hashCode();
	}

	@Override
	public int compareTo(History o) {
		return Double.compare(logProb, o.logProb);
	}

	@Override
	public String toString() {
		return "{" + getTagSeq().toString() + ", " + getLogProb() + "}";
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof History) {
			return tagSeq.toList().equals(((History) obj).getTagSeq().toList());
		}
		return false;
	}
}
