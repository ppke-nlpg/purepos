package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.ArrayList;

import junit.framework.TestCase;

import org.junit.Test;

public class NGramTest extends TestCase {

	@Test
	public void testNGram() {
		ArrayList<Integer> list = new ArrayList<Integer>();
		list.add(0);
		NGram<Integer> g = new NGram<Integer>(list, 2);
		list.add(1);
		assertNotSame(list.toString(), g.toString());

	}

	@Test
	public void testEqualsObject() {
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		NGram<Integer> g1 = new NGram<Integer>(list1, 2);
		NGram<Integer> g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.equals(g2));
		assertTrue(g2.equals(g1));
		list2.add(3);
		g2 = new NGram<Integer>(list2, 2);
		assertFalse(g1.equals(g2));
		assertFalse(g2.equals(g1));
	}

	@Test
	public void testCompareTo() {
		ArrayList<Integer> list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);

		ArrayList<Integer> list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(2);

		NGram<Integer> g1 = new NGram<Integer>(list1, 2);
		NGram<Integer> g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) < 0);

		list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(2);
		list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) > 0);

		list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) == 0);

		list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(0);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) > 0);

		list1 = new ArrayList<Integer>();
		list1.add(0);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) < 0);

		list1 = new ArrayList<Integer>();
		list1.add(2);
		list1.add(1);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(1);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertEquals(0, g1.compareTo(g2));

		list1 = new ArrayList<Integer>();
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) < 0);

		list1 = new ArrayList<Integer>();
		list1.add(1);
		list1.add(1);
		list2 = new ArrayList<Integer>();
		list2.add(1);
		g1 = new NGram<Integer>(list1, 2);
		g2 = new NGram<Integer>(list2, 2);
		assertTrue(g1.compareTo(g2) > 0);
	}
}
