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
	protected SessionFactory sessionFactory;
	
	@Override
	public PublicKey getByFingerprint(String fpr) {
		return (PublicKey) sessionFactory.getCurrentSession().createQuery("from PublicKey p where p.fingerprint = :fpr").setString("fpr", fpr).uniqueResult();
	}

	@Override
	public PublicKey getByKeyID(String keyID) {
		return (PublicKey) sessionFactory.getCurrentSession().createQuery("from PublicKey p where p.fingerprint like :fpr").setString("fpr", "%" + keyID.toUpperCase()).uniqueResult();
	}

	@Override
	public void save(PublicKey entity) {
		sessionFactory.getCurrentSession().save(entity);
	}

}
