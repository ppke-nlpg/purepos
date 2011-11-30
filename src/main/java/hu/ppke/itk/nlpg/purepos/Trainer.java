package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.model.Model;

public class Trainer implements Runnable {
	protected IDocument document;

	Trainer(IDocument document) {
		this.document = document;
	}

	protected Model<String, Integer> trainModel(IDocument document,
			int tagOrder, int emissionOrder, int maxSuffixLength,
			int rareFrequency) {
		// TODO: implement
		return null;
	}

	@Override
	public void run() {

	}

}
