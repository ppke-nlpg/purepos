package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

/**
 * Reader class for reading an unstemmed untagged token.
 * 
 * @author György Orosz
 * 
 */
public class SimpleTokenReader extends AbstractDocElementReader<IToken> {
	@Override
	public IToken read(String text) {
		IToken word = new Token(text);
		return word;
	}
}