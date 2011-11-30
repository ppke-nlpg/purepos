package hu.ppke.itk.nlpg.purepos.cli;

import hu.ppke.itk.nlpg.corpusreader.ParsingException;

import java.io.FileNotFoundException;

import org.junit.Before;
import org.junit.Test;

public class MSZNY2011DemoTest {
	MSZNY2011Demo demo;

	@Before
	public void init() {
		demo = new MSZNY2011Demo("./res/testCorpus.txt");
	}

	@Test
	public void readCorpusTest() throws FileNotFoundException, ParsingException {
		// IDocument d = dem
		// for (ISentence s : d.getSentences()) {
		// for (IToken t : s) {
		// System.out.println(t);
		// }
		// }
		// Assert.assertEquals(2, d.getSentences().size());
	}
	//
	// @Test
	// public void trainModelTest() throws FileNotFoundException,
	// ParsingException {
	// POSTaggerModel model = demo.trainModel(demo.readCorpus(demo.corpus));
	// // System.out.println(model.getAprioriTagProbs());
	// Assert.assertEquals(6, model.getAprioriTagProbs().size());
	// Assert.assertEquals(model.getAprioriTagProbs().get(1), model
	// .getAprioriTagProbs().get(3));
	// // System.out.println(model.getLowerCaseSuffixGuesser()
	// // .getTagLogProbabilities("alma"));
	// // ProbModel<String> m1 = (ProbModel<String>) model
	// // .getStandardEmissionModel();
	// // System.out.print(m1.getReprString());
	// // TODO: write meaningful test cases
	// // ProbModel<String> m2 = (ProbModel<String>) model
	// // .getSpecTokensEmissionModel();
	// // System.out.print(m2.getReprString());
	// //
	// // ProbModel<Integer> m3 = (ProbModel<Integer>) model
	// // .getTagTransitionModel();
	// // System.out.print(m3.getReprString());
	// }
	//
	// @Test
	// @Ignore
	// public void trainBigModel() throws FileNotFoundException,
	// ParsingException {
	// Date now = new Date();
	// demo = new MSZNY2011Demo("./res/szegedHumor.txt");
	// POSTaggerModel model = demo.trainModel(demo.readCorpus(demo.corpus));
	// Date now2 = new Date();
	// System.out.println(now2.getTime() - now.getTime());
	// }
}
