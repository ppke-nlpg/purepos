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

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

/**
 * Implementors should implement method for decoding hidden states for the
 * observations.
 * 
 * @author György Orosz
 * 
 */
public interface IPOSTaggerDecoder<W, T extends Comparable<T>> {
	/**
	 * Finds corresponding best tags for observations
	 * 
	 * @param observations
	 * @return tags
	 */
	List<T> decode(List<W> observations);

	/**
	 * Finds corresponding n-best tags for observations
	 * 
	 * @param observations
	 * @param maxResultsNumber
	 *            maximum number of possible decoding sequences
	 * @return tag sequence and its score
	 */
	List<Pair<List<T>, Double>> decode(List<W> observations,
			int maxResultsNumber);

}
