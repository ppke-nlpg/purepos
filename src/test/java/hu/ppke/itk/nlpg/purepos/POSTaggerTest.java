package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.purepos.common.AnalysisQueue;
import hu.ppke.itk.nlpg.purepos.common.TAnalysisItem;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

public class POSTaggerTest {

	@Test
	public void testPreprocessSentence() {
		String input = "A ház alma{{alma[FN][NOM]$$0.9||alom[FN][Pse3]$$0.1}} .";
		List<String> inputList = Arrays.asList(input.split("\\s+"));
		AnalysisQueue analysisQueue = new AnalysisQueue();
		List<String> outList = POSTagger.preprocessSentence(inputList, analysisQueue);

		Assert.assertEquals("ház", outList.get(1));
		Assert.assertEquals("alma", outList.get(2));

		Assert.assertEquals(true, analysisQueue.hasAnal(2));
		Assert.assertEquals(false, analysisQueue.hasAnal(1));
	}

	@Test
	public void testPreprocessSentenceEx() {
		ArrayList<Pair<String, ArrayList<TAnalysisItem>>> inputEx;
		inputEx = new ArrayList<Pair<String, ArrayList<TAnalysisItem>>>();
		ArrayList<TAnalysisItem> a;
		a = new ArrayList<TAnalysisItem>();
		inputEx.add(Pair.of("A",a));
		a = new ArrayList<TAnalysisItem>();
		inputEx.add(Pair.of("ház",a));
		a = new ArrayList<TAnalysisItem>();
		a.add(new TAnalysisItem("alma","[FN][NOM]",0.9));
		a.add(new TAnalysisItem("alom","[FN][Pse3]",0.1));
		inputEx.add(Pair.of("alma",a));
		a = new ArrayList<TAnalysisItem>();
		inputEx.add(Pair.of(".",a));
		AnalysisQueue analysisQueue = new AnalysisQueue();
		List<String> outList = POSTagger.preprocessSentenceEx(inputEx, analysisQueue);

		Assert.assertEquals("ház", outList.get(1));
		Assert.assertEquals("alma", outList.get(2));

		Assert.assertEquals(true, analysisQueue.hasAnal(2));
		Assert.assertEquals(false, analysisQueue.hasAnal(1));
	}
}
