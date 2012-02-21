package hu.ppke.itk.nlpg.purepos.morphology;

import hu.ppke.itk.nlpg.docmodel.IToken;
import hu.ppke.itk.nlpg.docmodel.internal.Token;

import java.util.ArrayList;
import java.util.List;

import com.morphologic.humor.JHumor;
import com.morphologic.humor.MorphAnalysis;

/***
 * Wrapper class around Humor for only getting POS tags
 * 
 * @author György Orosz
 * 
 */
public class HumorAnalyzer extends AbstractMorphologicalAnalyzer {
	private static IMorphologicalAnalyzer hInstance = null;
	private final JHumor humor;

	private HumorAnalyzer() {
		humor = new JHumor();
	}

	private HumorAnalyzer(String lexPath) {
		humor = new JHumor(lexPath);
	}

	@Override
	public List<String> getTags(String word) {
		return humor.getTags(word);
	}

	@Override
	public List<IToken> analyze(String word) {
		List<IToken> tokens = new ArrayList<IToken>();
		List<MorphAnalysis> anals = humor.getAnalysises(word);
		for (MorphAnalysis a : anals) {
			tokens.add(new Token(word, a.getStem(), a.getTag()));
		}
		return tokens;
	}

	public static IMorphologicalAnalyzer getInstance() {
		return HumorAnalyzer.getInstance(JHumor.LEX_PATH);
	}

	public static IMorphologicalAnalyzer getInstance(String lexPath) {
		if (hInstance == null)
			hInstance = new HumorAnalyzer(lexPath);
		return hInstance;
	}

}
