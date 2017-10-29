package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Ignore;
import org.junit.Test;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemmaTransformationTestVectorReader.LemmaTransformationTestVector;

import java.io.FileNotFoundException;

public class GeneralizedLemmaTransformationTest {

	@Test
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
		try {
			LemmaTransformationTestVector testVector = LemmaTransformationTestVectorReader.read(
					".\\src\\test\\java\\hu\\ppke\\itk\\nlpg\\purepos\\common\\lemma\\GeneralizedLemmaTransformation_TestVector.txt");
			int threshold = 2; // 2 is the default value, if you change it, the expected values must be recalculated
			System.out.println("GeneralziedLemmaTransformation test. Used threshold: "+ GeneralizedLemmaTransformation.threshold);
			for (int i = 0; i < testVector.token.length; i++) {

				System.out.println("Test Case "+(i+1)+"#\n"+"token: "+testVector.token[i]+"\nlemma: "+testVector.lemma[i]
						+"\ncode: "+testVector.expectedCode[i]+"\n");
				// creating the class
				GeneralizedLemmaTransformation t = new GeneralizedLemmaTransformation(testVector.token[i], testVector.lemma[i], 1,threshold);
				// testing the decode function
				Assert.assertEquals(testVector.expectedLemmaStuff[i], t.representation.getLeft());
				Assert.assertEquals(testVector.expectedCode[i], t.representation.getRight());
				// testing the encode function
				Pair<String, Integer> analyzed = t.analyze(testVector.token[i]);
				Assert.assertEquals(testVector.lemma[i], analyzed.getLeft());
				Assert.assertEquals(new Integer(1), analyzed.getRight());
			}
			;
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}
}
