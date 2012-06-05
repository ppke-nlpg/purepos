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
package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.model.Model;

import java.io.Serializable;

/**
 * Storing statistic about the model.
 * 
 * @author György Orosz
 * 
 */
public class Statistics implements Serializable {

	private static final long serialVersionUID = -6981789925628984349L;

	int sentences;
	int tokens;
	int lGuesserItems;
	int uGuesserItems;
	double theta;

	public Statistics() {
		sentences = 0;
		tokens = 0;
		lGuesserItems = 0;
		uGuesserItems = 0;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public void incrementLowerGuesserItems(int num) {
		lGuesserItems += num;
	}

	public void incrementUpperGuesserItems(int num) {
		uGuesserItems += num;
	}

	public void incrementTokenCount() {
		tokens++;
	}

	public void incrementSentenceCount() {
		sentences++;
	}

	public String getStat(Model<?, ?> m) {
		String ret = "Training corpus:\n";
		ret += tokens + " tokens\n";
		ret += sentences + " sentences\n";
		ret += m.getTagVocabulary().size() + " different tag\n\n";

		ret += "Guesser trained with\n";
		ret += lGuesserItems + " lowercase\n";
		ret += uGuesserItems + " uppercase tokens\n";
		ret += "theta = " + theta;

		return ret;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Statistics) {
			Statistics other = (Statistics) o;
			return other.lGuesserItems == this.lGuesserItems
					&& other.sentences == this.sentences
					&& other.theta == this.theta && other.tokens == this.tokens
					&& other.uGuesserItems == this.uGuesserItems;
		}
		return false;
	}
}
