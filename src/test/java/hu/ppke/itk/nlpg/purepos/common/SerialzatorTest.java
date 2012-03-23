package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.corpusreader.CorpusReader;
import hu.ppke.itk.nlpg.corpusreader.ParsingException;
import hu.ppke.itk.nlpg.docmodel.IDocument;
import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.internal.POSTaggerModel;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

public class SerialzatorTest {
	@Test
	public void readWriteTest() throws ParsingException, IOException,
			ClassNotFoundException {
		CorpusReader r = new CorpusReader();
		IDocument d = r
				.read("Michael#Michael#[FN][NOM] Karaman#??Karaman#[FN][NOM] ,#,#[PUNCT] az#az#[DET] Ann#Ann#[FN][NOM] 1#1#[SZN][NOM]");
		Model<String, Integer> model = POSTaggerModel.train(d, 3, 3, 10, 10);
		String pathname = "./_test.model";
		Serializator.writeModel(model, new File(pathname));
		Model<String, Integer> readModel = Serializator.readModel(new File(
				pathname));
		// TODO: write equality test case, now it is enough that it doesn't fail
	}
}
