package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Document;
import hu.ppke.itk.nlpg.docmodel.internal.Paragraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Corpus reader class. An instance is used the read a corpus.
 * 
 * @author György Orosz
 * 
 */
public class CorpusReader extends AbstractDocElementReader<IDocument> {
	/**
	 * Object which is used for reading tokens inside the corpus.
	 */
	protected AbstractDocElementReader<IToken> tokenReader = new StemmedTaggedTokenReader(
			fileEncoding);

	CorpusReader(AbstractDocElementReader<IToken> tokenReader) {
		this.tokenReader = tokenReader;
	}

	@Override
	public IDocument read(String text) throws ParsingException {
		String[] sents = text.split(lineSeparator);
		List<ISentence> sentences = new ArrayList<ISentence>();
		AbstractDocElementReader<ISentence> sentenceParser = new SentenceReader(
				tokenReader);
		for (int i = 0; i < sents.length; ++i) {
			if (sents[i].length() > 0) {
				ISentence sentence = sentenceParser.read(sents[i]);
				sentences.add(sentence);
			}
		}
		IDocument doc = new Document(new Paragraph(sentences));
		return doc;
	}
}