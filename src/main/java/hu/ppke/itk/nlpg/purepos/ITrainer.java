package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.purepos.common.Statistics;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;

public interface ITrainer {

	public RawModel trainModel(int tagOrder, int emissionOrder,
			int maxSuffixLength, int rareFrequency);

	public Statistics getStat();

}
