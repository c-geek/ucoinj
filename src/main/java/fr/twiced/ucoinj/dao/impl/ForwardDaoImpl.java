package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Forward;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.ForwardDao;

@Repository
@Transactional
public class ForwardDaoImpl extends GenericDaoImpl<Forward> implements ForwardDao {
	
	@Override
	public Forward getByKeyIds(KeyId from, KeyId to) {
		return (Forward) getSession().createQuery("from Forward f where f.from = :from and f.to = :to")
				.setParameter("from", from.getHash())
				.setParameter("to", to.getHash())
				.uniqueResult();
	}

	@Override
	protected String getEntityName() {
		return Forward.class.getName();
	}

	@Override
	public Forward getByHash(String hash) {
		return (Forward) getSession().createQuery("from Forward f where f.hash = :hash")
				.setParameter("hash", hash)
				.uniqueResult();
	}

}
