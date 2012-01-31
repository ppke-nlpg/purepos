package hu.ppke.itk.nlpg.purepos.model.internal;

public class IntVocabulary<W> extends Vocabulary<W, Integer> {

	@Override
	protected Integer addVocabularyElement(W element) {
		vocabulary.put(element, vocabulary.size());
		return vocabulary.get(element);
	}

	public static Integer getExtremalElement() {
		return -1;
	}

}
