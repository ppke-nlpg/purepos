package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import hu.ppke.itk.nlpg.purepos.model.IMapper;

public class StringMapper implements IMapper<String> {
	
	
	protected List<StringMapping> mappings;
	
	public StringMapper(List<StringMapping> mappings) {
		this.mappings = mappings;
	}
	
	@Override
	public String map(String element) {
		for (StringMapping m : mappings) {
			Pattern p = m.getTagPattern();
			Matcher matcher = p.matcher(element);
			if (matcher.find()) {
				String replacement = m.getReplacement();
				String repStr = matcher.replaceAll(replacement);
				return repStr;
			}
		}
		return element;
	}

	@Override
	public List<String> map(List<String> elements) {
		ArrayList<String> ret = new ArrayList<String>();
		for (String e : elements) {
			ret.add(map(e));
		}
		return ret;
	}



}
