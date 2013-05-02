package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.purepos.common.Globals;

import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

public class POSTaggerTest {

	@Test
	public void testPreprocessSentence() {
		String input = "A ház alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]$$0.1}} .";
		List<String> inputList = Arrays.asList(input.split("\\s+"));
		List<String> outList = POSTagger.preprocessSentence(inputList);

		Assert.assertEquals("ház", outList.get(1));
		Assert.assertEquals("alma", outList.get(2));

		Assert.assertEquals(true, Globals.analysisQueue.hasAnal(2));
		Assert.assertEquals(false, Globals.analysisQueue.hasAnal(1));
	}
}
