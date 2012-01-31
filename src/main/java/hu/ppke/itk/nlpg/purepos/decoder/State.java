package hu.ppke.itk.nlpg.purepos.decoder;

import java.util.ArrayList;
import java.util.List;

class State {
	public State(List<Integer> path, double weight) {
		this.path = new ArrayList<Integer>(path);

		this.weight = weight;
	}

	protected List<Integer> path = new ArrayList<Integer>();

	protected double weight = 0;

	public State createNext(Integer newStep, double plusWeight) {
		State s = new State(new ArrayList<Integer>(this.path), this.weight
				+ plusWeight);
		s.path.add(newStep);
		return s;
	}

	public List<Integer> getPath() {
		return path;
	}

	public double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return "{path: " + path + " weight: " + weight + "}";
	}
}