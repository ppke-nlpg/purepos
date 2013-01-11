package hu.ppke.itk.nlpg.purepos.common;

import hu.ppke.itk.nlpg.purepos.model.IMapper;
import hu.ppke.itk.nlpg.purepos.model.IProbabilityModel;

import java.util.List;
import java.util.Map;

public class OneWordLexicalModel implements IProbabilityModel<Integer, String> {

	protected Map<Integer, Double> probs;
	protected String word;

	public OneWordLexicalModel(Map<Integer, Double> probs, String word) {
		this.probs = probs;
		this.word = word;
	}

	protected IMapper<String> elementMapper = null;
	protected IMapper<Integer> contextMapper = null;

	@Override
	public void setElementMapper(IMapper<String> mapper) {
		this.elementMapper = mapper;
	}

	@Override
	public void setContextMapper(IMapper<Integer> mapper) {
		this.contextMapper = mapper;
	}

	@Override
	public Double getProb(List<Integer> context, String word) {
		if (elementMapper != null) {
			word = elementMapper.map(word);
		}
		if (contextMapper != null) {
			context = contextMapper.map(context);
		}

		Integer tag = context.get(context.size() - 1);
		if (word.equals(this.word) && probs.containsKey(tag)) {
			return probs.get(tag);
		}
		return 0.0;
	}

	@Override
	public Double getLogProb(List<Integer> context, String word) {
		Double prob = getProb(context, word);
		double lp = Math.log(prob);
		return lp;
	}

	@Override
	public IMapper<Integer> getContextMapper() {
		return contextMapper;
	}

}
