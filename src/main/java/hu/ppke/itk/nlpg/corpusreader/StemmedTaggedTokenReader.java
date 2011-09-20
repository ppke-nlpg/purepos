package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

/**
 * Reader class for reading a stemmed, tagged token.
 * 
 * @author György Orosz
 * 
 */
public class StemmedTaggedTokenReader extends AbstractDocElementReader<IToken> {

	StemmedTaggedTokenReader() {
		separator = "_";
	}

	StemmedTaggedTokenReader(String sep) {
		this.separator = sep;
	}

	@Override
	public IToken read(String text) {

		int pos = text.indexOf(separator);
		if (pos < 0 || text.equals("_"))
			return null;
		String tag = text.substring(pos + 1).trim();
		String word = text.substring(0, pos).trim();

		IToken t = new Token(word, tag);
		return t;
	}
}