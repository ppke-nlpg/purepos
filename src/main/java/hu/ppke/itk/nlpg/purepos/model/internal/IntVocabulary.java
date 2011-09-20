package hu.ppke.itk.nlpg.purepos.model.internal;

public class IntVocabulary<W> extends Vocabulary<W, Integer> {

	@Override
	public void addElement(W element) {
		vocabulary.put(element, vocabulary.size() + 1);

	}
}
