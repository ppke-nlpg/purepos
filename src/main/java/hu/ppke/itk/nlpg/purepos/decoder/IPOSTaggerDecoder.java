package hu.ppke.itk.nlpg.purepos.decoder;

import java.util.List;

/**
 * Implementors should implement method for decoding hidden states for the
 * observations.
 * 
 * @author György Orosz
 * 
 */
public interface IPOSTaggerDecoder<W, T extends Comparable<T>> {
	/**
	 * Finds corresponding tags for observations
	 * 
	 * @param observations
	 * @return tags
	 */
	List<T> decode(List<W> observations);

}
