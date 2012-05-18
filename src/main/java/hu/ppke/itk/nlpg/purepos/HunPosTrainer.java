package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.corpusreader.HunPosCorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

import java.io.File;
import java.io.FileNotFoundException;

@Deprecated
public class HunPosTrainer implements ITrainer {
	protected Statistics stat;
	protected IDocument document;

	public HunPosTrainer(File f) throws FileNotFoundException, ParsingException {
		document = readCorpus(f);
	}

	protected IDocument readCorpus(File file) throws FileNotFoundException,
			ParsingException {
		HunPosCorpusReader reader = new HunPosCorpusReader();
		return reader.readFromFile(file);

	}

	public HunPosTrainer(IDocument document) {
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
