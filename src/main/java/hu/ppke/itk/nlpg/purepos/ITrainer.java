package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.purepos.model.Model;

public interface ITrainer {

	public Model<String, Integer> trainModel(int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency);

}
