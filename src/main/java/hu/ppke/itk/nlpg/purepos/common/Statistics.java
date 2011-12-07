package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.model.Model;

public class Statistics {

	int sentences;
	int tokens;
	int lGuesserItems;
	int uGuesserItems;
	double theta;

	public Statistics() {
		sentences = 0;
		tokens = 0;
		lGuesserItems = 0;
		uGuesserItems = 0;
	}

	public void setTheta(double theta) {
		this.theta = theta;
	}

	public void incrementLowerGuesserItems(int num) {
		lGuesserItems += num;
	}

	public void incrementUpperGuesserItems(int num) {
		uGuesserItems += num;
	}

	public void incrementTokenCount() {
		tokens++;
	}

	public void incrementSentenceCount() {
		sentences++;
	}

	public String getStat(Model<?, ?> m) {
		String ret = "Training corpus:\n";
		ret += tokens + " tokens\n";
		ret += sentences + " sentences\n";
		ret += m.getTagVocabulary().size() + " different tag\n\n";

		ret += "Guesser trained with\n";
		ret += lGuesserItems + " lowercase\n";
		ret += uGuesserItems + " uppercase tokens\n";
		ret += "theta = " + theta;

		return ret;
	}
}
