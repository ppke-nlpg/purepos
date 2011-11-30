package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.internal.POSTaggerModel;

import java.io.File;
import java.io.FileNotFoundException;

public class Trainer implements ITrainer {
	protected IDocument document;

	public Trainer(File f) throws FileNotFoundException, ParsingException {
		document = readCorpus(f);
	}

	protected IDocument readCorpus(File file) throws FileNotFoundException,
			ParsingException {
		CorpusReader reader = new CorpusReader();
		return reader.readFromFile(file);

	}

	public Trainer(IDocument document) {
		this.document = document;
	}

	@Override
	public Model<String, Integer> trainModel(int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency) {
		return POSTaggerModel.train(document, tagOrder, emissionOrder,
				maxSuffixLength, rareFrequency);
	}

}
