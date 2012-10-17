package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.Scanner;

import junit.framework.Assert;

import org.junit.Test;

public class TaggedSequenceReaderTest {

	@Test
	public void readTest() {
		String s = "alma{{alma[FN][NOM]||alom[FN][NOM][FN][PSe3][NOM]}} facebookozik{{facebookozik[IGE][e3]}}"
				+ "\n"
				+ "vár{{vár[FN][NOM]||vár[FN][NOM][IGE][e3]}} pázmányos{{pázmányos[FN][NOM]||pázmányos[MN][NOM]}} asdf{{||}}";
		TaggedSequenceReader tsr = new TaggedSequenceReader(new Scanner(s));
		Scanner sc = tsr.getScanner();
		Assert.assertEquals(true, sc.hasNext());
		Assert.assertEquals("alma facebookozik", sc.nextLine());
		Assert.assertEquals(true, sc.hasNext());
		Assert.assertEquals("vár pázmányos asdf", sc.nextLine());
		Assert.assertEquals(false, sc.hasNext());

		IMorphologicalAnalyzer ma = tsr.getMorphologicalAnalyzer();

		Assert.assertEquals(2, ma.analyze("alma").size());
		Assert.assertEquals(2, ma.getTags("alma").size());

		Assert.assertEquals(1, ma.getTags("facebookozik").size());
		Assert.assertEquals("facebookozik", ma.analyze("facebookozik").get(0)
				.getStem());
		Assert.assertEquals(null, ma.analyze("körte"));
		Assert.assertEquals(null, ma.getTags("körte"));
	}
}
