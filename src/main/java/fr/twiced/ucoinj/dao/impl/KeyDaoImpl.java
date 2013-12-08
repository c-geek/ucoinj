package fr.twiced.ucoinj.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.KeyDao;

@Repository
@Transactional
public class KeyDaoImpl extends GenericDaoImpl<Key> implements KeyDao {

	@Override
	public boolean isManaged(KeyId natId) {
		Key k = (Key) getSession().createQuery("from Key k where k.fingerprint = :fpr")
				.setParameter("fpr", natId.getHash())
				.uniqueResult();
		return k != null && k.getManaged();
	}

	@Override
	public Key getByKeyId(KeyId natId) {
		return (Key) getSession().createQuery("from Key k where k.fingerprint = :fpr")
				.setParameter("fpr", natId.getHash())
				.uniqueResult();
	}

	@Override
	protected String getEntityName() {
		return Key.class.getName();
	}

}
