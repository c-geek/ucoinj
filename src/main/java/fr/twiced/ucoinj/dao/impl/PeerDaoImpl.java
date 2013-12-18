package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Peer;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.PeerDao;
import fr.twiced.ucoinj.service.MerkleService;

@Repository
@Transactional
public class PeerDaoImpl extends GenericDaoImpl<Peer> implements PeerDao {
	
	@Autowired
	private MerkleService merkleService;

	@Override
	public Peer getByKeyId(KeyId natId) {
		return (Peer) getSession().createQuery("from Peer p where p.fingerprint = :fpr")
				.setParameter("fpr", natId.getHash())
				.uniqueResult();
	}

	@Override
	protected String getEntityName() {
		return Peer.class.getName();
	}

}
