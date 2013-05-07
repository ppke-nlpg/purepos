package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;

public class GeneralizedLemmaTransformationTest {

	@Test
	@Ignore
	public void substringTest() {
		Assert.assertEquals(Pair.of(0, 2),
				GeneralizedLemmaTransformation.longestSubstring("alma", "alom"));

		Assert.assertEquals(Pair.of(0, 7), GeneralizedLemmaTransformation
				.longestSubstring("kőszívűbb", "kőszívű"));

		Assert.assertEquals(Pair.of(3, 1), GeneralizedLemmaTransformation
				.longestSubstring("legjobb", "jó"));

		Assert.assertEquals(Pair.of(3, 4), GeneralizedLemmaTransformation
				.longestSubstring("legokosabb", "okos"));

		Assert.assertEquals(Pair.of(1, 1), GeneralizedLemmaTransformation
				.longestSubstring("megesz", "enni"));

	}

	@Test
	public void reverseTest() {
		Assert.assertEquals(Pair.of("alom", 1),
				new GeneralizedLemmaTransformation("alma", "alom", 1)
						.analyze("alma"));

		String word = "katonát", lemma = "katona";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "legjobb";
		lemma = "jó";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "Alma";
		lemma = "alom";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "szebb";
		lemma = "szép";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "legszebb";
		lemma = "szép";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "leköt";
		lemma = "köt";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "övé";
		lemma = "ő";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "övé";
		lemma = "ő";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "értem";
		lemma = "én";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "enyém";
		lemma = "én";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "áztam";
		lemma = "ázik";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "késem";
		lemma = "késik";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "hozzá";
		lemma = "ő";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));

		word = "<,";
		lemma = ",";
		Assert.assertEquals(Pair.of(lemma, 1),
				new GeneralizedLemmaTransformation(word, lemma, 1)
						.analyze(word));
	}

}
