package hu.ppke.itk.nlpg.purepos.common;

public class TAnalysisItem {
	protected String lemma, tag;
	protected Double prob;

	public TAnalysisItem(String lemma, String ana, Double prob) {
		this.lemma = lemma;
		this.tag = ana;
		this.prob = prob;
	}

	public static TAnalysisItem create(String lemma, String ana) {
		Double prob = 1.0;
		return new TAnalysisItem(lemma, ana, prob);
	}

	public String getLemma() {
		return lemma;
	}

	public String getTag() {
		return tag;
	}

	public Double getProb() {
		return prob;
	}
}
