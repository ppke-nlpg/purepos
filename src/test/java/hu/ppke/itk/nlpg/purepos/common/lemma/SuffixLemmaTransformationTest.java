package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;
import hu.ppke.itk.nlpg.purepos.common.lemma.LemmaTransformationTestVectorReader.LemmaTransformationTestVector;
import java.io.FileNotFoundException;


public class SuffixLemmaTransformationTest {
	@Test
	public void testCoding() {

		System.out.println("SuffixLemmaTransformation test");

		try {
			LemmaTransformationTestVector testVector = LemmaTransformationTestVectorReader.read(
					".\\src\\test\\java\\hu\\ppke\\itk\\nlpg\\purepos\\common\\lemma\\SuffixLemmaTransformation_TestVector.txt");

			for (int i = 0; i < testVector.token.length; i++) {
				System.out.println("Test Case "+(i+1)+"#\n"+"token: "+testVector.token[i]+"\nlemma: "+testVector.lemma[i]
						+"\ncode: "+testVector.expectedCode[i]+"\n");
				// creating the class
				SuffixLemmaTransformation t = new SuffixLemmaTransformation(testVector.token[i], testVector.lemma[i], testVector.expectedTag[i]);
				// testing the decode function
				Assert.assertEquals(testVector.expectedLemmaStuff[i], t.representation.getLeft());
				Assert.assertEquals(testVector.expectedCode[i], t.representation.getRight());
				// testing the encode function
				Pair<String, Integer> analyzed = t.analyze(testVector.token[i]);
				Assert.assertEquals(testVector.lemma[i], analyzed.getLeft());
				Assert.assertEquals(testVector.expectedTag[i], analyzed.getRight());
			}
			;
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}
	}

//	@Test
//	public void conversionTest() {
//		SuffixLemmaTransformation t = new SuffixLemmaTransformation("alma",
//				"alom", 1);
//		Assert.assertEquals("alma", t.postprocess("alma"));
//		Assert.assertEquals("alma", t.postprocess("alma-"));
//		Assert.assertEquals("-", t.postprocess("-"));
//	}

}
