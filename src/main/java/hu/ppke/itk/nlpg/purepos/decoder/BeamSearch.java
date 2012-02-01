package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.model.Model;
import hu.ppke.itk.nlpg.purepos.model.internal.NGram;
import hu.ppke.itp.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.List;

public class BeamSearch extends FastDecoder {

	public BeamSearch(Model<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, double logTheta,
			int maxGuessedTags) {
		super(model, morphologicalAnalyzer, logTheta, maxGuessedTags);
	}

	@Override
	public List<Integer> decode(List<String> observations) {
		List<String> obs = new ArrayList<String>(observations);

		obs.add(Model.getEOSToken()); // adds 1 EOS marker as in HunPos
		int n = model.getTaggingOrder();

		ArrayList<Integer> startTags = new ArrayList<Integer>();
		for (int j = 0; j <= n; ++j) {
			startTags.add(model.getBOSIndex());
		}
		// TODO: is it 2?
		NGram<Integer> startNGram = new NGram<Integer>(startTags,
				model.getTaggingOrder());

		NGram<Integer> best = beamSearch(startNGram);
		return best.toList();

	}

	public NGram<Integer> beamSearch(NGram<Integer> start) {
		return beamSearch(start, 1);

	}

	public NGram<Integer> beamSearch(NGram<Integer> start, int numberOfValues) {
		return null;

	}
}
