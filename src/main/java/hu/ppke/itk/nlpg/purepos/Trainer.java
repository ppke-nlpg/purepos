package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.corpusreader.ICorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Trainer implements ITrainer {
	protected Statistics stat;
	protected IDocument document;
	protected ICorpusReader<IDocument> reader;

	public Trainer(File f, ICorpusReader<IDocument> reader)
			throws FileNotFoundException, ParsingException {
		this.reader = reader;
		document = readCorpus(f);
	}

	public Trainer(Scanner sc, ICorpusReader<IDocument> reader)
			throws ParsingException {
		this.reader = reader;
		this.document = readCorpus(sc);
	}

	protected IDocument readCorpus(File file) throws FileNotFoundException,
			ParsingException {
		return reader.readFromFile(file);

	}

	protected IDocument readCorpus(Scanner sc) throws ParsingException {
		return this.reader.readFromScanner(sc);

	}

	public Trainer(IDocument document) {
		this.document = document;
	}

	@Override
	public Statistics getStat() {
		return stat;
	}

	@Override
	public RawModel trainModel(int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency) {
		RawModel m = new RawModel(tagOrder, emissionOrder, maxSuffixLength,
				rareFrequency);
		m.train(document);
		stat = m.getLastStat();
		return m;
	}

}
