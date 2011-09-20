/*******************************************************************************
 * Copyright (c) 2011 György Orosz, Attila Novák, Balázs Indig
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v2.1
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 * 
 * Contributors:
 *     György Orosz - initial API and implementation
 ******************************************************************************/
package hu.ppke.itk.nlpg.docmodel.internal;

import hu.ppke.itk.nlpg.docmodel.IDocElement;
import hu.ppke.itk.nlpg.docmodel.IDocElementContainer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Vector;

/**
 * Abstract class which is a container for storing document parts in a general
 * way.
 * 
 * @author György Orosz
 * 
 * @param <T>
 *            type of the stored docuemnt part
 */
abstract class AbstractDocElementContainer<T extends IDocElement> extends
		AbstractDocElement implements IDocElementContainer<T> {
	protected static final String NL = System.getProperty("line.separator");

	protected Vector<T> elements = new Vector<T>();

	protected AbstractDocElementContainer() {
	}

	public AbstractDocElementContainer(Iterable<T> elements) {
		init(elements);
	}

	protected void init(Iterable<T> elements) {
		if (elements == null) {
			this.elements = new Vector<T>();
			return;
		}
		for (T t : elements) {
			this.elements.add(t);
		}
	}

	@Override
	public int size() {
		return elements.size();
	}

	@Override
	public boolean isEmpty() {
		return elements.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return elements.contains(o);
	}

	@Override
	public Object[] toArray() {
		return elements.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return elements.toArray(a);
	}

	@Override
	public boolean add(T e) {
		return elements.add(e);
	}

	@Override
	public boolean remove(Object o) {
		return elements.remove(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return elements.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return elements.addAll(c);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return elements.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return elements.retainAll(c);
	}

	@Override
	public void clear() {
		elements.clear();
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return elements.addAll(index, c);
	}

	@Override
	public T get(int index) {
		return elements.get(index);
	}

	@Override
	public T set(int index, T element) {
		return elements.set(index, element);
	}

	@Override
	public void add(int index, T element) {
		elements.add(index, element);

	}

	@Override
	public T remove(int index) {
		return elements.remove(index);
	}

	@Override
	public int indexOf(Object o) {
		return elements.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return elements.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return elements.listIterator();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return elements.listIterator(index);
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return elements.subList(fromIndex, toIndex);
	}

	@Override
	public List<String> toList() {
		ArrayList<String> list = new ArrayList<String>();
		for (T t : this) {
			list.add(t.toString());
		}
		return list;

	}

	@Override
	public Iterator<T> iterator() {
		return elements.iterator();
	}

}
