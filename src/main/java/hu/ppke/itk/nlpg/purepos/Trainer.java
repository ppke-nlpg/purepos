package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.corpusreader.ICorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.internal.POSTaggerModel;

import java.io.File;
import java.io.FileNotFoundException;

public class Trainer implements ITrainer {
	protected Statistics stat;
	protected IDocument document;
	protected ICorpusReader<IDocument> reader;

	public Trainer(File f, ICorpusReader<IDocument> reader)
			throws FileNotFoundException, ParsingException {
		this.reader = reader;
		document = readCorpus(f);
	}

	protected IDocument readCorpus(File file) throws FileNotFoundException,
			ParsingException {
		return reader.readFromFile(file);

	}

	public Trainer(IDocument document) {
		this.document = document;
	}

	@Override
	public Statistics getStat() {
		return stat;
	}

	@Override
	public Model<String, Integer> trainModel(int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency) {
		Model<String, Integer> m = POSTaggerModel.train(document, tagOrder,
				emissionOrder, maxSuffixLength, rareFrequency);
		stat = POSTaggerModel.getLastStat();
		return m;
	}

}
