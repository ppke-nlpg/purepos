package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.ISpecTokenMatcher;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecTokenMatcher implements ISpecTokenMatcher {

	protected LinkedHashMap<String, Pattern> patterns = new LinkedHashMap<String, Pattern>();

	// TODO: read patterns from file
	/**
	 * Initialize patterns from HunPos
	 */
	public SpecTokenMatcher() {
		addPattern("@CARD", "^[0-9]*$");
		addPattern("@CARDPUNCT", "^[0-9]+\\.$");
		addPattern("@CARDSEPS", "^[0-9\\.,:-]+[0-9]+$");
		addPattern("@CARDSUFFIX", "^[0-9]+[a-zA-Z][a-zA-Z]?[a-zA-Z]?$");
		addPattern("@HTMLENTITY", "^&[^;]+;?$");
	}

	@Override
	public String matchLexicalElement(String token) {
		for (Entry<String, Pattern> pattern : patterns.entrySet()) {
			Matcher m = pattern.getValue().matcher(token);
			if (m.find())
				return pattern.getKey();
		}
		return null;
	}

	protected void addPattern(String name, String pattern) {
		patterns.put(name, Pattern.compile(pattern));
	}

}
