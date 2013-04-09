package hu.ppke.itk.nlpg.purepos.cli.configuration;

import hu.ppke.itk.nlpg.purepos.model.internal.IntVocabulary;
import hu.ppke.itk.nlpg.purepos.model.internal.TagMapper;
import hu.ppke.itk.nlpg.purepos.model.internal.TagMapping;
import hu.ppke.itk.nlpg.purepos.model.internal.Vocabulary;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

public class ConfigurationTest {

	ConfigurationReader reader = new ConfigurationReader();

	protected File createFile() throws IOException {
		String out = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>"
				+ "<config>\n"
				+ "<mapping pattern=\"(\\[)(alma)(\\])\" to=\"$1körte$2\" />"
				+ "<mapping pattern=\"^(.*)(MN|FN)(\\|lat)(.*)$\" to=\"$1FN$4\" />"
				+ "</config>";
		File f = File.createTempFile("config_test_", ".xml");
		PrintStream ps = new PrintStream(f);
		ps.print(out);
		ps.close();
		return f;

	}

	protected void removeFile(File f) {
		f.delete();

	}

	@Test
	public void readTest() throws IOException, ConfigurationException {
		File f = createFile();
		Configuration conf = reader.read(f);
		List<TagMapping> ms = conf.getTagMappings();
		Assert.assertEquals(2, ms.size());
		Assert.assertEquals(ms.get(0).getTagPattern().pattern(),
				"(\\[)(alma)(\\])");
		Assert.assertEquals(ms.get(0).getReplacement().toString(), "$1körte$2");
		Assert.assertEquals(ms.get(1).getTagPattern().pattern(),
				"^(.*)(MN|FN)(\\|lat)(.*)$");
		Assert.assertEquals(ms.get(1).getReplacement().toString(), "$1FN$4");

		// Full test

		Vocabulary<String, Integer> vocabulary = new IntVocabulary<String>();
		Integer fn = vocabulary.addElement("[FN][NOM]");
		Integer mn = vocabulary.addElement("[MN][NOM]");
		Integer fnlat = vocabulary.addElement("[FN|lat][NOM]");
		Integer mnlat = vocabulary.addElement("[MN|lat][NOM]");
		Integer ige = vocabulary.addElement("[IGE][Me3]");

		TagMapper mapper = new TagMapper(vocabulary, ms);
		Assert.assertEquals(fn, mapper.map(fnlat));
		Assert.assertEquals(fn, mapper.map(fn));
		Assert.assertEquals(ige, mapper.map(ige));
		Assert.assertEquals(fn, mapper.map(fn));
		Assert.assertEquals(mn, mapper.map(mn));
		List<Integer> from = Arrays.asList(fn, mnlat, fnlat);
		List<Integer> to = Arrays.asList(fn, fn, fn);
		Assert.assertEquals(to, mapper.map(from));

		removeFile(f);

	}
}
