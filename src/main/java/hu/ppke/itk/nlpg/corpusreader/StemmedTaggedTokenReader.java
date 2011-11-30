package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

/**
 * Reader class for reading a tagged, unstemmed token.
 * 
 * @author György Orosz
 * 
 */
public class StemmedTaggedTokenReader extends AbstractDocElementReader<IToken> {

	public StemmedTaggedTokenReader() {
		separator = "#";
	}

	public StemmedTaggedTokenReader(String separator) {
		this.separator = separator;
	}

	@Override
	public IToken read(String text) {
		try {
			String[] wordParts = text.split(separator);
			if (wordParts.length == 0)
				return null;
			IToken word = new Token(wordParts[0],
					wordParts[1].replace('_', ' '), wordParts[2]);
			return word;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}