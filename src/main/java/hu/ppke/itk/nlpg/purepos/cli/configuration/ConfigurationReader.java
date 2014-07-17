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
package hu.ppke.itk.nlpg.purepos.cli.configuration;

import hu.ppke.itk.nlpg.purepos.model.internal.StringMapping;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.tree.ConfigurationNode;

public class ConfigurationReader {
	private static final String TAG = "to";
	private static final String PATTERN = "pattern";
	private static final String TAG_MAPPING = "tag_mapping";
	private static final String LEMMA_MAPPING = "lemma_mapping";

	public Configuration read(File f) throws ConfigurationException {
		XMLConfiguration xconf = new XMLConfiguration(f);
		
		List<ConfigurationNode> tagMappings = xconf.getRootNode().getChildren(
				TAG_MAPPING);
		LinkedList<StringMapping> tag_ret = new LinkedList<StringMapping>();
		for (ConfigurationNode m : tagMappings) {
			String spat = (String) m.getAttributes(PATTERN).get(0).getValue();
			String stag = (String) m.getAttributes(TAG).get(0).getValue();
			tag_ret.add(new StringMapping(spat, stag));
		}
		
		List<ConfigurationNode> lemmaMappings = xconf.getRootNode().getChildren(
				LEMMA_MAPPING);
		LinkedList<StringMapping> lemma_ret = new LinkedList<StringMapping>();
		for (ConfigurationNode m : lemmaMappings) {
			String spat = (String) m.getAttributes(PATTERN).get(0).getValue();
			String stag = (String) m.getAttributes(TAG).get(0).getValue();
			lemma_ret.add(new StringMapping(spat, stag));
		}


		return new Configuration(tag_ret, lemma_ret);
	}
}
