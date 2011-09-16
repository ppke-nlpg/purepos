package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.Sentence;
import hu.ppke.itk.nlpg.docmodel.api.ISentence;
import hu.ppke.itk.nlpg.docmodel.api.IToken;

import java.util.ArrayList;
import java.util.List;

/**
 * Reader class for reading sentences from a corpus.
 * 
 * @author György Orosz
 * 
 */
public class SentenceReader extends AbstractDocElementReader<ISentence> {

	private final AbstractDocElementReader<IToken> wordParser;

	SentenceReader(AbstractDocElementReader<IToken> wordParser) {
		this.wordParser = wordParser;
		separator = "\\s";
	}

	@Override
	public ISentence read(String text) throws ParsingException {

		if (text.equals(""))
			return new Sentence(null);
		String[] words = text.split(separator);
		List<IToken> tokens = new ArrayList<IToken>();
		for (int i = 0; i < words.length; ++i) {
			String wordstring = words[i];
			if (wordstring.length() > 0) {
				IToken word = wordParser.read(wordstring);
				if (word != null)
					tokens.add(word);
			}
		}
		ISentence sent = new Sentence(tokens);
		return sent;
	}
}