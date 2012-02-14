package hu.ppke.itk.nlpg.purepos.decoder;

import hu.ppke.itk.nlpg.purepos.model.internal.NGram;

class Node implements Comparable<Node> {

	// public Node(List<Integer> state, double weight) {
	// this.state = new NGram<Integer>(state);
	// this.weight = weight;
	// }

	public Node(NGram<Integer> state, double weight, Node previous) {
		this.state = state;
		this.weight = weight;
		this.prev = previous;
	}

	protected NGram<Integer> state;// = new ArrayList<Integer>();

	protected double weight = 0;

	protected Node prev;

	// public Node createNext(Integer newStep, double plusWeight) {
	// Node s = new Node(state.add(newStep), this.weight + plusWeight);
	//
	// return s;
	// }

	public NGram<Integer> getState() {
		return state;
	}

	public void setWeight(double w) {
		weight = w;
	}

	public void setPrevious(Node previous) {
		this.prev = previous;
	}

	public double getWeight() {
		return weight;
	}

	public Node getPrevious() {
		return prev;
	}

	@Override
	public String toString() {
		return "{state: " + state + " weight: " + weight + "}";
	}

	@Override
	public int compareTo(Node o) {
		return Double.compare(weight, o.weight);
	}
}