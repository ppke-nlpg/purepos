package hu.ppke.itk.nlpg.corpusreader;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.docmodel.ISentence;
import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Document;
import hu.ppke.itk.nlpg.docmodel.internal.Paragraph;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for reading corpuses, which is the output of the HunPos tagger.
 * 
 * @author György Orosz
 * 
 */
public class HunPosCorpusReader extends AbstractDocElementReader<IDocument> {
	private final AbstractDocElementReader<IToken> wordParser;

	public HunPosCorpusReader() {
		this(new TaggedTokenReader("\t"));
		this.fileEncoding = "ISO-8859-2";
	}

	protected HunPosCorpusReader(AbstractDocElementReader<IToken> wordParser) {
		this.wordParser = wordParser;
	}

	@Override
	public IDocument read(String text) throws ParsingException {
		String[] sents = text.split(lineSeparator + lineSeparator);
		List<ISentence> sentences = new ArrayList<ISentence>();
		AbstractDocElementReader<ISentence> sentenceParser = new SentenceReader(
				wordParser);
		sentenceParser.separator = lineSeparator;
		for (int i = 0; i < sents.length; ++i) {
			if (sents[i].length() - 1 > 0) {
				ISentence sentence = sentenceParser.read(sents[i]);
				sentences.add(sentence);
			}
		}
		IDocument doc = new Document(new Paragraph(sentences));
		return doc;
	}
}