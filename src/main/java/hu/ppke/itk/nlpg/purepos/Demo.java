package hu.ppke.itk.nlpg.purepos;

import hu.ppke.itk.nlpg.purepos.cli.configuration.Configuration;
import hu.ppke.itk.nlpg.purepos.common.serializer.SSerializer;
import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.RawModel;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;
import hu.ppke.itk.nlpg.purepos.morphology.NullAnalyzer;

import java.io.File;
import java.io.PrintStream;
import java.util.Scanner;

public class Demo {

	public static void main(String[] args) throws Exception {
		String modelPath = args[0];
		double beamLogTheta = Math.log(1000);
		double suffixLogTheta = Math.log(10);
		int maxGuessed = 10;
		boolean useBeamSearch = false;
		IMorphologicalAnalyzer analyzer = new NullAnalyzer();

		String lemmaTransformationType = "suffix"; 	// default value from console input
		int lemmaThreshold = 2;						// default value from console input
		RawModel rawmodel = SSerializer.readModel(new File(modelPath));
		CompiledModel<String, Integer> model = rawmodel
				.compile(new Configuration(), lemmaTransformationType, lemmaThreshold);
		
		ITagger tagger = new MorphTagger(model, analyzer, beamLogTheta, suffixLogTheta,
				maxGuessed, useBeamSearch);
		
		Scanner inputScanner = new Scanner(System.in);
		PrintStream taggerOutput = new PrintStream(System.out, true);
		String inputFormat = "ord";
		String outputFormat = "ord";
		tagger.tag(inputScanner, inputFormat, taggerOutput, outputFormat);

	}

}
