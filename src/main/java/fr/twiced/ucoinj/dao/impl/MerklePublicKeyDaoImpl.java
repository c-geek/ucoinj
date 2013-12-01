package fr.twiced.ucoinj.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import fr.twiced.ucoinj.UniqueMerkle;
import fr.twiced.ucoinj.bean.PublicKey;
import fr.twiced.ucoinj.dao.PublicKeyDao;

@Repository
@Transactional
public class MerklePublicKeyDaoImpl extends MerkleDaoImpl<PublicKey> {

	@Autowired
	private PublicKeyDao pubkeyDao;

	@Override
	public PublicKey getLeaf(String hash) {
		return pubkeyDao.getByFingerprint(hash);
	}

	@Override
	public String getName() {
		return UniqueMerkle.PUBLIC_KEY.name();
	}

}
