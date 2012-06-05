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

import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

class Node implements Comparable<Node> {

	// public Node(List<Integer> state, double weight) {
	// this.state = new NGram<Integer>(state);
	// this.weight = weight;
	// }

	public Node(NGram<Integer> state, double weight, Node previous) {
		this.state = state;
		this.weight = weight;
		this.prev = previous;
	}

	protected NGram<Integer> state;// = new ArrayList<Integer>();

	protected double weight = 0;

	protected Node prev;

	// public Node createNext(Integer newStep, double plusWeight) {
	// Node s = new Node(state.add(newStep), this.weight + plusWeight);
	//
	// return s;
	// }

	public NGram<Integer> getState() {
		return state;
	}

	public void setWeight(double w) {
		weight = w;
	}

	public void setPrevious(Node previous) {
		this.prev = previous;
	}

	public double getWeight() {
		return weight;
	}

	public Node getPrevious() {
		return prev;
	}

	@Override
	public String toString() {
		return "{state: " + state + " weight: " + weight + "}";
	}

	@Override
	public int compareTo(Node o) {
		return Double.compare(weight, o.weight);
	}
}
