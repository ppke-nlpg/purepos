package hu.ppke.itk.nlpg.purepos.model;

import java.util.List;

public interface IMapper<T> {
	
	public T map(T element);

	public List<T> map(List<T> elements);

}
