package hu.ppke.itk.nlpg.purepos.common;

import java.util.Map;
import java.util.regex.Pattern;

import junit.framework.Assert;

import org.junit.Test;

public class AnalysisQueueTest {

	@Test
	public void regeExpTest() {
		Pattern stringRE = Pattern.compile(AnalysisQueue.stringPat);
		Assert.assertEquals(true, stringRE.matcher("öüóőúáűí").matches());
		Assert.assertEquals(true, stringRE.matcher("19-e").matches());
		Assert.assertEquals(true, stringRE.matcher("2000.").matches());
		Assert.assertEquals(true, stringRE.matcher(".").matches());
		Assert.assertEquals(true, stringRE.matcher("-").matches());
		Assert.assertEquals(true, stringRE.matcher("?").matches());
		Assert.assertEquals(true, stringRE.matcher("–").matches());
		Assert.assertEquals(true, stringRE.matcher("―").matches());
		Assert.assertEquals(true, stringRE.matcher("…").matches());
		Assert.assertEquals(true, stringRE.matcher("(").matches());

		Pattern analPat = Pattern.compile(AnalysisQueue.analPat);
		// System.out.println(analPat.pattern());
		Assert.assertEquals(true, analPat.matcher("alma[FN][NOM]").matches());
		Assert.assertEquals(true, analPat.matcher("alma[FN][NOM]$$0.9")
				.matches());
		Assert.assertEquals(true, analPat.matcher("alma[FN][NOM]$$12")
				.matches());
		Assert.assertEquals(true, analPat.matcher("alma[FN][NOM]$$12.1341234")
				.matches());

		Pattern analsPat = AnalysisQueue.analFormPat;
		Assert.assertEquals(true, analsPat.matcher("alma{{alma[FN][NOM]}}")
				.matches());
		Assert.assertEquals(true,
				analsPat.matcher("alma{{alma[FN][NOM]||alom[FN][Pse3]}}")
						.matches());
		Assert.assertEquals(
				true,
				analsPat.matcher(
						"alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]$$0.1}}")
						.matches());
		// Ez sajnos hibás, de regexppel nem kezelhető
		Assert.assertEquals(true,
				analsPat.matcher("alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]}}")
						.matches());
	}

	@Test
	public void preanalTest() {
		// AnalysisQueue aq = new AnalysisQueue();
		Assert.assertEquals(
				true,
				AnalysisQueue
						.isPreanalysed("alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]$$0.1}}"));
		Assert.assertEquals(true, AnalysisQueue
				.isPreanalysed("alma{{alma[FN][NOM]||alom[FN][Pse3]}}"));
		Assert.assertEquals(true,
				AnalysisQueue.isPreanalysed("alma{{alma[FN][NOM]}}"));
		Assert.assertEquals(false, AnalysisQueue.isPreanalysed("alma"));
	}

	@Test
	public void cleanTest() {
		Assert.assertEquals("alma", AnalysisQueue
				.clean("alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]$$0.1}}"));
		Assert.assertEquals("alma",
				AnalysisQueue.clean("alma{{alma[FN][NOM]||alom[FN][Pse3]}}"));
		Assert.assertEquals("alma",
				AnalysisQueue.clean("alma{{alma[FN][NOM]}}"));
	}

	@Test
	public void unitTest() {
		AnalysisQueue aq = new AnalysisQueue();
		aq.init(5);

		aq.addWord("alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]$$0.1}}", 1);
		Assert.assertEquals(2, aq.anals.get(1).keySet().size());
		Map<String, Double> anals = aq.getAnals(1);
		Assert.assertEquals(true, anals.containsKey("alma[FN][NOM]"));
		Assert.assertEquals(true, anals.containsKey("alom[FN][Pse3]"));

		Assert.assertEquals(0.9, anals.get("alma[FN][NOM]"));
		Assert.assertEquals(0.1, anals.get("alom[FN][Pse3]"));

		Assert.assertEquals(false, aq.hasAnal(0));
		Assert.assertEquals(false, aq.hasAnal(8));
		Assert.assertEquals(true, aq.hasAnal(1));

		Assert.assertEquals(false, aq.useProbabilties(8));
		Assert.assertEquals(false, aq.useProbabilties(0));
		Assert.assertEquals(true, aq.useProbabilties(1));

		aq.addWord("alma{{alma[FN][NOM]||alom[FN][Pse3]}}", 0);
		Assert.assertEquals(2, aq.anals.get(0).keySet().size());
		anals = aq.getAnals(0);
		Assert.assertEquals(true, anals.containsKey("alma[FN][NOM]"));
		Assert.assertEquals(true, anals.containsKey("alom[FN][Pse3]"));

		Assert.assertEquals(true, aq.hasAnal(0));
		Assert.assertEquals(false, aq.hasAnal(8));
		Assert.assertEquals(true, aq.hasAnal(1));

		Assert.assertEquals(false, aq.useProbabilties(8));
		Assert.assertEquals(false, aq.useProbabilties(0));
		Assert.assertEquals(true, aq.useProbabilties(1));

		Assert.assertEquals("alom", AnalysisQueue.anal2lemma("alom[FN][Pse3]"));
		Assert.assertEquals("[FN][Pse3]",
				AnalysisQueue.anal2tag("alom[FN][Pse3]"));

	}
}
