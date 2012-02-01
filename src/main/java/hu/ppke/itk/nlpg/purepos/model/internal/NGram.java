package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.model.INGram;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;

/**
 * Standard implementation of the INGram interface, which uses ArrayList for
 * storing tags
 * 
 * @author György Orosz
 * 
 * @param <T>
 */
public class NGram<T extends Comparable<T>> implements INGram<T> {

	protected List<T> tokenList;
	protected final int compareLength;

	/**
	 * Constructor
	 * 
	 * @param tokens
	 *            The n-gram elements
	 * @param compareLength
	 *            the size of the compare window (used to compare two elements
	 *            starting from the end)
	 */
	public NGram(List<T> tokens, int compareLength) {
		this.tokenList = new ArrayList<T>(tokens);
		this.compareLength = compareLength;
	}

	public NGram(List<T> tokens) {
		this.tokenList = new ArrayList<T>(tokens);
		this.compareLength = -1;
	}

	@Override
	public String toString() {
		return tokenList.toString();
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof NGram) {
			return this.compareTo((NGram<T>) obj) == 0;
		}
		return false;
	}

	@Override
	public int compareTo(NGram<T> other) {
		ListIterator<T> thisIterator = tokenList.listIterator(tokenList.size());
		ListIterator<T> otherIterator = other.tokenList
				.listIterator(other.tokenList.size());
		int size;
		if (compareLength != -1) {
			size = compareLength;
		} else {
			size = Integer.MAX_VALUE;
		}

		for (int counter = 0; counter < size; ++counter) {
			if (thisIterator.hasPrevious() && otherIterator.hasPrevious()) {
				T thisElement = thisIterator.previous();
				T otherElement = otherIterator.previous();
				int r = thisElement.compareTo(otherElement);
				if (r != 0)
					return r;
			} else if (thisIterator.hasPrevious()
					&& !otherIterator.hasPrevious()) {
				return 1;
			} else if (!thisIterator.hasPrevious()
					&& otherIterator.hasPrevious()) {
				return -1;
			} else if (!thisIterator.hasPrevious()
					&& !otherIterator.hasPrevious()) {
				return 0;
			}
		}
		return 0;
	}

	@Override
	public List<T> toList() {
		return new ArrayList<T>(tokenList);
	}
}
