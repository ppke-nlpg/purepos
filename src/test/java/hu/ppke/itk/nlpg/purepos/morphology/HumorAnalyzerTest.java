package hu.ppke.itk.nlpg.purepos.morphology;

import hu.ppke.itp.nlpg.purepos.morphology.HumorAnalyzer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class HumorAnalyzerTest {

	HumorAnalyzer humor;

	@Before
	public void init() {
		humor = HumorAnalyzer.getInstance();
	}

	@Test
	public void testHumor() {
		Assert.assertEquals(2, humor.analyze("alma").size());
		// for (IToken t : humor.analyze("alma"))
		// System.out.println(t);
		System.out.println(humor.analyze("A"));
		System.out.println(humor.analyze("Az"));
	}
}
