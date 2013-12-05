package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.bean.id.KeyId;
import fr.twiced.ucoinj.dao.MerkleOfPublicKeyDao;
import fr.twiced.ucoinj.dao.PublicKeyDao;

@Repository
@Transactional
public class MerkleOfPublicKeyDaoImpl extends GenericMultipleMerkleDaoImpl<PublicKey, KeyId> implements MerkleOfPublicKeyDao {

	@Autowired
	private PublicKeyDao pubkeyDao;

	@Override
	public PublicKey getLeaf(String hash, KeyId natId) {
		return pubkeyDao.getByFingerprint(hash);
	}

	@Override
	public PublicKey getNew(String hash) {
		PublicKey pk = new PublicKey();
		pk.setFingerprint(hash);
		return pk;
	}
}
