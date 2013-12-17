package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Key;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.KeyDao;
import fr.twiced.ucoinj.exceptions.UnknownLeafException;
import fr.twiced.ucoinj.service.MerkleService;

@Repository
@Transactional
public class KeyDaoImpl extends GenericDaoImpl<Key> implements KeyDao {
	
	@Autowired
	private MerkleService merkleService;

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
	
	@Override
	public void save(Key entity) {
		updateManagedKeyMerkle(entity);
		super.save(entity);
	}

	@Override
	public void delete(Key entity) {
		super.delete(entity);
		entity.setManaged(false);
		updateManagedKeyMerkle(entity);
	}
	
	@Override
	public void update(Key entity) {
		updateManagedKeyMerkle(entity);
		super.update(entity);
	}
	
	private void updateManagedKeyMerkle(Key entity) {
		try {
			merkleService.searchManagedKey(false, entity.getFingerprint());
			if (!entity.getManaged()) {
				merkleService.removeManagedKey(entity);
			}
		} catch (UnknownLeafException e) {
			if (entity.getManaged()) {
				merkleService.putManagedKey(entity);
			}
		}
	}

}
