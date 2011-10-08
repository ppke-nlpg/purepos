package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.INGram;

import java.util.ArrayList;
import java.util.List;

/**
 * Standard implementation of the INGram interface, which uses ArrayList for
 * storing tags
 * 
 * @author György Orosz
 * 
 * @param <T>
 */
@Deprecated
public class NGram<T> implements INGram<T> {

	protected List<T> tokenList;

	public NGram(List<T> tokens) {
		this.tokenList = new ArrayList<T>(tokens);
	}

	@Override
	public String toString() {
		return tokenList.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NGram) {
			@SuppressWarnings("unchecked")
			NGram<T> ng = (NGram<T>) obj;
			return ng.tokenList.equals(this.tokenList);
		}
		return false;
	}
}
