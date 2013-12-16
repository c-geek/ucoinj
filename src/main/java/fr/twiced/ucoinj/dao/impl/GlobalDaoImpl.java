package fr.twiced.ucoinj.dao.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Amendment;
import fr.twiced.ucoinj.bean.Transaction;
import fr.twiced.ucoinj.dao.AmendmentDao;
import fr.twiced.ucoinj.dao.GlobalDao;
import fr.twiced.ucoinj.dao.TransactionDao;

@Repository
@Transactional
public class GlobalDaoImpl implements GlobalDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private AmendmentDao amDao;
	
	@Autowired
	private TransactionDao txDao;

	protected Session getSession() {
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void removeAll() {
		getSession().createQuery("update from Merkle m set m.root = null").executeUpdate();
		getSession().createQuery("delete from Node").executeUpdate();
		getSession().createQuery("delete from Merkle").executeUpdate();
		getSession().createQuery("delete from Coin").executeUpdate();
		List<Transaction> txs = txDao.getAll();
		for (Transaction t : txs) {
			txDao.delete(t);
		}
		getSession().createQuery("delete from Vote").executeUpdate();
		List<Amendment> amendments = amDao.getAll();
		for (Amendment a : amendments) {
			amDao.delete(a);
		}
		getSession().createQuery("delete from PublicKey").executeUpdate();
		getSession().createQuery("delete from Signature").executeUpdate();
		getSession().createQuery("delete from Key").executeUpdate();
	}
}
