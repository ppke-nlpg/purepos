package hu.ppke.itk.nlpg.purepos.model.internal;

import java.util.ArrayList;

import junit.framework.Assert;
import junit.framework.TestCase;

import org.junit.Test;

public class IntVocabularyTest extends TestCase {

	@Test
	public void testAddElement() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(0, v.size());
		Integer key = v.addElement("alma");
		assertEquals(1, v.size());
		Assert.assertEquals(key, v.addElement("alma"));
		assertEquals(1, v.size());

	}

	// @Test
	// public void testSize() {
	// IntVocabulary<String> v = new IntVocabulary<String>();
	// assertEquals(0, v.size());
	// v.addElement("alma");
	// assertEquals(1, v.size());
	// }

	@Test
	public void testGetIndex() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(null, v.getIndex("alma"));
		v.addElement("alma");
		assertEquals(new Integer(0), v.getIndex("alma"));
	}

	@Test
	public void testGetWord() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(null, v.getWord(0));
		assertEquals(null, v.getWord(1));
		v.addElement("alma");
		assertEquals("alma", v.getWord(0));
	}

	@Test
	public void testGetIndeces() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		ArrayList<String> strs = new ArrayList<String>();
		strs.add("alma");
		strs.add("körte");
		assertEquals(null, v.getIndeces(strs));
		v.addElement("alma");
		assertEquals(null, v.getIndeces(strs));
		v.addElement("körte");
		ArrayList<Integer> idcs = new ArrayList<Integer>();
		idcs.add(0);
		idcs.add(1);
		assertEquals(v.getIndeces(strs), new NGram<Integer>(idcs));
	}
}
