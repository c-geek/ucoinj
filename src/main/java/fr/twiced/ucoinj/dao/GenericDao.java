package fr.twiced.ucoinj.dao;

import java.util.List;


public interface GenericDao<E> {
	
	List<E> getAll();
	
	void removeAll();

	void save(E entity);
	
	void update(E entity);
	
	void delete(E entity);
}
