package fr.twiced.ucoinj.dao;


public interface GenericDao<E> {

	void save(E entity);
	
	void update(E entity);
	
	void delete(E entity);
}
