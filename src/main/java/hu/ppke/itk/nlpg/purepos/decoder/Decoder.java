package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;

/**
 * Viterbi algorithm for decoding.
 * 
 * @author György Orosz
 * 
 * @param <W>
 * @param <T>
 */
public abstract class Decoder<W, T extends Comparable<T>> implements
		IPOSTaggerDecoder<W, T> {
	/**
	 * Model which is learnt by the trainer.
	 */
	CompiledModel<W, T> model;

	public Decoder(CompiledModel<W, T> model) {
		this.model = model;
	}

}
