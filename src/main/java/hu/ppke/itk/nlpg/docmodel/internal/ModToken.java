package hu.ppke.itk.nlpg.docmodel.internal;

public class ModToken extends Token{

	protected String originalStem = null;
	
	public ModToken(String token, String originalStem, String stem, String tag) {
		super(token, stem, tag);
		this.originalStem = originalStem;
	}
	
	public String getOriginalStem() {
		return this.originalStem;
	}

}
