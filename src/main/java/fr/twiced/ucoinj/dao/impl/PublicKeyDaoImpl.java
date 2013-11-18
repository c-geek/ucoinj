package fr.twiced.ucoinj.dao.impl;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.dao.PublicKeyDao;

@Repository
@Transactional
public class PublicKeyDaoImpl implements PublicKeyDao {

	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public PublicKey getByFingerprint(String fpr) {
		return (PublicKey) sessionFactory.getCurrentSession().createQuery("from PublicKey p where p.fingerprint = :fpr").setString("fpr", fpr).uniqueResult();
	}

	@Override
	public void save(PublicKey pubkey) {
		sessionFactory.getCurrentSession().persist(pubkey);
	}

}
