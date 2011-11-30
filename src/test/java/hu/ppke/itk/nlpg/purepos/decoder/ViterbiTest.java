package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.cli.MSZNY2011Demo;

import org.junit.Before;
import org.junit.Test;

public class ViterbiTest {

	MSZNY2011Demo demo;

	@Before
	public void init() {
		demo = new MSZNY2011Demo("./res/testCorpus.txt");
	}

	@Test
	public void test() {

	}

}
