package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.dao.GenericDao;

@Repository
@Transactional
public abstract class GenericDaoImpl<E> implements GenericDao<E> {

	@Autowired
	protected SessionFactory sessionFactory;
	
	protected Session getSession(){
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void save(E entity) {
		getSession().save(entity);
		getSession().flush();
	}

	@Override
	public void update(E entity) {
		getSession().update(entity);
		getSession().flush();
	}

	@Override
	public void delete(E entity) {
		getSession().delete(entity);
		getSession().flush();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> getAll() {
		return getSession().createQuery("from " + getEntityName()).list();
	}

	protected abstract String getEntityName();
}
