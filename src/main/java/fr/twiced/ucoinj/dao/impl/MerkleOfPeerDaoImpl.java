package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.Peer;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.MerkleOfPeerDao;
import fr.twiced.ucoinj.dao.PeerDao;

@Repository
@Transactional
public class MerkleOfPeerDaoImpl extends GenericMultipleMerkleDaoImpl<Peer, KeyId> implements MerkleOfPeerDao {

	@Autowired
	private PeerDao peerDao;

	@Override
	public Peer getLeaf(String hash, KeyId natId) {
		return peerDao.getByKeyId(new KeyId(hash));
	}

	@Override
	public Peer getNew(String hash) {
		Peer p = new Peer();
		p.setFingerprint(hash);
		return p;
	}
}
