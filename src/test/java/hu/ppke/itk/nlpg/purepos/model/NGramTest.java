package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

import java.util.ArrayList;

import junit.framework.TestCase;

public class NGramTest extends TestCase {

	public void testNGram() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		NGram<Integer> g = new NGram<Integer>(list);
		list.add(1);
		assertNotSame(list.toString(), g.toString());

	}

	public void testEqualsObject() {
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		NGram<Integer> g1 = new NGram<Integer>(list1);
		NGram<Integer> g2 = new NGram<Integer>(list2);
		assertTrue(g1.equals(g2));
		assertTrue(g2.equals(g1));
		list2.add(3);
		g2 = new NGram<Integer>(list2);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
	}
}
