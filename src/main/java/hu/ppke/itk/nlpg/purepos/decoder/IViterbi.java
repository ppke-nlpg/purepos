package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.model.Model;

/**
 * Viterbi algorithm for decoding.
 * 
 * @author György Orosz
 * 
 * @param <W>
 * @param <T>
 */
public abstract class IViterbi<W, T> implements IPOSTaggerDecoder<W, T> {
	/**
	 * Model which is learnt by the trainer.
	 */
	Model<W, T> model;

	public IViterbi(Model<W, T> model) {
		this.model = model;
	}

}
