package hu.ppke.itk.nlpg.purepos.model;

import hu.ppke.itk.nlpg.purepos.model.internal.IntVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

import java.util.ArrayList;

import junit.framework.TestCase;

public class IntVocabularyTest extends TestCase {

	public void testAddElement() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(0, v.size());
		v.addElement("alma");
		assertEquals(1, v.size());

	}

	public void testSize() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(0, v.size());
		v.addElement("alma");
		assertEquals(1, v.size());
	}

	public void testGetIndex() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(null, v.getIndex("alma"));
		v.addElement("alma");
		assertEquals(new Integer(1), v.getIndex("alma"));
	}

	public void testGetWord() {
		IntVocabulary<String> v = new IntVocabulary<String>();
		assertEquals(null, v.getWord(0));
		assertEquals(null, v.getWord(1));
		v.addElement("alma");
		assertEquals("alma", v.getWord(1));
	}

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
		idcs.add(1);
		idcs.add(2);
		assertEquals(v.getIndeces(strs), new NGram<Integer>(idcs));
	}
}
