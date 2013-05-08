package hu.ppke.itk.nlpg.purepos.common.lemma;

import junit.framework.Assert;

import org.junit.Test;

public class LemmaUtilTest {
	@Test
	public void mainTagTest() {
		String tag, mainTag;
		tag = "[V.Subj.S3.Def]";
		// tag = "[X]";
		//
		// Pattern mainPosPat = Pattern.compile("\\[([^.])[.\\]]");
		// Matcher matcher = mainPosPat.matcher(tag);
		// System.err.println(matcher.find());
		// System.err.println(matcher.group(1));

		mainTag = "V";
		Assert.assertEquals(mainTag, LemmaUtil.mainPosTag(tag));

		tag = "[X]";
		mainTag = "X";
		Assert.assertEquals(mainTag, LemmaUtil.mainPosTag(tag));

		tag = "[Adv]";
		mainTag = "Adv";
		Assert.assertEquals(mainTag, LemmaUtil.mainPosTag(tag));
	}
}
