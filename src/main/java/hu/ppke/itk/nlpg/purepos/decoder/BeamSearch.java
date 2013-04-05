package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.model.internal.CompiledModel;
import hu.ppke.itk.nlpg.purepos.model.internal.History;
import hu.ppke.itk.nlpg.purepos.model.internal.NGram;
import hu.ppke.itk.nlpg.purepos.morphology.IMorphologicalAnalyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.MinMaxPriorityQueue;

public class BeamSearch extends AbstractDecoder {
	protected Integer beamSize = 10;
	protected boolean fixedBeam = true;

	public BeamSearch(CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, double logTheta,
			double sufTheta, int maxGuessedTags) {
		super(model, morphologicalAnalyzer, logTheta, sufTheta, maxGuessedTags);
		fixedBeam = false;
	}

	public BeamSearch(CompiledModel<String, Integer> model,
			IMorphologicalAnalyzer morphologicalAnalyzer, int beamSize,
			double sufTheta, int maxGuessedTags) {
		super(model, morphologicalAnalyzer, 0, sufTheta, maxGuessedTags);
		this.beamSize = beamSize;
		fixedBeam = true;
	}

	@Override
	public List<Pair<List<Integer>, Double>> decode(List<String> observations,
			int maxResultsNumber) {
		observations = prepareObservations(observations);
		MinMaxPriorityQueue<History> beam = beamSearch(observations);
		return getKTop(beam, maxResultsNumber);
	}

	private List<Pair<List<Integer>, Double>> getKTop(
			MinMaxPriorityQueue<History> beam, int maxResultsNumber) {
		List<Pair<List<Integer>, Double>> ret = new ArrayList<Pair<List<Integer>, Double>>();
		int n = Math.min(maxResultsNumber, beam.size());
		for (int i = 0; i < n; ++i) {
			History lastElement = beam.removeLast();
			List<Integer> tagSeq = lastElement.getTagSeq().toList();
			List<Integer> cleaned = clean(tagSeq);
			ret.add(Pair.of(cleaned, lastElement.getLogProb()));
		}
		return ret;
	}

	private List<Integer> clean(List<Integer> tagSeq) {
		return tagSeq.subList(model.getTaggingOrder(), tagSeq.size() - 1);
	}

	private MinMaxPriorityQueue<History> beamSearch(List<String> observations) {
		MinMaxPriorityQueue<History> beam = initBeam();
		int position = 0;
		for (String word : observations) {
			Set<NGram<Integer>> contexts = collectContexts(beam);
			Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> probs = getNextProbs(
					contexts, word, position, position == 0);
			// try {
			beam = updateBeam(beam, probs);
			prune(beam);
			// } catch (Exception e) {
			// e.printStackTrace();
			// }

		}
		position++;

		return beam;
	}

	private Set<NGram<Integer>> collectContexts(
			MinMaxPriorityQueue<History> beam) {
		Set<NGram<Integer>> ret = new HashSet<NGram<Integer>>();
		for (History h : beam) {
			ret.add(h.getTagSeq());
		}

		return ret;
	}

	private MinMaxPriorityQueue<History> updateBeam(
			MinMaxPriorityQueue<History> beam,
			Map<NGram<Integer>, Map<Integer, Pair<Double, Double>>> probs) {
		MinMaxPriorityQueue<History> newBeam = MinMaxPriorityQueue.create();

		for (History h : beam) {
			NGram<Integer> context = h.getTagSeq();
			Double oldProb = h.getLogProb();

			Map<Integer, Pair<Double, Double>> transitions = probs.get(context);

			for (Map.Entry<Integer, Pair<Double, Double>> nexts : transitions
					.entrySet()) {
				Integer nextTag = nexts.getKey();
				Pair<Double, Double> probVals = nexts.getValue();

				NGram<Integer> newSeq = context.add(nextTag);
				Double newProb = oldProb + probVals.getLeft()
						+ probVals.getRight();

				newBeam.add(new History(newSeq, newProb));
			}
		}
		return newBeam;
	}

	private void prune(MinMaxPriorityQueue<History> beam) {
		if (fixedBeam) {
			while (beam.size() > this.beamSize) {
				beam.removeFirst();
			}
		} else {
			History max = beam.peekLast();
			while (!(beam.peekFirst().getLogProb() > max.getLogProb()
					- logTheta)) {
				beam.removeFirst();
			}
		}
	}

	private MinMaxPriorityQueue<History> initBeam() {
		MinMaxPriorityQueue<History> beam = MinMaxPriorityQueue.create();
		NGram<Integer> initNGram = createInitialElement();
		beam.add(new History(initNGram, 0.0));
		return beam;
	}

	protected NGram<Integer> createInitialElement() {
		int n = model.getTaggingOrder() - 1;

		ArrayList<Integer> startTags = new ArrayList<Integer>();
		for (int j = 0; j <= n; ++j) {
			startTags.add(model.getBOSIndex());
		}

		NGram<Integer> startNGram = new NGram<Integer>(startTags,
				model.getTaggingOrder());
		return startNGram;
	}
}
