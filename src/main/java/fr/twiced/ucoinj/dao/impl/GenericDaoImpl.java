package fr.twiced.ucoinj.dao.impl;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.dao.GenericDao;

@Repository
@Transactional
public class GenericDaoImpl<E> implements GenericDao<E> {

	@Autowired
	protected SessionFactory sessionFactory;

	@Override
	public void save(E entity) {
		getSession().save(entity);
	}
	
	protected Session getSession(){
		return sessionFactory.getCurrentSession();
	}

}
