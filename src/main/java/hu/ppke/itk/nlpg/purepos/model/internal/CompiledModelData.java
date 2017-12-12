package hu.ppke.itk.nlpg.purepos.model.internal;

import hu.ppke.itk.nlpg.purepos.common.Util;
import hu.ppke.itk.nlpg.purepos.common.lemma.AbstractLemmaTransformation;
import hu.ppke.itk.nlpg.purepos.common.lemma.Transformation;
import hu.ppke.itk.nlpg.purepos.model.ICombiner;
import hu.ppke.itk.nlpg.purepos.model.ISuffixGuesser;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.PrintStream;
import java.util.Map;

public class CompiledModelData<W, T extends Comparable<T>> {
	public LemmaUnigramModel<W> unigramLemmaModel;
	public ISuffixGuesser<W, AbstractLemmaTransformation<Pair<W,Integer>>> lemmaGuesser;
	public ISuffixGuesser<String, String> suffixLemmaModel;
	public ICombiner combiner;

	public ProbModel<T> tagTransitionModel;
	public ProbModel<W> standardEmissionModel;
	public ProbModel<W> specTokensEmissionModel;
	public ISuffixGuesser<W, T> lowerCaseSuffixGuesser;
	public ISuffixGuesser<W, T> upperCaseSuffixGuesser;
	public Map<T, Double> aprioriTagProbs;

	@Deprecated
	public CompiledModelData(LemmaUnigramModel<W> unigramLemmaModel,
			ICombiner combiner, ProbModel<T> tagTransitionModel,
							 ProbModel<W> standardEmissionModel,
							 ProbModel<W> specTokensEmissionModel,
			ISuffixGuesser<W, T> lowerCaseSuffixGuesser,
			ISuffixGuesser<W, T> upperCaseSuffixGuesser,
			Map<T, Double> aprioriTagProbs,
			ISuffixGuesser<W, AbstractLemmaTransformation<Pair<W,Integer>>> lemmaTree) {
		this.unigramLemmaModel = unigramLemmaModel;
		this.combiner = combiner;
		this.tagTransitionModel = tagTransitionModel;
		this.standardEmissionModel = standardEmissionModel;
		this.specTokensEmissionModel = specTokensEmissionModel;
		this.lowerCaseSuffixGuesser = lowerCaseSuffixGuesser;
		this.upperCaseSuffixGuesser = upperCaseSuffixGuesser;
		this.aprioriTagProbs = aprioriTagProbs;
		this.lemmaGuesser = lemmaTree;
	}

	// @Deprecated
	CompiledModelData() {

	}

	public void print(PrintStream ps, RawModelData rawModelData, boolean dot) throws Exception{
		//HashSuffixTree -> HashSuffixGuesser
		this.lemmaGuesser.print(ps ,"lemmaGuesser","transformation");
		ps.println();
		this.lowerCaseSuffixGuesser.print(ps, "lowerCaseSuffixGuesser","tag");
		ps.println();
		this.upperCaseSuffixGuesser.print(ps, "upperCaseSuffixGuesser","tag");
		ps.println();
		this.suffixLemmaModel.print(ps, "suffixLemmaModel","POStag");
		ps.println();
		// LemmaUnigramModel
		this.unigramLemmaModel.print(ps,"unigramLemmaModel");
		ps.println();
		// others
		printaprioriTagProbs(ps,this.aprioriTagProbs,rawModelData.tagNGramModel.getTotalFrequency(),
				rawModelData.tagNGramModel.getWords(), "aprioriTagProbs");
		ps.println();
		this.combiner.print(ps,"combiner");
		ps.println();
		// ProbModel / NGramModel
		rawModelData.tagNGramModel.print(ps,this.tagTransitionModel.getNodes(), dot,"tagTransitionModel","tag");
		ps.println();
		rawModelData.stdEmissionNGramModel.print(ps,this.standardEmissionModel.getNodes(), dot,"standardEmissionModel","");
		ps.println();
		rawModelData.specEmissionNGramModel.print(ps,this.specTokensEmissionModel.getNodes(), dot,"specTokensEmissionModel","");
	}

	public void print_separated (RawModelData rawModelData, boolean dot) throws Exception{
		String encoding = System.getProperty("file.encoding");
		//HashSuffixTree -> HashSuffixGuesser
		PrintStream _lemmaGuesser = new PrintStream(new File("lemmaGuesser.txt"),encoding);
		PrintStream _lowerCaseSuffixGuesser = new PrintStream(new File("lowerCaseSuffixGuesser.txt"), encoding);
		PrintStream _upperCaseSuffixGuesser = new PrintStream(new File("upperCaseSuffixGuesser.txt"), encoding);
		PrintStream	_suffixLemmaModel = new PrintStream(new File("suffixLemmaModel.txt"), encoding);
		//LemmaUnigramModel
		PrintStream _unigramLemmaModel = new PrintStream(new File("unigramLemmaModel.txt"),encoding);
		// others
		PrintStream _aprioriTagProbs = new PrintStream(new File("aprioriTagProbs.txt"),encoding);
		PrintStream _combiner = new PrintStream(new File("combiner.txt"),encoding);
		// ProbModel
		//tagTransitionModel
		PrintStream _tagTransitionModel = new PrintStream(new File("tagTransitionModel.txt"), encoding);
		//standardEmissionModel
		PrintStream _standardEmissionModel = new PrintStream(new File("standardEmissionModel.txt"), encoding);
		//specTokensEmissionModel
		PrintStream _specTokensEmissionModel = new PrintStream(new File("specTokensEmissionModel.txt"), encoding);

		//HashSuffixTree -> HashSuffixGuesser
		this.lemmaGuesser.print(_lemmaGuesser ,"lemmaGuesser","transformation");
		this.lowerCaseSuffixGuesser.print(_lowerCaseSuffixGuesser, "lowerCaseSuffixGuesser","tag");
		this.upperCaseSuffixGuesser.print(_upperCaseSuffixGuesser, "upperCaseSuffixGuesser","tag");
		this.suffixLemmaModel.print(_suffixLemmaModel, "suffixLemmaModel","POStag");
		// LemmaUnigramModel
		this.unigramLemmaModel.print(_unigramLemmaModel,"unigramLemmaModel");
		// others
		printaprioriTagProbs(_aprioriTagProbs,this.aprioriTagProbs, rawModelData.tagNGramModel.getTotalFrequency(),
				rawModelData.tagNGramModel.getWords(),"aprioriTagProbs");
		this.combiner.print(_combiner,"combiner");
		// ProbModel / NGramModel
		rawModelData.tagNGramModel.print(_tagTransitionModel,this.tagTransitionModel.getNodes(),
				dot,"tagTransitionModel","tag");
		rawModelData.stdEmissionNGramModel.print(_standardEmissionModel,this.standardEmissionModel.getNodes(),
				dot,"standardEmissionModel","");
		rawModelData.specEmissionNGramModel.print(_specTokensEmissionModel,this.specTokensEmissionModel.getNodes(),
				dot,"specTokensEmissionModel","");
	}

	protected void printaprioriTagProbs(PrintStream ps,Map<T,Double> tagProbs ,
										int sum_freq, Map<Integer, Integer> freqs,String name){
		ps.println(name + " sum_freq: " + sum_freq +" {");
		for (Map.Entry<T,Double> entry : tagProbs.entrySet()) {
			ps.println("\t"+ Transformation.decodeTag(entry.getKey().toString(), Util.tagVocabulary) +" : "+ freqs.get(entry.getKey()) +" : " + Util.formatDecimal(entry.getValue()));
		}
		ps.println("}");
	}
}