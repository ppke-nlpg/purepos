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

import hu.ppke.itk.nlpg.purepos.model.IMapper;
import hu.ppke.itk.nlpg.purepos.model.IVocabulary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagMapper implements IMapper<Integer> {
	// LinkedHashMap<Pattern, String> mapping;
	List<TagMapping> tagMappings;
	private IVocabulary<String, Integer> vocabulary;

	public TagMapper(IVocabulary<String, Integer> tagVocabulary,
			List<TagMapping> tagMappings) {
		this.vocabulary = tagVocabulary;
		this.tagMappings = tagMappings;
		// init();
	}

	// private void init() {
	// add("^(.*)(MN|FN)(\\|lat)(.*)$", "$1FN$4");
	// }

	// private void add(String regexp, String replacement) {
	// Pattern p = Pattern.compile(regexp);
	// mapping.put(p, replacement);
	// }

	@Override
	public Integer map(Integer tag) {
		if (vocabulary.getMaximalIndex() < tag) {
			String tagStr = vocabulary.getWord(tag);
			for (TagMapping m : tagMappings) {
				Pattern p = m.getTagPattern();
				Matcher matcher = p.matcher(tagStr);
				if (matcher.matches()) {
					String replacement = m.getReplacement();
					String repTagStr = matcher.replaceAll(replacement);
					Integer retTag = vocabulary.getIndex(repTagStr);
					if (retTag != null)
						return retTag;
				}
			}
			return tag;
		} else
			return tag;

	}

	@Override
	public List<Integer> map(List<Integer> elements) {
		ArrayList<Integer> ret = new ArrayList<Integer>();
		for (Integer e : elements) {
			ret.add(map(e));
		}
		return ret;
	}

	@Override
	public Collection<Integer> filter(Collection<Integer> morphAnals,
			Collection<Integer> possibleTags) {
		List<Integer> ret = new LinkedList<Integer>();
		for (Integer anal : morphAnals) {
			Integer mappedTag = map(anal);
			if (possibleTags.contains(mappedTag)) {
				ret.add(anal);
			}
		}
		return ret;
	}

}
