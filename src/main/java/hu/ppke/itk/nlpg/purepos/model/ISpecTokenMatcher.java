package hu.ppke.itk.nlpg.purepos.model;

/**
 * Implementors should implement a class to be able to match special tokens, and
 * return a lexical element
 * 
 * @author György Orosz
 * 
 */
public interface ISpecTokenMatcher {
	/**
	 * Check whether any part of the token match any pattern.
	 * 
	 * @param token
	 * @return pattern class if token matches, else null
	 */
	public String matchLexicalElement(String token);

}
